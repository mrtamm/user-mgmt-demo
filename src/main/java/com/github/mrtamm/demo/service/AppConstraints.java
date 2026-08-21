package com.github.mrtamm.demo.service;

/**
 * Exception type for app-specific business constraint violations and related failures.
 *
 * <p>Use subtypes to throw exceptions depending on the use-case. These errors are transformed to
 * the Problem-Details format by the {@code ExceptionsToApiResponse} class.
 */
public abstract class AppConstraints extends RuntimeException {

  protected AppConstraints(String message) {
    super(message);
  }

  /**
   * When a request refers to a non-existing record.
   */
  public static final class NotFound extends AppConstraints {

    public NotFound(String message) {
      super(message);
    }

  }

  /**
   * When a request provides an invalid value for some field.
   * The value may satisfy basic validation constraints but has an issue with business constraints.
   */
  public static final class BadInput extends AppConstraints {

    /**
     * Optional field-name that this error refers to.
     */
    private final String fieldName;

    public BadInput(String message, String fieldName) {
      super(message);
      this.fieldName = fieldName;
    }

    public String getFieldName() {
      return fieldName;
    }

  }

  /**
   * When the server-side has to fail an operation due to unsatisfied technical condition.
   */
  public static final class ServiceError extends AppConstraints {

    public ServiceError(String message) {
      super(message);
    }

  }

}
