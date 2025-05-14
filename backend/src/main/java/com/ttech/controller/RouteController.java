package com.ttech.controller;

import com.ttech.dto.responses.ApiResponse;
import com.ttech.dto.requests.RouteSearchRequest;
import com.ttech.model.Transportation;
import com.ttech.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@Tag(name = "Route Management", description = "APIs for finding routes between locations")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/search")
    @Operation(summary = "Find valid routes between two locations")
    public ResponseEntity<ApiResponse<List<List<Transportation>>>> findRoutes(@Valid @ModelAttribute RouteSearchRequest request) {
        List<List<Transportation>> routes = routeService.findValidRoutes(request);
        return ResponseEntity.ok(ApiResponse.success(routes));
    }
} 