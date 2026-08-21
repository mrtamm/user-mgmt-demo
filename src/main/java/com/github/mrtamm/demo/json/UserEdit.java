package com.github.mrtamm.demo.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * User entity type used in JSON requests for adding a new or updating an existing user.
 *
 * @param firstName Mandatory first name of the user.
 * @param lastName Mandatory last name of the user.
 * @param email Mandatory user's email address.
 */
public record UserEdit(
    @Schema(description = "First name of the person", example = "John")
    @NotBlank @Size(min = 1, max = 100)
    String firstName,

    @Schema(description = "Person's last name", example = "Smith")
    @NotBlank @Size(min = 1, max = 100)
    String lastName,

    @Schema(
        description = "Person's email (must be unique in the system)",
        example = "john.smith@example.org"
    )
    @NotNull @Size(min = 6, max = 100)
    @Email(regexp = ".*@[a-z0-9.-]+\\.[a-z]{2,15}")
    String email
) {}
