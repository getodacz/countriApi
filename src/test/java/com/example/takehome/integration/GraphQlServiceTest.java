package com.example.takehome.integration;

import com.example.takehome.dto.graphql.ContinentGqlData;
import com.example.takehome.service.GraphQlService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This class is used to test the graphql service.
 */
@ActiveProfiles("test")
@SpringBootTest
@ContextConfiguration(classes = GraphQlService.class)
class GraphQlServiceTest {
    @Autowired
    private GraphQlService graphQlService;

    /**
     * This test method is used to test the graphql service.
     * It should retrieve latest data from the https://countries.trevorblades.com/graphql endpoint.
     */
    @Test
    public void logFlywayMigrationData() {
        try {
            ContinentGqlData continentDto = graphQlService.getContinentsWithCountries();
            assertTrue(continentDto.getData().getContinents().size() > 5);
        } catch (Exception e) {
            System.out.println("Error while retrieving new api data from the graphql endpoint: " + e);
            fail("Error loading GRAPHQL data.");
        }
    }
}