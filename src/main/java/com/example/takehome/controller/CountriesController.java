package com.example.takehome.controller;

import com.example.takehome.dto.ContinentCountryData;
import com.example.takehome.exception.ApiInputDataValidationException;
import com.example.takehome.model.Country;
import com.example.takehome.service.CountryService;
import com.example.takehome.util.Util;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 Controller class for handling country related requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Cacheable("countryDataCache")
public class CountriesController {
    private final CountryService countryService;

    /**
     The public version of the main endpoint of the application.
     Retrieves country data for a public user for the given country codes.
     Uses rate limiting to limit the number of requests per second.

     @param countryCodes list of country codes to retrieve data for
     @return response entity containing a list of continent country data objects
     */
    @RateLimiter(name = "nonAuthCountryDataRateLimiter")
    @GetMapping("/public/countries/{countryCodes}")
    public ResponseEntity<List<ContinentCountryData>> getCountryDataForPublicUser(@PathVariable List<String> countryCodes) {
        log.info("Retrieving country data for public user for country codes: " + countryCodes);
        return getCountryData(countryCodes);
    }

    /**
     The private version of the main endpoint of the application.
     Retrieves country data for an authenticated user for the given country codes.
     @param countryCodes list of country codes to retrieve data for
     @return response entity containing a list of continent country data objects
     */
    @RateLimiter(name = "authCountryDataRateLimiter")
    @GetMapping("/private/countries/{countryCodes}")
    public ResponseEntity<List<ContinentCountryData>> getCountryDataForAuthUser(@PathVariable List<String> countryCodes) {
        log.info("Retrieving country data for auth user for country codes: " + countryCodes);
        return getCountryData(countryCodes);
    }

    /**
     Retrieves country data for the given country codes.

     @param countryCodes list of country codes to retrieve data for
     @return response entity containing a list of continent country data objects
     */
    private ResponseEntity<List<ContinentCountryData>> getCountryData(List<String> countryCodes) {
        if(countryCodes.isEmpty()) {
            // Exit early if no country codes are provided
            return ResponseEntity.badRequest().build();
        }
        // Get countries with the given codes (duplicates are removed for simplicity)
        List<Country> countryList = countryService.getCountriesByCodeIn(
                Util.processCountries(countryCodes));

        // Convert the list of countries to the desired result format
        List<ContinentCountryData> continentCountries = Util.convertToContinentCountries(countryList);
        if(continentCountries.isEmpty()) {
            // Exit early if no countries are found
            throw new ApiInputDataValidationException("The list of countries contains only invalid country codes.");
        }
        return ResponseEntity.ok(continentCountries);
    }
}