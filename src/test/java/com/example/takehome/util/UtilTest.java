package com.example.takehome.util;

import com.example.takehome.dto.ContinentCountryData;
import com.example.takehome.exception.ApiInputDataValidationException;
import com.example.takehome.model.Continent;
import com.example.takehome.model.Country;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/**
 * This class is used to test the util class.
 */
class UtilTest {

    /**
     * This test method is used to test the convertToContinentCountries method.
     * It should convert the list of countries to a list of continent countries.
     */
    @Test
    void convertToContinentCountries() {
        Continent northAmerica = new Continent("NA", "North America", List.of(
                new Country("US", "USA", new Continent("NA", "Europe", List.of())),
                new Country("CA", "Canada", new Continent("NA", "Europe", List.of())),
                new Country("MX", "Canada", new Continent("NA", "Europe", List.of()))
        ));

        Continent europe = new Continent("EU", "Europe", List.of(
                new Country("IT", "Italy", new Continent("EU", "Europe", List.of())),
                new Country("UK", "United Kingdom", new Continent("EU", "Europe", List.of())),
                new Country("GE", "Germany", new Continent("EU", "Europe", List.of()))
        ));

        Country usCountry = new Country("US", "USA", northAmerica);
        Country caCountry = new Country("CA", "Canada", northAmerica);
        Country itCountry = new Country("IT", "Italy", europe);

        List<ContinentCountryData> continentCountryData = Util.convertToContinentCountries(List.of(usCountry, caCountry, itCountry));

        // 2 continents
        assertEquals(2, continentCountryData.size());
        continentCountryData.forEach(continentCountry -> {
            if (continentCountry.getName().equals("North America")) {
                assertEquals(2, continentCountry.getCountries().size());
                assertTrue(continentCountry.getCountries().containsAll(List.of("US", "CA")));
                assertEquals(1, continentCountry.getOtherCountries().size());
                assertTrue(continentCountry.getOtherCountries().contains("MX"));
            } else if (continentCountry.getName().equals("Europe")) {
                assertEquals(1, continentCountry.getCountries().size());
                assertTrue(continentCountry.getCountries().contains("IT"));
                assertEquals(2, continentCountry.getOtherCountries().size());
                assertTrue(continentCountry.getOtherCountries().containsAll(List.of("UK", "GE")));
            }
        });
    }

    /**
     * This test method is used to test the processCountries utility method.
     * It should process the list of countries, eliminate duplicates
     * and return a list of 3 countries.
     */
    @Test
    void processCountries() {
        List<String> countries = List.of("CA", " US", "mx ", "US");
        List<String> processedCountries = Util.processCountries(countries);

        assertEquals(3, processedCountries.size());
        assertTrue(new HashSet<>(processedCountries).containsAll(List.of("CA", "US", "MX")));
    }

    /**
     * This test method is used to test the processCountries utility method.
     * It should throw an exception because one of the input countries is not a 2 letter code.
     */
    @Test
    void processCountriesShouldThrowException() {
        try {
            Util.processCountries( List.of("CA", " USA"));
            fail("Should have thrown an exception");
        } catch (Exception e) {
            assertTrue(e instanceof ApiInputDataValidationException);
            assertEquals("Your request could not be processed. Country code must be 2 characters long", e.getMessage());
        }
    }
}