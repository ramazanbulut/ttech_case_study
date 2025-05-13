package com.ttech.repository;

import com.ttech.model.Location;
import com.ttech.model.Transportation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

@Repository
public interface TransportationRepository
        extends JpaRepository<Transportation, Long>,JpaSpecificationExecutor<Transportation>
{

    List<Transportation> findByOriginLocation(Location origin);

    List<Transportation> findByDestinationLocation(Location destination);

    List<Transportation> findByOriginLocationAndDestinationLocation(Location origin, Location destination);
}


