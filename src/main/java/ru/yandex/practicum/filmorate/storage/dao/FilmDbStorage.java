package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Component
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final LikesDbStorage likesDbStorage;

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO Film (name, description, release_date, duration, mpa_id) VALUES(?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        film.getGenres().forEach(genre -> addGenreToFilm(film.getId(), genre.getId()));
        log.info("Фильм {} сохранен", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE Film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                " WHERE film_id = ?";
        int amountOperations = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        if (amountOperations > 0) {
            deleteAllGenresFromFilm(film.getId());
            Set<Genre> genres = film.getGenres();
            genres.forEach(genre -> addGenreToFilm(film.getId(), genre.getId()));
            genres.clear();
            genres.addAll(getGenreByFilmId(film.getId()));
            return film;
        }
        log.debug("Фильм с id={} не найден", film.getId());
        throw new ValidationException(String.format("Фильм с id = %s не найден", film.getId()));
    }

    @Override
    public int deleteFilm(String id) {
        return jdbcTemplate.update("DELETE FROM Film WHERE id = ?", id);
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.*, m.name FROM Film AS f JOIN Mpa AS m ON f.mpa_id = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, new FilmMapper());
        for (Film film: films) {
            film.getGenres().addAll(getGenreByFilmId(film.getId()));
            film.getLikes().addAll(likesDbStorage.getFilmLikes(film.getId()));
        }
        return films;
    }

    @Override
    public Film getFilmById(long filmId) {
        String sql = "SELECT f.*, m.name FROM Film AS f JOIN mpa AS m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
        Film film = jdbcTemplate.queryForObject(sql, new FilmMapper(), filmId);
        if (film != null) {
            film.getGenres().addAll(getGenreByFilmId(filmId));
            film.getLikes().addAll(likesDbStorage.getFilmLikes(filmId));
        }
        return film;
    }

    private List<Genre> getGenreByFilmId(long filmId) {
        String sql = "SELECT g.* FROM GenreFilm AS gf JOIN Genre AS g ON gf.genre_id = g.genre_id WHERE " +
                "gf.film_id = ? ORDER BY g.genre_id";
        return jdbcTemplate.query(sql, new GenreMapper(), filmId);
    }

    private void addGenreToFilm(long filmId, int genreId) {
        String sql = "INSERT INTO GenreFilm (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    private void deleteAllGenresFromFilm(long filmId) {
        String sql = "DELETE FROM GenreFilm WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}
