package com.github.mrtamm.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Provides general purpose logging for REST API requests.
 *
 * <p>An instance is automatically created, picked up, and registered by the Spring framework.
 */
@Component
public class RequestResponseLogger extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    long start = System.nanoTime();
    filterChain.doFilter(request, response);

    long durationMs = (System.nanoTime() - start) / 1_000_000;

    StringBuilder sb = new StringBuilder(100);
    sb.append("Processed [")
        .append(request.getMethod())
        .append(' ')
        .append(request.getRequestURI());

    if (request.getQueryString() != null) {
      sb.append('?').append(request.getQueryString());
    }

    sb.append("]: HTTP ")
        .append(response.getStatus())
        .append(" | ")
        .append(durationMs)
        .append("ms");
    logger.info(sb.toString());
  }

}
