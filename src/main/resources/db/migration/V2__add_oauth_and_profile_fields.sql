ALTER TABLE users
  ADD COLUMN auth_provider VARCHAR(50) NOT NULL DEFAULT 'LOCAL',
  ADD COLUMN full_name VARCHAR(255),
  ADD COLUMN profile_picture_url VARCHAR(512),
  ADD COLUMN reset_token VARCHAR(100),
  ADD COLUMN reset_token_expiry TIMESTAMP;
