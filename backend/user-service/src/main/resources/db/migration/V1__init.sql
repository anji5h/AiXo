CREATE TABLE users
(
    user_id    UUID                     NOT NULL,
    google_id  VARCHAR(255)             NOT NULL,
    name       VARCHAR(100)             NOT NULL,
    email      VARCHAR(255)             NOT NULL,
    imageurl   VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_googleid UNIQUE (google_id);