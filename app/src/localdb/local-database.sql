-- Rate My Duck local development schema for PostgreSQL 17.
-- This script is run automatically when the database volume is initialized.
-- It intentionally does not drop tables, so rerunning it cannot erase data.

BEGIN;

CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255),
    bio TEXT,
    profile_picture_link TEXT,
    provider VARCHAR(10) NOT NULL
        CONSTRAINT valid_provider_check CHECK (provider IN ('github', 'google')),
    provider_user_id VARCHAR(255) NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    email VARCHAR(128) UNIQUE,
    CONSTRAINT unique_provider_user_id UNIQUE (provider, provider_user_id)
);

CREATE TABLE ducks (
    duck_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image_link TEXT,
    description TEXT,
    creator_id BIGINT REFERENCES users (user_id) ON DELETE SET NULL,
    rating_score INT
);

COMMENT ON COLUMN ducks.rating_score IS 'Updated with every rating';

CREATE TABLE ratings (
    user_id BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    duck_id BIGINT NOT NULL REFERENCES ducks (duck_id) ON DELETE CASCADE,
    is_positive BOOLEAN NOT NULL,
    PRIMARY KEY (user_id, duck_id)
);

CREATE TABLE comments (
    comment_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    duck_id BIGINT NOT NULL REFERENCES ducks (duck_id) ON DELETE CASCADE,
    comment_text VARCHAR(500) NOT NULL
);

CREATE INDEX ducks_name_index ON ducks USING btree (name);
CREATE INDEX ducks_creator_id_index ON ducks USING btree (creator_id);
CREATE INDEX ducks_rating_index ON ducks USING btree (rating_score);
CREATE INDEX users_username_index ON users USING btree (username);
CREATE INDEX ratings_user_id_index ON ratings USING btree (user_id);
CREATE INDEX comments_user_id_index ON comments USING btree (user_id);
CREATE INDEX comments_duck_id_index ON comments USING btree (duck_id);

COMMIT;
