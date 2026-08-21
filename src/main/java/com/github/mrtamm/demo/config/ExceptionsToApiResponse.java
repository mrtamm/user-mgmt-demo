package com.github.mrtamm.demo.config;

import com.github.mrtamm.demo.service.AppConstraints;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines API response formats (HTTP status with Problem-Details body) for certain exceptions.
 */
@RestControllerAdvice
public class ExceptionsToApiResponse extends ResponseEntityExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ExceptionsToApiResponse.class);

  @ExceptionHandler(AppConstraints.class)
  public ResponseEntity<Object> handleAppConstraints(
      AppConstraints ex, WebRequest request) {

    HttpStatus status = switch (ex) {
      case AppConstraints.NotFound ignored -> HttpStatus.NOT_FOUND;
      case AppConstraints.BadInput ignored -> HttpStatus.BAD_REQUEST;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };

    String title = switch (ex) {
      case AppConstraints.NotFound ignored -> "Problem with the URL";
      case AppConstraints.BadInput ignored -> "Problem with the submitted data";
      default -> "Problem in the service";
    };

    ProblemDetail problem = ProblemDetail.forStatus(status);
    problem.setTitle(title);
    problem.setDetail(ex.getMessage());

    if (ex instanceof AppConstraints.BadInput badInput) {
      if (badInput.getFieldName() != null) {
        problem.setProperty("errors", Map.of(badInput.getFieldName(), ex.getMessage()));
        problem.setDetail("Validation failed for the request body");
      }
    }

    return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
  }

  @ExceptionHandler
  public ResponseEntity<Object> handleInternalError(RuntimeException ex, WebRequest request) {
    log.warn("Detected internal error.", ex);

    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problem.setTitle("Problem with handling the request");
    //problem.setDetail(ex.getMessage()); // In production, don't expose this message.

    return handleExceptionInternal(
        ex, problem, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @Override
  protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request
  ) {
    // Process field validation errors where there can be more than one error per field.
    // First, group all errors per field.
    // Next, replace list of errors (per field) with the first string.
    Map<String, Object> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .filter(err -> err.getDefaultMessage() != null)
        .collect(Collectors.groupingBy(
            FieldError::getField,
            Collectors.mapping(
                DefaultMessageSourceResolvable::getDefaultMessage,
                Collectors.toList())
        ))
        .entrySet()
        .stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> entry.getValue().stream().sorted().findFirst()
        ));

    ProblemDetail problem = ProblemDetail.forStatus(status);
    problem.setTitle("Problem with the submitted data");
    problem.setDetail("Validation failed for the request body");
    problem.setProperty("errors", errors);

    return handleExceptionInternal(ex, problem, headers, status, request);
  }

}
