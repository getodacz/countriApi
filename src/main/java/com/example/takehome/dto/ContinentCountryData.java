package com.example.takehome.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
/**
 * DTO for continent country data.
 */
@Getter
@Setter
public class ContinentCountryData {
    private List<String> countries;
    private String name;
    private List<String> otherCountries;

    public ContinentCountryData() {
        this.countries = new ArrayList<>();
        this.otherCountries = new ArrayList<>();
    }
}