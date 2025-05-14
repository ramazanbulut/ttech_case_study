package com.ttech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Country is required")
    @Column(nullable = false)
    private String country;

    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Location code is required")
    @Column(name = "location_code", nullable = false, unique = true)
    private String locationCode;

    public enum SortBy {
        id, name, locationCode, country, city
    }
}

