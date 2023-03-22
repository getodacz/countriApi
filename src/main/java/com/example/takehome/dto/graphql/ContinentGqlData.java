package com.example.takehome.dto.graphql;

import lombok.Getter;
import java.util.List;

/**
 * DTO for GraphQL continent data.
 */
@Getter
public class ContinentGqlData {
    private ContinentData data;

    @Getter
    public class ContinentData {
        private List<Continent> continents;
    }

    @Getter
    public static class Continent {
        private String code;
        private String name;
        private List<Country> countries;
    }

    @Getter
    public static class Country {
        private String code;
        private String name;
    }
}
