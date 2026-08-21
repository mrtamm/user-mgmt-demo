package com.github.mrtamm.demo.service;

import com.github.mrtamm.demo.json.UserView;
import com.github.mrtamm.demo.json.UserEdit;
import com.github.mrtamm.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Business-level operations on the USER entity.
 */
@Service
public class UserService {

  private static final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository repository;

  public UserService(UserRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  /**
   * Retrieves a list of all users (without any specific order).
   */
  public List<UserView> findAll() {
    return repository.findAll();
  }

  /**
   * Adds a new user record, which is also the return value. The user is required to have a unique
   * email address value.
   *
   * @param user User data for the new record.
   *
   * @return The fresh record details.
   */
  @Transactional
  public UserView add(UserEdit user) {
    if (repository.existsEmail(user.email())) {
      throw new AppConstraints.BadInput("Please check if the user is already registered " +
          "as the email is already in use", "email");
    }

    UUID newId = repository.generateId();
    repository.add(newId, user);

    log.info("Successfully added USER with ID=[{}].", newId);
    return repository.findById(newId);
  }

  /**
   * Updates an existing user record with the new values. The user email value must remain unique.
   * An error is thrown when the record is not found.
   *
   * @param id The targeted record ID.
   * @param user User data for the existing record.
   *
   * @return The fresh record details.
   */
  @Transactional
  public UserView update(UUID id, UserEdit user) {
    try {
      int updatedCount = repository.update(id, user);
      if (updatedCount != 1) {
        throw new AppConstraints.NotFound("The referenced user was not found");
      }
      return repository.findById(id);
    } catch (DuplicateKeyException e) {
      throw new AppConstraints.BadInput("Provided email is already used by another user", "email");
    }
  }

}
