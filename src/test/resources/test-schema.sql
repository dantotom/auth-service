DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS refresh_token;

CREATE TABLE users (
                       id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(255),
                       status VARCHAR(16) NOT NULL,
                       user_role   VARCHAR(16) NOT NULL,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP,
                       last_login_at TIMESTAMP
);

CREATE TABLE refresh_token (
                               id           UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                               user_id      UUID NOT NULL,
                               token_hash   VARCHAR(512) NOT NULL,
                               issued_at    TIMESTAMP NOT NULL,
                               expires_at   TIMESTAMP NOT NULL,
                               revoked_at   TIMESTAMP,
                               rotated_from UUID,

                               CONSTRAINT fk_refresh_token_user
                                   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                               CONSTRAINT fk_refresh_token_rotated
                                   FOREIGN KEY (rotated_from) REFERENCES refresh_token(id) ON DELETE SET NULL
);

CREATE INDEX idx_refresh_token_user ON refresh_token(user_id);
CREATE INDEX idx_refresh_token_hash ON refresh_token(token_hash);