ALTER TABLE users
    ADD o_auth_id VARCHAR(255);

ALTER TABLE users
    ADD o_auth_provider VARCHAR(255);

ALTER TABLE users
    ALTER COLUMN o_auth_id SET NOT NULL;

ALTER TABLE users
    ALTER COLUMN o_auth_provider SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT uc_users_oauthid UNIQUE (o_auth_id);

ALTER TABLE users
    DROP COLUMN google_id;