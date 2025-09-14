package it.dtd.note.bdd.hooks;

import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public class ResetHooks {

    private final JdbcTemplate jdbc;

    @Before
    public void clean() {
        jdbc.update("DELETE FROM users");
    }
}
