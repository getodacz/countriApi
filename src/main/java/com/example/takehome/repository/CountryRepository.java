package com.example.takehome.repository;

import com.example.takehome.model.Country;
import com.example.takehome.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The CountryRepository interface provides the methods for interacting
 * with the Country entity {@link Country} in the database.
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, String> {

    /**
     Retrieves a list of countries by their codes.
     @param countryList A list of country codes to search for.
     @return A list of countries matching the provided codes.
     */
    List<Country> findCountriesByCodeIn(List<String> countryList);
}
