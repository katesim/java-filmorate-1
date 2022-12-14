create table if not exists users
(
    id       INT GENERATED BY DEFAULT AS IDENTITY primary key,
    name     CHARACTER VARYING(100),
    login    CHARACTER VARYING(50)  not null,
    email    CHARACTER VARYING(100) not null,
    birthday DATE
);

create table if not exists genres
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     VARCHAR(100) NOT NULL
);

create table if not exists mpa
(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name   VARCHAR(100) NOT NULL
);

create table if not exists films
(
    id           INT GENERATED BY DEFAULT AS IDENTITY primary key,
    name         CHARACTER VARYING(100) not null,
    description  CHARACTER VARYING(200) not null,
    release_date DATE,
    duration     INT,
    mpa_id       INTEGER REFERENCES mpa (id) ON DELETE NO ACTION
);

create table if not exists rating_films
(
    user_id INT REFERENCES users (id) ON DELETE CASCADE,
    film_id INT REFERENCES films (id) ON DELETE CASCADE,
    UNIQUE  (film_id, user_id)
);
create table if not exists friends
(
    user_id   INT REFERENCES users (id) ON DELETE CASCADE,
    friend_id INT REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE    (user_id, friend_id)
);

create table if not exists films_genres
(
    film_id  INT REFERENCES films (id) ON DELETE CASCADE,
    genre_id INT REFERENCES genres (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS film_id_uniq_index
    ON films (id);

CREATE UNIQUE INDEX IF NOT EXISTS user_id_uniq_index
    ON users (id);