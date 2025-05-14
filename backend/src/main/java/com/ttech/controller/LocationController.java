package com.ttech.controller;

import com.ttech.dto.responses.ApiResponse;
import com.ttech.model.Location;
import com.ttech.model.Location.SortBy;
import com.ttech.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/locations")
@Tag(name = "Location Management", description = "APIs for managing locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    @Operation(summary = "Get all locations with pagination and search")
    public ResponseEntity<ApiResponse<Page<Location>>> getAllLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") SortBy sortBy,
            @Valid @RequestParam(defaultValue = "ASC") Sort.Direction sortOrder,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrder, sortBy.toString()));
        Page<Location> locations = locationService.getAllLocations(search, pageable);
        return ResponseEntity.ok(ApiResponse.success(locations));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get location by ID")
    public ResponseEntity<ApiResponse<Location>> getLocationById(@PathVariable Long id) {
        Location location = locationService.getLocationById(id);
        return ResponseEntity.ok(ApiResponse.success(location));
    }

    @PostMapping
    @Operation(summary = "Create a new location")
    public ResponseEntity<ApiResponse<Location>> createLocation(@Valid @RequestBody Location location) {
        Location savedLocation = locationService.createLocation(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(savedLocation));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing location")
    public ResponseEntity<ApiResponse<Location>> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody Location locationDetails) {

        Location updatedLocation = locationService.updateLocation(id, locationDetails);
        return ResponseEntity.ok(ApiResponse.success(updatedLocation));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a location")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.ok(ApiResponse.successVoid());
    }
}
