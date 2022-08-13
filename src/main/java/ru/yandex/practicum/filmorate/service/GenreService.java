package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenreById(int id) {
        Optional<Genre> genre = genreStorage.loadGenreById(id);
        if (genre.isPresent()) {
            log.debug("GET genre {}", genre.get());
            return genre.get();
        } else {
            throw new NotFoundException("Genre id #" + id + " not found");
        }
    }

    public List<Genre> loadGenresByFilmId(int id) {
        return genreStorage.loadGenresByFilmId(id);
    }

    public void saveGenresInFilm(int id, List<Genre> genres) {
        genreStorage.saveGenresInFilm(id, genres);
    }

    public void updateFilmGenres(int id, List<Genre> genres) {
        genreStorage.deleteGenresInFilm(id);
        genreStorage.saveGenresInFilm(id, genres);
    }

    public void deleteGenresInFilm(int id) {
        genreStorage.deleteGenresInFilm(id);
    }

    public List<Genre> getAllGenres() {
        List<Genre> genre = genreStorage.getAllGenres();
        log.debug("Load {} genres", genre.size());
        return genre;
    }

    public List<Genre> findByIds(List<Integer> ids) {
        return genreStorage.findByIds(ids);
    }
}
