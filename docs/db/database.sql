-- Rate My Duck Database
-- Last Updated: 11/9/2026
-- Version: 1.0

-- Drops tables if database isn't empty
BEGIN;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS ducks;
DROP TABLE IF EXISTS users;
COMMIT;

-- Creates tables
BEGIN;

CREATE TABLE users
(
    user_id              SERIAL Primary Key,
    username             VARCHAR(255),
    bio                  TEXT,
    profile_picture_link TEXT,
    provider             VARCHAR(10)  NOT NULL
        CONSTRAINT valid_provider_check CHECK (provider IN ('github', 'google')),
    provider_user_id     varchar(255) NOT NULL,
    is_admin             BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT unique_provider_user_id UNIQUE (provider, provider_user_id)
);

CREATE TABLE ducks
(
    duck_id      SERIAL Primary Key,
    name         VARCHAR(255) NOT NULL,
    image_link   TEXT,
    description  TEXT,
    creator_id   INT REFERENCES users (user_id) ON DELETE SET NULL,
    rating_score INT,
);
COMMENT ON COLUMN ducks.rating_score is 'Updated with every rating';


CREATE TABLE ratings
(
    user_id     INT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    duck_id     INT NOT NULL REFERENCES ducks (duck_id) ON DELETE CASCADE,
    is_positive BOOLEAN NOT NULL,
    PRIMARY KEY (user_id, duck_id)
);

CREATE TABLE comments
(
    comment_id SERIAL Primary Key,
    user_id    INT NOT NULL References users (user_id) ON DELETE CASCADE,
    duck_id    INT NOT NULL REFERENCES ducks (duck_id) ON DELETE CASCADE,
    comment    TEXT NOT NULL
);
COMMIT;

-- Create Indexes
BEGIN;
-- Duck Indices
CREATE INDEX ducks_name_index ON ducks USING btree (name);
CREATE INDEX ducks_creator_id_index ON ducks USING btree (creator_id);
CREATE INDEX ducks_position_index ON ducks USING btree (position);
CREATE INDEX ducks_rating_index ON ducks USING btree (rating_score);

-- User Indices
CREATE INDEX users_username_index ON users USING btree (username);

-- Rating Indices
CREATE INDEX ratings_user_id_index ON ratings USING btree (user_id); -- Composite Primary Keys efficiently index the starting attribute, not any others

-- Comment Indices
CREATE INDEX comments_user_id_index ON comments USING btree (user_id);
CREATE INDEX comments_user_id_index ON comments USING btree (duck_id);

COMMIT;
