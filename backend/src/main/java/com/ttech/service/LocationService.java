package com.ttech.service;

import com.ttech.constant.ExceptionMessages;
import com.ttech.model.Location;
import com.ttech.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public Page<Location> getAllLocations(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return locationRepository.findByNameContainingIgnoreCaseOrLocationCodeContainingIgnoreCase(search, search,
                    pageable);
        } else {
            return locationRepository.findAll(pageable);
        }
    }

    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                            String.format(ExceptionMessages.LOCATION_NOT_FOUND, id)));
    }

    public Location createLocation(Location location) {
        if (locationRepository.findByLocationCode(location.getLocationCode()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(ExceptionMessages.LOCATION_CODE_EXISTS, location.getLocationCode()));
        }
        return locationRepository.save(location);
    }

    public Location updateLocation(Long id, Location locationDetails) {
        Location existingLocation = locationRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                            String.format(ExceptionMessages.LOCATION_NOT_FOUND, id)));

        Location existingWithCode = locationRepository.findByLocationCode(locationDetails.getLocationCode());
        if (existingWithCode != null && !existingWithCode.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(ExceptionMessages.LOCATION_CODE_EXISTS, locationDetails.getLocationCode()));
        }

        existingLocation.setName(locationDetails.getName());
        existingLocation.setCountry(locationDetails.getCountry());
        existingLocation.setCity(locationDetails.getCity());
        existingLocation.setLocationCode(locationDetails.getLocationCode());

        return locationRepository.save(existingLocation);
    }

    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                            String.format(ExceptionMessages.LOCATION_NOT_FOUND, id)));
        try {
            locationRepository.delete(location);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    ExceptionMessages.LOCATION_DELETE_CONFLICT);
        }
    }
}
