package net.badowl.imot.jdbc;

import net.badowl.imot.AreaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcAreaRepo implements AreaRepo {

    @Autowired
    private JdbcTemplate template;

    @Override
    public List<String> list() {
        return template.queryForList("SELECT * FROM areas", String.class);
    }
}
