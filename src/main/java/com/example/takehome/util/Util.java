package com.example.takehome.util;

import com.example.takehome.dto.ContinentCountryData;
import com.example.takehome.exception.ApiInputDataValidationException;
import com.example.takehome.model.Country;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * This class is used to provide utility methods.
 */
public class Util {
    /**
     * This method is used to process the list of countries and convert it to the
     * desired result structure specified in the requirements
     * which is a list of ContinentCountryData
     * The converted result will be returned by the main application endpoint in the response body by calling:
     * ResponseEntity.ok(continentCountries)
     *
     * @param countryList The list of countries.
     * @return The list of processed countries.
     */
    public static List<ContinentCountryData> convertToContinentCountries(List<Country> countryList) {
        Map<String, ContinentCountryData> continentMap = new HashMap<>();

        countryList.forEach(country -> {
            String continentCode = country.getContinent().getCode();
            String continentName = country.getContinent().getName();

            if (continentMap.get(continentCode) == null) {
                // New continent found
                ContinentCountryData continentCountryData = new ContinentCountryData();
                continentCountryData.setName(continentName);
                continentCountryData.getCountries().add(country.getCode());
                continentCountryData.getOtherCountries().addAll(
                        country.getContinent().getCountries().stream()
                                .map(Country::getCode)
                                .filter(code -> !code.equals(country.getCode()))
                                .toList());

                continentMap.put(continentCode, continentCountryData);
            } else {
                // Continent already exist in the map; update the currencies with this currency
                ContinentCountryData continentCountryData = continentMap.get(continentCode);

                // Assume input currencies are unique in the list
                continentCountryData.getCountries().add(country.getCode());
                var updatedOtherCurrencies = continentCountryData.getOtherCountries()
                        .stream()
                        .filter(otherCurrencyCode -> !otherCurrencyCode.equals(country.getCode()))
                        .toList();

                continentCountryData.setOtherCountries(updatedOtherCurrencies);
            }
        });

        return continentMap.values().stream().toList();
    }

    /**
     * This method is used to process the list of countries.
     * It should remove the duplicate countries and trim the spaces.
     * @param list The list of countries.
     * @return The list of processed countries (clean).
     */
    public static List<String> processCountries(List<String> list) {
        // Remove whiteCharacters and duplicates, convert to uppercase
        List<String> cleanList = list.stream()
                .map(s -> s.replaceAll("\\s", "").toUpperCase()).toList()
                .stream().distinct().toList();

        cleanList.forEach(countryCode -> {
            if (countryCode.length() != 2) {
                throw new ApiInputDataValidationException("Country code must be 2 characters long");
            }
        });

        return cleanList;
    }
}
