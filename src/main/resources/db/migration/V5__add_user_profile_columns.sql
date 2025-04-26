ALTER TABLE users
  ADD COLUMN reset_token           VARCHAR(64),
  ADD COLUMN full_name             VARCHAR(255),
  ADD COLUMN profile_picture_url   VARCHAR(255);
