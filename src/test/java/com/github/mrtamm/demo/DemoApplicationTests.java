package com.github.mrtamm.demo;

import com.github.mrtamm.demo.json.UserEdit;
import com.github.mrtamm.demo.json.UserView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.client.RestTestClient.BodyContentSpec;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

/**
 * Integration tests against the {@code UserController} using {@code restTestClient} to simulate API
 * requests. These test use the full application solution, including the H2 database.
 *
 * <p>For keeping a clean state, the APP_USER table is truncated after each test-case.
 */
@SpringBootTest
@AutoConfigureRestTestClient
@Sql(statements = "DELETE FROM app_user", executionPhase = AFTER_TEST_METHOD)
class DemoApplicationTests {

  @Autowired
  private RestTestClient restTestClient;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Verifies the positive flow: listing, adding, and updating users.
   */
  @Test
  void shouldAddAndUpdateUsers() {
    // 1. Check that initially there are no users.
    checkUserList(List.of());

    // 2. Add first user:
    var userMike = addUser("Mike", "Watson", "mike.watson@example.org");
    checkUserList(List.of(userMike));

    // 3. Update all fields of the first user:
    var userMichael = updateUser(userMike.id(), "Michael", "Watt", "michael.watt@example.org");
    checkUserList(List.of(userMichael));

    // 4. Add second user and verify that the list is also updated:
    var userMichelle = addUser("Michelle", "Walton", "michelle.walton@example.org");
    checkUserList(List.of(userMichael, userMichelle));
  }

  /**
   * Verifies field validation: empty string and null values for mandatory fields.
   */
  @Test
  void shouldRejectEmptyFields() {
    addUserFail("", null, null)
        .jsonPath("$.errors.firstName").isEqualTo("must not be blank")
        .jsonPath("$.errors.lastName").isEqualTo("must not be blank")
        .jsonPath("$.errors.email").isEqualTo("must not be null");
  }

  /**
   * Verifies field validation: strings with spaces for mandatory fields.
   */
  @Test
  void shouldRejectBlankFields() {
    addUserFail("   ", "   ", "   ")
        .jsonPath("$.errors.firstName").isEqualTo("must not be blank")
        .jsonPath("$.errors.lastName").isEqualTo("must not be blank")
        .jsonPath("$.errors.email").isEqualTo("must be a well-formed email address");
  }

  /**
   * Verifies field validation: too long string values.
   */
  @Test
  void shouldRejectTooLongValues() {
    addUserFail("A".repeat(101), "B".repeat(101), "a".repeat(64) + "@test.test.test.test.test.test.services")
        .jsonPath("$.errors.firstName").isEqualTo("size must be between 1 and 100")
        .jsonPath("$.errors.lastName").isEqualTo("size must be between 1 and 100")
        .jsonPath("$.errors.email").isEqualTo("size must be between 6 and 100");
  }

  /**
   * Verifies field validation: invalid email addresses.
   */
  @Test
  void shouldRejectInvalidEmail() {
    List<String> invalidEmails = List.of("abcdefg", "abc@def", "abcdef@example.");

    for (String invalidEmail : invalidEmails) {
      addUserFail("A", "B", invalidEmail)
          .jsonPath("$.errors.email").isEqualTo("must be a well-formed email address");
    }
  }

  /**
   * Verifies the duplicate email check.
   */
  @Test
  void shouldRejectDuplicateEmail() {
    addUser("Duplicate", "Email1", "duplicate@test.org");
    addUserFail("Duplicate", "Email2", "duplicate@test.org")
        .jsonPath("$.errors.email").isEqualTo("Please check if the user is already registered as "
            + "the email is already in use");
  }

  /**
   * Verifies the failure to edit a user that does not exist.
   */
  @Test
  void shouldReceiveNotFound() {
    restTestClient.post().uri("/api/v1/users/{id}", UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON)
        .body(new UserEdit("A", "B", "info@example.org"))
        .exchange()
        .expectStatus().isNotFound()
        .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .expectBody()
        .jsonPath("$.title").isEqualTo("Problem with the URL")
        .jsonPath("$.detail").isEqualTo("The referenced user was not found");
  }
  /**
   * API call for retrieving the list of users. Verifies that it matches the provided list users.
   *
   * @param users Expected users. Can be an empty array.
   */
  private void checkUserList(List<UserView> users) {
    restTestClient.get().uri("/api/v1/users")
        .exchange()
        .expectStatus().isOk()
        .expectBody().json(objectMapper.writeValueAsString(users));
  }

  /**
   * API call for adding a user. Also verifies the returned user data.
   */
  private UserView addUser(String firstName, String lastName, String email) {
    UserEdit input = new UserEdit(firstName, lastName, email);

    return restTestClient.post().uri("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .body(input)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserView.class)
        .value(it -> verify(it, null, input))
        .returnResult()
        .getResponseBody();
  }

  /**
   * API call for adding a user – for cases where a validation error is expected.
   */
  private BodyContentSpec addUserFail(String firstName, String lastName, String email) {
    UserEdit input = new UserEdit(firstName, lastName, email);

    return restTestClient.post().uri("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .body(input)
        .exchange()
        .expectStatus().isBadRequest()
        .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .expectBody()
        .jsonPath("$.title").isEqualTo("Problem with the submitted data")
        .jsonPath("$.detail").isEqualTo("Validation failed for the request body");
  }

  /**
   * API call for updating a user. Also verifies the returned user data.
   */
  private UserView updateUser(UUID id, String firstName, String lastName, String email) {
    UserEdit input = new UserEdit(firstName, lastName, email);

    return restTestClient.post().uri("/api/v1/users/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(input)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserView.class)
        .value(it -> verify(it, id, input))
        .returnResult()
        .getResponseBody();
  }

  private void verify(UserView given, UUID expectedId, UserEdit expected) {
    assertThat(given).isNotNull();

    if (expectedId == null) {
      assertThat(given.id()).isNotNull();
    } else {
      assertThat(given.id()).isEqualTo(expectedId);
    }

    assertThat(given.firstName()).isEqualTo(expected.firstName());
    assertThat(given.lastName()).isEqualTo(expected.lastName());
    assertThat(given.email()).isEqualTo(expected.email());
  }

}
