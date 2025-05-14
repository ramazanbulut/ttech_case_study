package com.ttech.service;

import com.ttech.constant.ExceptionMessages;
import com.ttech.dto.requests.RouteSearchRequest;
import com.ttech.model.Location;
import com.ttech.model.Transportation;
import com.ttech.repository.LocationRepository;
import com.ttech.repository.TransportationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

@Service
public class RouteService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TransportationRepository transportationRepository;

    public List<List<Transportation>> findValidRoutes(RouteSearchRequest request) {
        Location origin = locationRepository.findByLocationCode(request.getOriginLocationCode());
        Location destination = locationRepository.findByLocationCode(request.getDestinationLocationCode());

        if (origin == null ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(ExceptionMessages.INVALID_ORIGIN_LOCATION,
                        request.getOriginLocationCode()));
        }
        if (destination == null ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(ExceptionMessages.INVALID_DESTINATION_LOCATION,
                        request.getDestinationLocationCode()));
        }

        // Use current date if date is not provided
        LocalDate searchDate = request.getDate() != null ? request.getDate() : LocalDate.now();

        List<List<Transportation>> allRoutes = new ArrayList<>();
        findRoutes(origin, destination, new ArrayList<>(), allRoutes, searchDate, new HashSet<>(),false);

        return allRoutes.stream()
                .filter(this::isValidRoute)
                .collect(Collectors.toList());
    }

    private void findRoutes(Location current, Location destination, List<Transportation> currentRoute,
                            List<List<Transportation>> allRoutes, LocalDate date,
                            Set<Location> visited, boolean hasFlight) {

        if (current.equals(destination)) {
            allRoutes.add(new ArrayList<>(currentRoute));
            return;
        }

        if (currentRoute.size() > 3 || visited.contains(current)) {
            return;
        }

        visited.add(current);

        List<Transportation> possibleTransports = transportationRepository.findByOriginLocation(current);

        for (Transportation transport : possibleTransports) {
            if (!isTransportAvailable(transport, date)) {
                continue;
            }

            if (hasFlight && transport.getTransportationType() == Transportation.TransportationType.FLIGHT) {
                continue;
            }

            currentRoute.add(transport);

            boolean nextHasFlight = hasFlight || transport.getTransportationType() == Transportation.TransportationType.FLIGHT;

            findRoutes(transport.getDestinationLocation(), destination, currentRoute, allRoutes, date, visited, nextHasFlight);

            currentRoute.remove(currentRoute.size() - 1);
        }

        visited.remove(current);
    }


    private boolean isTransportAvailable(Transportation transport, LocalDate date) {
        System.out.print(transport.getId());
        System.out.print(transport.getOperatingDays() );

        if (transport.getOperatingDays() == null || transport.getOperatingDays().isEmpty()) {
            return true;
        }
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return transport.getOperatingDays().contains(dayOfWeek.getValue());
    }

    private boolean isValidRoute(List<Transportation> route) {
        boolean hasFlight = false;
        int beforeFlightCount = 0;
        int afterFlightCount = 0;

        for (Transportation transport : route) {
            if (transport.getTransportationType() == Transportation.TransportationType.FLIGHT) {
                if (hasFlight) {
                    return false; // Multiple flights
                }
                hasFlight = true;
            } else {
                if (!hasFlight) {
                    beforeFlightCount++;
                } else {
                    afterFlightCount++;
                }
            }
        }

        return hasFlight && beforeFlightCount <= 1 && afterFlightCount <= 1;
    }
} 