package com.ttech.dto.requests;

import lombok.Data;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class TransportationFilterRequest {
    private String originLocationCode;
    private String destinationLocationCode;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
}
