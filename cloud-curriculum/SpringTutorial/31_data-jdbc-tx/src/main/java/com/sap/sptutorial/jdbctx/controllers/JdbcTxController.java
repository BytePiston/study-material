package com.sap.sptutorial.jdbctx.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sap.sptutorial.jdbctx.domain.Player;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("players")
public class JdbcTxController {

    private static final String QUERY_PLAYER_COUNT = "SELECT COUNT(*) FROM PLAYERS WHERE ID = ?";
    private static final String QUERY_PLAYER = "SELECT * FROM PLAYERS WHERE ID = ?";
    public static final String UPDATE_PLAYER_NAME = "UPDATE PLAYERS SET NAME = ? WHERE ID = ?";
    public static final String UPDATE_PLAYER_BIRTHDATE = "UPDATE PLAYERS SET BIRTH_DATE = ? WHERE ID = ?";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class NotFoundExeception extends RuntimeException {

        NotFoundExeception(Throwable cause) {
            super(cause);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    static class NotExistingException extends RuntimeException {

        NotExistingException(String message) {
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    static class BadDataException extends RuntimeException {

        BadDataException(String message) {
            super(message);
        }
    }

    private static class PlayerRowMapper implements RowMapper<Player> {

        @Override
        public Player mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Player(rs.getInt("ID"), rs.getString("NAME"),
                    rs.getTimestamp("BIRTH_DATE"));
        }
    }

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping(value = "/{id}")
    public Player getPlayer(@PathVariable("id") String id) {
        log.debug("getPlayer({})", id);
        Player p;
        try {
            p = jdbc.queryForObject(QUERY_PLAYER, new PlayerRowMapper(),
                    Integer.valueOf(id));
        }
        catch (EmptyResultDataAccessException ex) {
            throw new NotFoundExeception(ex.getCause());
        }

        log.debug(p.toString());
        log.debug(jdbc.getDataSource().toString());
        return p;
    }

    @Transactional()
    @PutMapping(value = "/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") String id,
            @RequestBody Player p) {
        log.debug(p.toString());
        int num = jdbc.queryForObject(QUERY_PLAYER_COUNT, Integer.class,
                Integer.valueOf(id));
        if (num != 1) {
            throw new NotExistingException(
                    "Instance to be updated does not exist: id=" + id);
        }

        jdbc.update(UPDATE_PLAYER_NAME, p.getName(), Integer.valueOf(id));
        Timestamp newBirthDate = p.getBirthDate();
        if (newBirthDate != null) {
            Calendar calNewBirth = Calendar.getInstance();
            calNewBirth.setTime(newBirthDate);
            calNewBirth.set(Calendar.YEAR, calNewBirth.get(Calendar.YEAR) + 18);

            Calendar calToday = Calendar.getInstance();
            calToday.set(Calendar.HOUR_OF_DAY, 0);
            calToday.set(Calendar.MINUTE, 0);
            calToday.set(Calendar.SECOND, 0);
            calToday.set(Calendar.MILLISECOND, 0);

            if (calNewBirth.toInstant().isAfter(calToday.toInstant())) {
                throw new BadDataException("Too young");
            }
            jdbc.update(UPDATE_PLAYER_BIRTHDATE, p.getBirthDate(),
                    Integer.valueOf(id));
        }
        return new ResponseEntity<>(getPlayer(id), HttpStatus.OK);
    }
}
