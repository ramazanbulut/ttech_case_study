package com.ttech.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttech.dto.ApiResponse;
import com.ttech.dto.RouteSearchRequest;
import com.ttech.model.Location;
import com.ttech.model.Transportation;
import com.ttech.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RouteController.class)
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RouteService routeService;

    private Location istanbul;
    private Location ankara;
    private Location izmir;
    private Transportation flight;
    private Transportation bus;

    @BeforeEach
    void setUp() {
        // Setup test locations
        istanbul = new Location();
        istanbul.setId(1L);
        istanbul.setLocationCode("IST");
        istanbul.setName("Istanbul");
        istanbul.setCity("Istanbul");
        istanbul.setCountry("Turkey");

        ankara = new Location();
        ankara.setId(2L);
        ankara.setLocationCode("ANK");
        ankara.setName("Ankara");
        ankara.setCity("Ankara");
        ankara.setCountry("Turkey");

        izmir = new Location();
        izmir.setId(3L);
        izmir.setLocationCode("IZM");
        izmir.setName("Izmir");
        izmir.setCity("Izmir");
        izmir.setCountry("Turkey");

        // Setup test transportation
        flight = new Transportation();
        flight.setId(1L);
        flight.setOriginLocation(istanbul);
        flight.setDestinationLocation(ankara);
        flight.setTransportationType(Transportation.TransportationType.FLIGHT);
        flight.setOperatingDays(Arrays.asList(1, 2, 3, 4, 5, 6, 7)); // Every day

        bus = new Transportation();
        bus.setId(2L);
        bus.setOriginLocation(ankara);
        bus.setDestinationLocation(izmir);
        bus.setTransportationType(Transportation.TransportationType.BUS);
        bus.setOperatingDays(Arrays.asList(1, 2, 3, 4, 5, 6, 7)); // Every day
    }

    @Test
    void findRoutes_Successful() throws Exception {
        // Given
        RouteSearchRequest request = new RouteSearchRequest();
        request.setOriginLocationCode("IST");
        request.setDestinationLocationCode("IZM");
        request.setDate(LocalDate.now());

        List<List<Transportation>> expectedRoutes = Collections.singletonList(
                Arrays.asList(flight, bus)
        );

        when(routeService.findValidRoutes(any(RouteSearchRequest.class)))
                .thenReturn(expectedRoutes);

        // When & Then
        mockMvc.perform(post("/api/routes/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0][0].transportationType").value("FLIGHT"))
                .andExpect(jsonPath("$.data[0][1].transportationType").value("BUS"));
    }

    @Test
    void findRoutes_SuccessfulWithoutDate() throws Exception {
        // Given
        RouteSearchRequest request = new RouteSearchRequest();
        request.setOriginLocationCode("IST");
        request.setDestinationLocationCode("IZM");
        // Date is not set

        List<List<Transportation>> expectedRoutes = Collections.singletonList(
                Arrays.asList(flight, bus)
        );

        when(routeService.findValidRoutes(any(RouteSearchRequest.class)))
                .thenReturn(expectedRoutes);

        // When & Then
        mockMvc.perform(post("/api/routes/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0][0].transportationType").value("FLIGHT"))
                .andExpect(jsonPath("$.data[0][1].transportationType").value("BUS"));
    }

    @Test
    void findRoutes_InvalidLocationCode() throws Exception {
        // Given
        RouteSearchRequest request = new RouteSearchRequest();
        request.setOriginLocationCode("INVALID");
        request.setDestinationLocationCode("IZM");
        // Date is optional, not setting it

        when(routeService.findValidRoutes(any(RouteSearchRequest.class)))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(post("/api/routes/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void findRoutes_InvalidRequest() throws Exception {
        // Given
        RouteSearchRequest request = new RouteSearchRequest();
        // Missing required fields (origin and destination)

        // When & Then
        mockMvc.perform(post("/api/routes/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void findRoutes_NoRoutesFound() throws Exception {
        // Given
        RouteSearchRequest request = new RouteSearchRequest();
        request.setOriginLocationCode("IST");
        request.setDestinationLocationCode("IZM");
        // Date is optional, not setting it

        when(routeService.findValidRoutes(any(RouteSearchRequest.class)))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(post("/api/routes/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
} 