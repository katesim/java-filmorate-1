package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmMapper implements RowMapper<Film> {
    private final GenreService genreService;

    public FilmMapper(GenreService genreService) {
        this.genreService = genreService;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("MPA_ID"));
        mpa.setName(rs.getString("MPA"));
        Film film = new Film();
        film.setId(rs.getInt("ID"));
        film.setName(rs.getString("NAME"));
        film.setDescription(rs.getString("DESCRIPTION"));
        film.setReleaseDate(rs.getDate("RELEASE_DATE").toLocalDate());
        film.setDuration(rs.getLong("DURATION"));
        film.setMpa(mpa);
        film.setGenres(genreService.loadGenresByFilmId(rs.getInt("ID")));
        return film;
//
//        Mpa mpa = Mpa.builder().
//                id(rs.getInt("MPA_ID"))
//                .name(rs.getString("MPA"))
//                .build();
//        return Film.builder()
//                .id(rs.getInt("ID"))
//                .name(rs.getString("NAME"))
//                .description(rs.getString("DESCRIPTION"))
//                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
//                .duration(rs.getLong("DURATION"))
//                .mpa(mpa)
//                .genres(genreService.loadGenresByFilmId(rs.getInt("id")))
//                .build();
    }
    }

