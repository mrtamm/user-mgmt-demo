CREATE TABLE app_user (
  id         UUID PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  last_name  VARCHAR(100) NOT NULL,
  email      VARCHAR(100) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT current_timestamp
);

COMMENT ON TABLE app_user IS 'Application user details';
