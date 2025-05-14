package com.ttech.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionMessages {
    // General Exception Messages
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred: %s";
    
    // Validation Exception Messages
    public static final String INVALID_PARAMETER_VALUE = "Invalid value '%s' for parameter '%s'.";
    public static final String VALID_VALUES_ARE = " Valid values are: [%s].";
    public static final String EXPECTED_TYPE = " Expected type: %s.";
    
    // Location Service Messages
    public static final String LOCATION_NOT_FOUND = "Location not found with id: %d";
    public static final String LOCATION_CODE_EXISTS = "Location code already exists: %s";
    public static final String LOCATION_DELETE_CONFLICT = "This location cannot be deleted because it is used in one or more transportations.";
    
    public static final String TRANSPORTATION_NOT_FOUND = "Transportation not found with id: %d";
    public static final String INVALID_ORIGIN_LOCATION = "Invalid originLocationCode: %s";
    public static final String INVALID_DESTINATION_LOCATION = "Invalid destinationLocationCode: %s";
}