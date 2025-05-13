package com.ttech.service;

import com.ttech.dto.TransportationFilterRequest;
import com.ttech.model.Location;
import com.ttech.model.Transportation;
import com.ttech.repository.LocationRepository;
import com.ttech.repository.TransportationRepository;
import com.ttech.specifications.TransportationSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class TransportationService {

    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;

    public Page<Transportation> getAllTransportations(TransportationFilterRequest filter, Pageable pageable) {
        Location origin = null;
        if (filter.getOriginLocationCode() != null) {
            origin = locationRepository.findByLocationCode(filter.getOriginLocationCode());
            if (origin == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid originLocationCode: " + filter.getOriginLocationCode());
            }
        }

        Location destination = null;
        if (filter.getDestinationLocationCode() != null) {
            destination = locationRepository.findByLocationCode(filter.getDestinationLocationCode());
            if (destination == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid destinationLocationCode: " + filter.getDestinationLocationCode());
            }
        }

        Specification<Transportation> spec = Specification
                .where(TransportationSpecifications.hasOrigin(origin))
                .and(TransportationSpecifications.hasDestination(destination))
                .and(TransportationSpecifications.operatesOnDay(
                        filter.getDate() != null ? filter.getDate().getDayOfWeek() : null));

        return transportationRepository.findAll(spec, pageable);
    }

    public Transportation getTransportationById(Long id) {
        return transportationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Transportation not found with id: " + id));
    }

    public Transportation createTransportation(Transportation transportation) {
        return transportationRepository.save(transportation);
    }

    public Transportation updateTransportation(Long id, Transportation transportationDetails) {
        Transportation existing = transportationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Transportation not found with id: " + id));

        existing.setOriginLocation(transportationDetails.getOriginLocation());
        existing.setDestinationLocation(transportationDetails.getDestinationLocation());
        existing.setTransportationType(transportationDetails.getTransportationType());
        existing.setOperatingDays(transportationDetails.getOperatingDays());

        return transportationRepository.save(existing);
    }

    public void deleteTransportation(Long id) {
        Transportation transportation = transportationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Transportation not found with id: " + id));
        transportationRepository.delete(transportation);
    }
}
