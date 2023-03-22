package com.example.takehome.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
/**
 * JPA Entity defining the continent source data used in the application.
 * The country data will be stored in the country table.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table( name = "continent",
    uniqueConstraints = {
        @UniqueConstraint( name="continent_code_idx", columnNames = "code")
    }
)
public class Continent {
    @Id
    @Column(name = "code", nullable = false, length = 3)
    private String code;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "continent")
    private List<Country> countries;
}
