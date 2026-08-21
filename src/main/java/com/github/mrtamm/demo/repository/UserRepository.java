package com.github.mrtamm.demo.repository;

import com.github.mrtamm.demo.json.UserView;
import com.github.mrtamm.demo.json.UserEdit;
import com.github.mrtamm.demo.service.AppConstraints;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Database-level operations on the USER table (without applying business constraints).
 */
@Repository
public class UserRepository {

  private final JdbcClient jdbcClient;

  public UserRepository(JdbcClient jdbcClient) {
    this.jdbcClient = Objects.requireNonNull(jdbcClient);
  }

  /**
   * Retrieves all APP_USER records.
   *
   * @return A list of table records.
   */
  public List<UserView> findAll() {
    return jdbcClient.sql("SELECT id, first_name, last_name, email FROM app_user")
        .query(UserPayloadMapper.INSTANCE)
        .list();
  }

  /**
   * Retrieves a single APP_USER record by ID. Fails with a runtime exception when the record is not
   * found.
   *
   * @param id The targeted APP_USER record ID.
   * @return The record data.
   */
  public UserView findById(UUID id) {
    return jdbcClient.sql("SELECT id, first_name, last_name, email FROM app_user WHERE id=?")
        .param(id)
        .query(UserPayloadMapper.INSTANCE)
        .single();
  }

  /**
   * Adds a new APP_USER record that uses given ID and user data.
   *
   * <p>Potentially fails with a runtime exception when the EMAIL is not unique or the name fields
   * are empty.
   *
   * @param newId ID for the new record.
   * @param user User data.
   */
  public void add(UUID newId, UserEdit user) {
    jdbcClient.sql("INSERT INTO app_user (id, first_name, last_name, email) VALUES (?,?,?,?)")
        .param(newId)
        .param(user.firstName())
        .param(user.lastName())
        .param(user.email())
        .update();
  }

  /**
   * Updates an existing APP_USER record that uses given ID.
   *
   * <p>Potentially fails with a DuplicateKeyException when the EMAIL is not unique or the name
   * fields are empty.
   *
   * <p>This method does not fail when no matching record is updated. Use the returned number to
   * verify that the expected number of records were updated.
   *
   * @param id The target APP_USER record ID.
   * @param user User data.
   * @return The number of rows updated (typically 1).
   */
  public int update(UUID id, UserEdit user) {
    return jdbcClient.sql("UPDATE app_user SET first_name=?, last_name=?, email=? WHERE id=?")
        .param(user.firstName())
        .param(user.lastName())
        .param(user.email())
        .param(id)
        .update();
  }

  /**
   * Generates a UUID value, which is ensured to be not used by existing APP_USER records.
   *
   * <p>To avoid deadlock, this method generates a UUID value no more than 100 times.
   * When the limit is exceeded, the method fails with a runtime exception.
   *
   * @return The generated and unused UUID value.
   */
  public UUID generateId() {
    for (int i = 0; i < 100; i++) {
      UUID id = UUID.randomUUID();

      boolean found = jdbcClient.sql("SELECT id FROM app_user WHERE id=?")
          .param(id)
          .query()
          .optionalValue()
          .isPresent();

      if (!found) {
        return id;
      }
    }

    throw new AppConstraints.ServiceError("Unique unused user ID could not be generated "
        + "(please try again)");
  }

  public boolean existsEmail(String email) {
    return jdbcClient.sql("SELECT 1 FROM app_user WHERE email=?")
        .param(email)
        .query()
        .optionalValue()
        .isPresent();
  }

  /**
   * Row-mapper for reading returned APP_USER records.
   */
  private static class UserPayloadMapper implements RowMapper<UserView> {

    private static final UserPayloadMapper INSTANCE = new UserPayloadMapper();

    @Override
    public UserView mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new UserView(
          UUID.fromString(rs.getString(1)),
          rs.getString(2),
          rs.getString(3),
          rs.getString(4)
      );
    }

  }

}
