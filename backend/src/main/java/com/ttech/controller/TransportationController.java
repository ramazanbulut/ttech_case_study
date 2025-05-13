package com.ttech.controller;

import com.ttech.dto.ApiResponse;
import com.ttech.dto.TransportationFilterRequest;
import com.ttech.model.Transportation;
import com.ttech.service.TransportationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transportations")
@Tag(name = "Transportation Management", description = "APIs for managing transportations")
@RequiredArgsConstructor
public class TransportationController {

    private final TransportationService transportationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Transportation>>> getAllTransportations(
            @Valid @ModelAttribute TransportationFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Transportation> results = transportationService.getAllTransportations(filter, pageable);
            return ResponseEntity.ok(ApiResponse.success(results));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transportation by ID")
    public ResponseEntity<ApiResponse<Transportation>> getById(@PathVariable Long id) {
        Transportation transportation = transportationService.getTransportationById(id);
        return ResponseEntity.ok(ApiResponse.success(transportation));
    }

    @PostMapping
    @Operation(summary = "Create a new transportation")
    public ResponseEntity<ApiResponse<Transportation>> createTransportation(
            @RequestBody Transportation transportation) {
        try {
            Transportation created = transportationService.createTransportation(transportation);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred while creating the transportation"));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing transportation")
    public ResponseEntity<ApiResponse<Transportation>> updateTransportation(@PathVariable Long id,
            @RequestBody Transportation details) {
        try {
            Transportation updated = transportationService.updateTransportation(id, details);
            return ResponseEntity.ok(ApiResponse.success(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred while updating the transportation"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a transportation")
    public ResponseEntity<ApiResponse<Void>> deleteTransportation(@PathVariable Long id) {
        try {
            transportationService.deleteTransportation(id);
            return ResponseEntity.ok(ApiResponse.successVoid());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }
}
