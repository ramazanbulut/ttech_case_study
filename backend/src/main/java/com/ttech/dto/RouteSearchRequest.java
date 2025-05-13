package com.ttech.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RouteSearchRequest {
    @NotBlank(message = "Origin location code is required")
    private String originLocationCode;

    @NotBlank(message = "Destination location code is required")
    private String destinationLocationCode;

    private LocalDate date; // Optional date field

    public RouteSearchRequest() {
    }

    public RouteSearchRequest(String originLocationCode, String destinationLocationCode, LocalDate date) {
        this.originLocationCode = originLocationCode;
        this.destinationLocationCode = destinationLocationCode;
        this.date = date;
    }
}
