package co.siegerand.stocklevelservice;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

public class MySqlDbContainer {

    private static final MySQLContainer database = new MySQLContainer("mysql:5.7.32");

    static {
        database.start();
    }

    @DynamicPropertySource
    private static void setDatabaseProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
    }

}
