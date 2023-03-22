package com.example.takehome.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * JPA Entity defining the country source data used in the application.
 * The country data will be stored in the country table.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table( name = "country",
    uniqueConstraints = {
            @UniqueConstraint( name="country_code_idx", columnNames = "code")
    }
)
public class Country {
    @Id
    @Column(name = "code", nullable = false, length = 3)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne
    @JoinColumn(name = "continent_code")
    @JsonIgnore
    private Continent continent;
}
