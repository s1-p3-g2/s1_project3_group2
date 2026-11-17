BEGIN;
TRUNCATE TABLE users RESTART IDENTITY CASCADE; -- deletes all users + restarts auto-incremented pk
INSERT INTO users
(username, bio, profile_picture_link, provider, provider_user_id, is_admin)
VALUES ('quackmaster', 'Professional duck enthusiast.',
        'https://example.com/profiles/quackmaster.jpg', 'github', 'gh_1001', TRUE),
       ('featherfan', 'Collecting rubber ducks since 2018.',
        'https://example.com/profiles/featherfan.jpg', 'google', 'google_2001', FALSE),
       ('pond_scientist', 'Researching duck behavior and pond ecosystems.',
        'https://example.com/profiles/pond-scientist.jpg', 'github', 'gh_1002', FALSE),
       ('rubberduckie', 'I like ducks with tiny hats.',
        'https://example.com/profiles/rubberduckie.jpg', 'google', 'google_2002', FALSE),
       ('duckdetective', 'Finding the best ducks on the internet.',
        'https://example.com/profiles/duckdetective.jpg', 'github', 'gh_1003', FALSE);
COMMIT;

BEGIN;
TRUNCATE TABLE ducks RESTART IDENTITY CASCADE; -- deletes all ducks + restarts auto-incremented pk
INSERT INTO ducks
(name, image_link, description, creator_id, rating_score)
VALUES ('Captain Quackers',
        'https://example.com/ducks/captain-quackers.jpg',
        'A brave yellow duck wearing a tiny sailor hat.',
        1,
        3),
       ('Sir Waddles',
        'https://example.com/ducks/sir-waddles.jpg',
        'A sophisticated duck with a bright blue bow tie.',
        2,
        1),
       ('Detective Duck',
        'https://example.com/ducks/detective-duck.jpg',
        'A mysterious duck searching for clues around the pond.',
        3,
        5),
       ('Chef Quack',
        'https://example.com/ducks/chef-quack.jpg',
        'A culinary duck specializing in imaginary pond cuisine.',
        4,
        1),
       ('Space Duck',
        'https://example.com/ducks/space-duck.jpg',
        'A futuristic duck ready to explore the galaxy.',
        5,
        3),
       ('Sleepy Duck',
        'https://example.com/ducks/sleepy-duck.jpg',
        'A very tired duck taking a nap beside the pond.',
        1,
        10),
       ('Paparazo',
        'https://example.com/ducks/paparazi-duck.jpg',
        'A very nosy duck, always has a camera at his side.',
        1,
        2),
       ('Cowboy Duck',
        'https://example.com/ducks/cowboy-duck.jpg',
        'One things for sure, the pond is only big enough for one duck when hes around.',
        1,
        6),
       ('Business Duck',
        'https://example.com/ducks/suit-duck.jpg',
        'A very professional duck, hes got a meeting to go to.',
        1,
        10),
       ('Albert',
        'https://example.com/ducks/boring-duck.jpg',
        'A very ..., um... Hes just a yellow duck okay?',
        1,
        -10);
COMMIT;

BEGIN;
TRUNCATE TABLE ratings RESTART IDENTITY CASCADE; -- deletes all ratings + restarts auto-incremented pk
INSERT INTO ratings
(user_id, duck_id, is_positive)
VALUES (1, 1, TRUE),
       (2, 1, TRUE),
       (3, 1, FALSE),
       (4, 1, TRUE),

       (1, 2, TRUE),
       (2, 2, FALSE),
       (5, 2, FALSE),

       (1, 3, TRUE),
       (2, 3, TRUE),
       (3, 3, TRUE),
       (4, 3, TRUE),
       (5, 3, TRUE),

       (2, 4, TRUE),
       (3, 4, FALSE),

       (1, 5, TRUE),
       (3, 5, FALSE),
       (4, 5, TRUE),

       (2, 6, FALSE),
       (5, 6, FALSE);
COMMIT;

BEGIN;
TRUNCATE TABLE comments RESTART IDENTITY CASCADE; -- deletes all comments + restarts auto-incremented pk
INSERT INTO comments
(user_id, duck_id, comment_text)
VALUES (2, 1, 'The sailor hat makes this duck legendary.'),
       (3, 1, 'Captain Quackers looks ready for adventure.'),
       (1, 2, 'Very elegant. Someone should give Sir Waddles a monocle.'),
       (4, 3, 'This duck definitely knows more than it is saying.'),
       (5, 3, 'My favorite duck in the collection so far.'),
       (1, 4, 'I would absolutely order soup from Chef Quack.'),
       (2, 5, 'Space Duck belongs on the moon.'),
       (3, 6, 'A peaceful and relatable duck.');
COMMIT;

