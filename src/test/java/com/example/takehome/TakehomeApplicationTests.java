package com.example.takehome;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.endpoint.ApiVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * This class is used to test the application.
 */
@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(properties = {
        "SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres?currentSchema=country_api",
        "SPRING_DATASOURCE_USERNAME=postgres",
        "SPRING_DATASOURCE_PASSWORD=p123321!"
})
class TakehomeApplicationTests {
    /**
     * This test method is used to test that the application starts and has the desired version
     */
    @Test
    void contextLoads() {
        assertEquals(ApiVersion.LATEST, ApiVersion.V3);
    }
}
