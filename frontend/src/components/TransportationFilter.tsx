import React, { useState } from 'react';
import { Box, Button, Stack } from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { Autocomplete, TextField, InputAdornment } from '@mui/material';
import { Location } from '../types';
import SearchIcon from '@mui/icons-material/Search';
import { locationApi } from '../services/api';

interface Props {
    onFilterChange: (filters: {
        originLocationCode: string;
        destinationLocationCode: string;
        date: string | null;
    }) => void;
    locations: Location[];
}

const TransportationFilter: React.FC<Props> = ({ onFilterChange, locations }) => {
    const [originLocation, setOriginLocation] = useState<Location | null>(null);
    const [destinationLocation, setDestinationLocation] = useState<Location | null>(null);
    const [date, setDate] = useState<Date | null>(null);
    const [originSearchResults, setOriginSearchResults] = useState<Location[]>([]);
    const [destinationSearchResults, setDestinationSearchResults] = useState<Location[]>([]);
    const [searchLoading, setSearchLoading] = useState(false);

    const handleFilter = () => {
        onFilterChange({
            originLocationCode: originLocation?.locationCode || '',
            destinationLocationCode: destinationLocation?.locationCode || '',
            date: date
                ? `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
                : null,
        });
    };

    const handleReset = () => {
        setOriginLocation(null);
        setDestinationLocation(null);
        setDate(null);
        onFilterChange({
            originLocationCode: '',
            destinationLocationCode: '',
            date: null,
        });
    };

    const searchLocations = async (searchTerm: string, isOrigin: boolean) => {
        if (!searchTerm) {
            if (isOrigin) {
                setOriginSearchResults([]);
            } else {
                setDestinationSearchResults([]);
            }
            return;
        }

        try {
            setSearchLoading(true);
            const response = await locationApi.getAll({ search: searchTerm });
            if (isOrigin) {
                setOriginSearchResults(response.content);
            } else {
                setDestinationSearchResults(response.content);
            }
        } catch (error) {
            console.error('Error searching locations:', error);
            if (isOrigin) {
                setOriginSearchResults([]);
            } else {
                setDestinationSearchResults([]);
            }
        } finally {
            setSearchLoading(false);
        }
    };

    return (
        <Box
            sx={{
                display: 'flex',
                flexWrap: 'wrap',
                alignItems: 'flex-end',
                gap: 2,
                background: 'white',
                p: 3,
                borderRadius: 2,
                boxShadow: 2,
                mb: 3,
            }}
        >
            <Autocomplete
                value={originLocation}
                onChange={(event, newValue) => {
                    setOriginLocation(newValue);
                }}
                options={originSearchResults}
                getOptionLabel={(option) => `${option.name} (${option.locationCode})`}
                onInputChange={(_, newInputValue) => {
                    searchLocations(newInputValue, true);
                }}
                loading={searchLoading}
                renderInput={(params) => (
                    <TextField
                        {...params}
                        label="Origin"
                        size="small"
                        sx={{ minWidth: 250 }}
                        InputProps={{
                            ...params.InputProps,
                            startAdornment: (
                                <>
                                    <InputAdornment position="start">
                                        <SearchIcon />
                                    </InputAdornment>
                                    {params.InputProps.startAdornment}
                                </>
                            ),
                        }}
                    />
                )}
                isOptionEqualToValue={(option, value) => option.id === value.id}
            />

            <Autocomplete
                value={destinationLocation}
                onChange={(event, newValue) => {
                    setDestinationLocation(newValue);
                }}
                options={destinationSearchResults}
                getOptionLabel={(option) => `${option.name} (${option.locationCode})`}
                onInputChange={(_, newInputValue) => {
                    searchLocations(newInputValue, false);
                }}
                loading={searchLoading}
                renderInput={(params) => (
                    <TextField
                        {...params}
                        label="Destination"
                        size="small"
                        sx={{ minWidth: 250 }}
                        InputProps={{
                            ...params.InputProps,
                            startAdornment: (
                                <>
                                    <InputAdornment position="start">
                                        <SearchIcon />
                                    </InputAdornment>
                                    {params.InputProps.startAdornment}
                                </>
                            ),
                        }}
                    />
                )}
                isOptionEqualToValue={(option, value) => option.id === value.id}
            />

            <DatePicker
                label="Date"
                value={date}
                onChange={(newValue) => setDate(newValue)}
                slotProps={{
                    textField: {
                        size: 'small',
                        sx: { minWidth: 200 }
                    }
                }}
            />

            <Stack direction="row" spacing={2}>
                <Button variant="contained" onClick={handleFilter}>
                    Filter
                </Button>
                <Button variant="outlined" onClick={handleReset}>
                    Reset
                </Button>
            </Stack>
        </Box>
    );
};

export default TransportationFilter;