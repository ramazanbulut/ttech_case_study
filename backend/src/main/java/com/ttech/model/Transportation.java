package com.ttech.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "transportations")
@Data
@NoArgsConstructor
public class Transportation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origin_location_id", nullable = false)
    private Location originLocation;

    @ManyToOne
    @JoinColumn(name = "destination_location_id", nullable = false)
    private Location destinationLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "transportation_type", nullable = false)
    private TransportationType transportationType;

    @Column(name = "operating_days", columnDefinition = "integer[]")
    private List<Integer> operatingDays;

    public enum TransportationType {
        FLIGHT,
        BUS,
        SUBWAY,
        UBER
    }
} 