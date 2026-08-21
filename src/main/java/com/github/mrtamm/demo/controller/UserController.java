package com.github.mrtamm.demo.controller;

import com.github.mrtamm.demo.json.UserEdit;
import com.github.mrtamm.demo.json.UserView;
import com.github.mrtamm.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST API for managing users.
 */
@RestController
@Tag(name = "User API")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Operation(summary = "Retrieve all users", description = "Returns all registered users")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Found users are provided in the response payload in JSON format"
      )
  })
  @GetMapping("/api/v1/users")
  public List<UserView> list() {
    return userService.findAll();
  }

  @Operation(summary = "Add a new user", description = "Registers given information as a new user")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "User was successfully added and its details are provided in the payload"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Provided user data is incomplete, or the email is already in use"
      ),
  })
  @PostMapping("/api/v1/users")
  public UserView add(@Valid @RequestBody UserEdit user) {
    return userService.add(user);
  }

  @Operation(summary = "Update user", description = "Updates information about an existing user")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "User was successfully updated and its details are provided in the payload"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Provided user data is incomplete, or the changed email is already in use"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "The specified user was not found"
      ),
  })
  @PostMapping("/api/v1/users/{id}")
  public UserView update(@PathVariable UUID id, @Valid @RequestBody UserEdit user) {
    return userService.update(id, user);
  }

}
