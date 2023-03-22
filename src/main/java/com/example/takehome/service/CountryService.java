package com.example.takehome.service;

import com.example.takehome.model.Country;
import com.example.takehome.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 Service class for managing countries.
 */
@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    /**
     Returns a list of {@link Country} objects whose code is present in the provided list.
     @param countryList a list of country codes to search for
     @return a list of {@link Country} objects whose code is present in the provided list
     */
    public List<Country> getCountriesByCodeIn(List<String> countryList) {
        return countryRepository.findCountriesByCodeIn(countryList);
    }
}
