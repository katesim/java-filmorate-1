MERGE INTO mpa (MPA_ID, NAME)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

MERGE INTO genres (GENRE_ID, NAME)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Фантастика'),
           (5, 'Триллер'),
           (6, 'Боевик');