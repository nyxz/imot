package net.badowl.imot;

import net.badowl.imot.testcontainers.PostgreSqlContainer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Rollback
@Transactional
@Sql(scripts = {"classpath:sql/data.sql"})
abstract class SpringTest {

    static {
        System.setProperty("NOTIFICATION_PROPERTY_FILTER", "date_created > now() - interval '1 hour'");
        PostgreSqlContainer.enable();
    }
}
