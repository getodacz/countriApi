package com.example.takehome.integration;

import com.example.takehome.dto.ContinentCountryData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the CountriesController class.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres?currentSchema=country_api",
        "SPRING_DATASOURCE_USERNAME=postgres",
        "SPRING_DATASOURCE_PASSWORD=p123321!"
})
class CountriesControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Tests the rate limiter for the /api/v1/public/countries/{countryCode} endpoint.
     */
    @Test
    public void testCountryRateLimiter() {
        Map<HttpStatusCode, Integer> responseStatusCount = new ConcurrentHashMap<>();
        int rateLimitPerPeriod = 5;
        IntStream.rangeClosed(1, rateLimitPerPeriod + 1)
                .parallel()
                .forEach(i -> {
                    ResponseEntity<Object> response = restTemplate.getForEntity("/api/v1/public/countries/RO", Object.class);
                    HttpStatusCode statusCode = response.getStatusCode();
                    responseStatusCount.put(statusCode, responseStatusCount.getOrDefault(statusCode, 0) + 1);
                });

        assertEquals(2, responseStatusCount.keySet().size());
        assertTrue(responseStatusCount.containsKey(HttpStatus.TOO_MANY_REQUESTS));
        assertTrue(responseStatusCount.containsKey(HttpStatus.OK));
    }

    @Test
    public void publicApiApiShouldPassComplexTest() {
        ResponseEntity<ContinentCountryData[]> response = restTemplate.getForEntity("/api/v1/public/countries/CA,US,RO,IT,MD,AD,AF,CL,CN,AR,BR,MX", ContinentCountryData[].class);
        List<ContinentCountryData> continentCountryData = List.of(Objects.requireNonNull(response.getBody()));

        assertEquals(4, continentCountryData.size());

        continentCountryData.forEach(continentCountry -> {
            switch (continentCountry.getName()) {
                case "North America" -> {
                    assertEquals(3, continentCountry.getCountries().size());
                    assertTrue(continentCountry.getCountries().containsAll(List.of("US", "CA", "MX")));
                    assertTrue(10 < continentCountry.getOtherCountries().size());
                    assertFalse(continentCountry.getOtherCountries().contains("US"));
                    assertFalse(continentCountry.getOtherCountries().contains("CA"));
                    assertFalse(continentCountry.getOtherCountries().contains("MX"));
                }
                case "Europe" -> {
                    assertEquals(4, continentCountry.getCountries().size());
                    assertTrue(continentCountry.getCountries().containsAll(List.of("AD", "IT", "MD", "RO")));
                    assertTrue(10 < continentCountry.getOtherCountries().size());
                    assertFalse(continentCountry.getOtherCountries().contains("AD"));
                    assertFalse(continentCountry.getOtherCountries().contains("IT"));
                    assertFalse(continentCountry.getOtherCountries().contains("MD"));
                    assertFalse(continentCountry.getOtherCountries().contains("RO"));
                }
                case "Asia" -> {
                    assertEquals(2, continentCountry.getCountries().size());
                    assertTrue(continentCountry.getCountries().containsAll(List.of("CN", "AF")));
                    assertTrue(10 < continentCountry.getOtherCountries().size());
                    assertFalse(continentCountry.getOtherCountries().contains("CN"));
                    assertFalse(continentCountry.getOtherCountries().contains("AF"));
                }
                case "South America" -> {
                    assertEquals(3, continentCountry.getCountries().size());
                    assertTrue(continentCountry.getCountries().containsAll(List.of("AR", "BR", "CL")));
                    assertTrue(10 < continentCountry.getOtherCountries().size());
                    assertFalse(continentCountry.getOtherCountries().contains("AR"));
                    assertFalse(continentCountry.getOtherCountries().contains("BR"));
                    assertFalse(continentCountry.getOtherCountries().contains("CL"));
                }
            }
        });
    }

    @Test
    public void publicApiShouldPassForValidAndInvalidCountries() {
        ResponseEntity<ContinentCountryData[]> response = restTemplate.getForEntity("/api/v1/public/countries/IT ,ZZ, US", ContinentCountryData[].class);
        List<ContinentCountryData> continentCountryData = List.of(Objects.requireNonNull(response.getBody()));

        assertEquals(2, continentCountryData.size());
    }

    @Test
    public void publicApiShouldPassForIncorrectFormattedValidCountries() {
        ResponseEntity<ContinentCountryData[]> response = restTemplate.getForEntity("/api/v1/public/countries/IT , ca ,uS", ContinentCountryData[].class);
        List<ContinentCountryData> continentCountryData = List.of(Objects.requireNonNull(response.getBody()));

        assertEquals(2, continentCountryData.size());
    }

    @Test
    public void shouldThrowExceptionForInvalidCodesOnly() {
        try {
            restTemplate.getForEntity("/api/v1/public/countries/YY,ZZ", ContinentCountryData[].class);
            fail("Should have thrown an exception");
        } catch (Exception e) {
            // due to list of countries containing only invalid codes
            assertTrue(e instanceof RestClientException);
        }
    }

}