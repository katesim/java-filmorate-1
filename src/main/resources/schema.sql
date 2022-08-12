create table if not exists users
(
    id       INTEGER auto_increment primary key,
    name     CHARACTER VARYING(100),
    login    CHARACTER VARYING(50)  not null,
    email    CHARACTER VARYING(100) not null,
    birthday DATE
);

create table if not exists mpa
(
    mpa_id INTEGER PRIMARY KEY,
    name   VARCHAR(100) NOT NULL
);

create table if not exists films
(
    id           INTEGER auto_increment primary key,
    name         CHARACTER VARYING(100) not null,
    description  CHARACTER VARYING(200) not null,
    release_date DATE,
    duration     INTEGER,
    mpa_id       INTEGER REFERENCES mpa (mpa_id)
);

create table if not exists rating_films
(
    user_id INTEGER REFERENCES users (id),
    film_id INTEGER REFERENCES films (id)
);
create table if not exists friends
(
    user_id   INTEGER REFERENCES users (id),
    friend_id INTEGER REFERENCES users (id)
);

create table if not exists films_mpa
(
    film_id INTEGER REFERENCES films (id),
    mpa_id  INTEGER REFERENCES mpa (mpa_id)
);

create table if not exists genres
(
    genre_id INTEGER PRIMARY KEY,
    name     VARCHAR(100) NOT NULL
);
create table if not exists films_genres
(
    film_id  INTEGER REFERENCES films (id),
    genre_id INTEGER REFERENCES genres (genre_id)
);