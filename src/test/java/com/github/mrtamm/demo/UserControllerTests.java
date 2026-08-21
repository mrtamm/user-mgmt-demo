package com.github.mrtamm.demo;

import com.github.mrtamm.demo.controller.UserController;
import com.github.mrtamm.demo.json.UserEdit;
import com.github.mrtamm.demo.json.UserView;
import com.github.mrtamm.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Integration tests against the {@link UserController} using {@code restTestClient} to simulate API
 * requests.
 */
@WebMvcTest(UserController.class)
@AutoConfigureRestTestClient
public class UserControllerTests {

  @MockitoBean
  private UserService userService;

  @Autowired
  private RestTestClient restTestClient;

  @Test
  public void shouldReturnUsers() {
    // Prepare test-data for the endpoint:
    List<UserView> testUsers = List.of(
        new UserView(UUID.randomUUID(), "Mike", "Watson", "mike.watson@example.org"),
        new UserView(UUID.randomUUID(), "Michael", "Watt", "michael.watt@example.org"),
        new UserView(UUID.randomUUID(), "Michelle", "Walton", "michelle.walton@example.org")
    );

    when(userService.findAll()).thenReturn(testUsers);

    // Invoke the endpoint:
    restTestClient.get().uri("/api/v1/users")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(new ParameterizedTypeReference<List<UserView>>() {})
        .isEqualTo(testUsers);

    // Verify the service call:
    verify(userService).findAll();
  }

  @Test
  public void shouldAddUser() {
    // Prepare test-data for the endpoint:
    UserEdit testUserData = new UserEdit("Mike", "Watson", "mike.watson@example.org");
    UserView result = new UserView(UUID.randomUUID(), "Mike", "Watson", "mike.watson@example.org");

    when(userService.add(testUserData)).thenReturn(result);

    // Invoke the endpoint:
    restTestClient.post().uri("/api/v1/users")
        .body(testUserData)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserView.class)
        .isEqualTo(result);

    // Verify the service call:
    verify(userService).add(testUserData);
  }

  @Test
  public void shouldUpdateUser() {
    // Prepare test-data for the endpoint:
    UUID userId = UUID.randomUUID();
    UserEdit testUserData = new UserEdit("Mike", "Watson", "mike.watson@example.org");
    UserView result = new UserView(userId, "Mike", "Watson", "mike.watson@example.org");

    when(userService.update(userId, testUserData)).thenReturn(result);

    // Invoke the endpoint:
    restTestClient.post().uri("/api/v1/users/" + userId)
        .body(testUserData)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserView.class)
        .isEqualTo(result);

    // Verify service call:
    verify(userService).update(userId, testUserData);
  }

}
