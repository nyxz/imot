package net.badowl.imot;

import net.badowl.imot.testcontainers.PostgreSqlContainer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Sql(scripts = {"classpath:sql/data.sql"})
abstract class SpringTest {

    static {
        PostgreSqlContainer.enable();
    }
}
