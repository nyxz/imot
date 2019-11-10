package net.badowl.imot.testcontainers;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSqlContainer {
    /**
     * Run PostgreSQL container to host the test database.
     */
    private static final PostgreSQLContainer postgresqlContainer = ((PostgreSQLContainer) new PostgreSQLContainer("postgres:10.6")
            .withInitScript("sql/schema.sql"))
            .withUsername("imot")
            .withPassword("imot")
            .withDatabaseName("imot");

    static {
        postgresqlContainer.start();
        System.setProperty("SPRING_DATASOURCE_URL", postgresqlContainer.getJdbcUrl());
        System.setProperty("SPRING_DATASOURCE_USERNAME", postgresqlContainer.getUsername());
        System.setProperty("SPRING_DATASOURCE_PASSWORD", postgresqlContainer.getPassword());
    }


    public static void enable() {
    }
}
