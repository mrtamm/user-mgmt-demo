package com.github.mrtamm.demo;

import com.github.mrtamm.demo.json.UserEdit;
import com.github.mrtamm.demo.json.UserView;
import com.github.mrtamm.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers {@link UserService} methods while using H2 database for storing the data.
 */
@SpringBootTest
public class UserServiceTests {

  @Autowired
  private UserService userService;

  /**
   * Verifies UserService methods by adding new and updating existing users, and fetching users from
   * the database. Initially starting with an empty table.
   *
   * <p>These tests use the actual in-memory H2 database.
   */
  @Test
  public void shouldIncrementallyAddAndUpdateUsers() {
    // Test data:
    UserEdit user1Data = new UserEdit("John", "Doe", "john@test.org");
    UserEdit user2Data = new UserEdit("Mike", "Watson", "mike.watson@example.org");
    UserEdit user2DataUpdate = new UserEdit("Michelle", "Walton", "michelle.walton@example.org");

    // 0. Initially expecting no users:
    assertThat(userService.findAll()).isEmpty();

    // 1. Add first user, verify that the returned properties are correct:
    UserView user1 = userService.add(user1Data);
    verifyNewUser(user1, user1Data);

    // Verify that the list of all users just contains this one:
    assertThat(userService.findAll()).isEqualTo(List.of(user1));

    // 2. Add second user, verify that the returned properties are correct:
    UserView user2 = userService.add(user2Data);
    verifyNewUser(user2, user2Data);

    // Verify that the list of all users just contains the 2 users:
    assertThat(userService.findAll()).isEqualTo(List.of(user1, user2));

    // 3. Update the second user, verify that the returned properties are correct:
    UserView user2Updated = userService.update(user2.id(), user2DataUpdate);
    assertThat(user2Updated.id()).isEqualTo(user2.id());
    verifyNewUser(user2Updated, user2DataUpdate);

    // Verify that the list of all users just contains the 2 users, and the second one has updated
    // properties:
    assertThat(userService.findAll()).isEqualTo(List.of(user1, user2Updated));
  }

  private void verifyNewUser(UserView user, UserEdit data) {
    assertThat(user.id()).isNotNull();
    assertThat(user.firstName()).isEqualTo(data.firstName());
    assertThat(user.lastName()).isEqualTo(data.lastName());
    assertThat(user.email()).isEqualTo(data.email());
  }

}
