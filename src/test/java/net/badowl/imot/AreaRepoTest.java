package net.badowl.imot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertTrue;

class AreaRepoTest extends SpringTest {

    @Autowired
    private AreaRepo repo;

    @Test
    void list() {
        final List<String> list = repo.list();
        assertTrue(list.size() > 0);
    }
}