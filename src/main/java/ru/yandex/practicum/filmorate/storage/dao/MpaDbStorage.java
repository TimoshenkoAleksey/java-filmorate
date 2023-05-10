package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM Mpa ORDER BY mpa_id";
        return jdbcTemplate.query(sql, new MpaMapper());
    }

    public Mpa getMpaById(int mpaId) {
        String sql = "SELECT * FROM Mpa WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, new MpaMapper(), mpaId);
    }
}
