package com.ttech.repository;

import com.ttech.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByLocationCode(String locationCode);
    
    List<Location> findByCountryContainingIgnoreCase(String country);
    List<Location> findByCityContainingIgnoreCase(String city);
    List<Location> findByNameContainingIgnoreCase(String name);
    List<Location> findByCountryContainingIgnoreCaseAndCityContainingIgnoreCase(String country, String city);

    Page<Location> findByNameContainingIgnoreCaseOrLocationCodeContainingIgnoreCase(
            String name, String locationCode, Pageable pageable);
} 