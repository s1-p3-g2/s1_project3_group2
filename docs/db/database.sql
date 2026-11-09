-- Rate My Duck Database
-- Last Updated: 11/9/2026
-- Version: 1.0

-- Drops tables if database isn't empty
BEGIN;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS ducks;
COMMIT;

-- Creates tables
BEGIN;
CREATE TABLE ducks (
    duck_id SERIAL Primary Key,
    name    VARCHAR(255) NOT NULL,
    image_link TEXT,
    description TEXT,
    creator_id INT REFERENCES users(user_id),
    rating_score INT DEFAULT 0,
    position INT DEFAULT -1
);
COMMENT ON COLUMN ducks.rating_score is 'Updated with every rating, or ever X amount of time';
COMMENT ON COLUMN ducks.position is 'Updated with every rating, or ever X amount of time';

CREATE TABLE users (
    user_id SERIAL Primary Key,
    username VARCHAR(255),
    bio TEXT,
    profile_picture_link TEXT,
    provider VARCHAR(10) NOT NULL CONSTRAINT valid_provider_check CHECK (provider IN ('github', 'google')),
    provider_user_id varchar(255) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE
);

CREATE TABLE ratings (
    user_id INT REFERENCES users(user_id),
    duck_id INT REFERENCES ducks(duck_id),
    is_positive BOOLEAN NOT NULL
);

CREATE TABLE comments (
    comment_id SERIAL Primary Key,
    user_id INT References users(user_id),
    duck_id INT REFERENCES ducks(duck_id),
    comment TEXT
);
COMMIT;

-- Create Indexes
BEGIN;
-- Duck Indices
CREATE INDEX ducks_duck_id_index ON ducks USING btree (duck_id);
CREATE INDEX ducks_username_index ON ducks USING btree (name);
CREATE INDEX ducks_creator_id_index ON ducks USING btree (creator_id);
CREATE INDEX ducks_position_index ON ducks USING btree (position);
CREATE INDEX ducks_rating_index ON ducks USING btree (rating_score);

-- User Indices
CREATE INDEX users_username_index ON users USING btree (username);
CREATE INDEX users_user_id_index ON users USING btree (user_id);

-- Rating Indices
CREATE INDEX ratings_user_id_index ON ratings USING btree (user_id);
CREATE INDEX ratings_duck_id_index ON ratings USING btree (duck_id);

-- Comment Indices
CREATE INDEX comments_user_id_index ON comments USING btree (user_id);
CREATE INDEX comments_duck_id_index ON comments USING btree (duck_id);
COMMIT;