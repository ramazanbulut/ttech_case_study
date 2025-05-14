import React, { useEffect, useState } from 'react';
import {
    Box,
    Button,
    FormControl,
    InputLabel,
    MenuItem,
    Paper,
    Select,
    Typography,
    TextField,
    InputAdornment,
    Autocomplete,
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers';
import { Location, Transportation, Page, TransportationType } from '../types';
import { locationApi, routeApi } from '../services/api';
import SearchIcon from '@mui/icons-material/Search';
import RouteDetailsDrawer from '../components/RouteDetailsDrawer';

const RoutesPage: React.FC = () => {
    const [locations, setLocations] = useState<Location[]>([]);
    const [origin, setOrigin] = useState<Location | null>(null);
    const [destination, setDestination] = useState<Location | null>(null);
    const [date, setDate] = useState<Date | null>(new Date());
    const [routes, setRoutes] = useState<Transportation[][]>([]);
    const [originSearchResults, setOriginSearchResults] = useState<Location[]>([]);
    const [destinationSearchResults, setDestinationSearchResults] = useState<Location[]>([]);
    const [searchLoading, setSearchLoading] = useState(false);
    const [openRouteIndex, setOpenRouteIndex] = useState<number | null>(null);
    const [hasSearched, setHasSearched] = useState(false);

    useEffect(() => {
        loadLocations();
    }, []);

    const loadLocations = async () => {
        try {
            const locationsData = await locationApi.getAll();
            setLocations(locationsData.content);
        } catch (error) {
            console.error('Error loading locations:', error);
        }
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

    const handleSearch = async () => {
        if (!origin || !destination || !date) return;

        try {
            setHasSearched(true);
            const routes = await routeApi.search({
                originLocationCode: origin.locationCode,
                destinationLocationCode: destination.locationCode,
                date: date,
            });
            setRoutes(routes);
        } catch (error) {
            console.error('Error searching routes:', error);
        }
    };

    return (
        <Box>
            <Box sx={{ display: 'flex', gap: 2, mb: 4 }}>
                <FormControl fullWidth>
                    <Autocomplete
                        options={originSearchResults}
                        getOptionLabel={(option) => `${option.name} (${option.locationCode})`}
                        value={origin}
                        onChange={(_, newValue) => {
                            setOrigin(newValue);
                        }}
                        onInputChange={(_, newInputValue) => {
                            searchLocations(newInputValue, true);
                        }}
                        loading={searchLoading}
                        renderInput={(params) => (
                            <TextField
                                {...params}
                                label="Origin Location"
                                placeholder="Search origin location..."
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
                    />
                </FormControl>

                <FormControl fullWidth>
                    <Autocomplete
                        options={destinationSearchResults}
                        getOptionLabel={(option) => `${option.name} (${option.locationCode})`}
                        value={destination}
                        onChange={(_, newValue) => {
                            setDestination(newValue);
                        }}
                        onInputChange={(_, newInputValue) => {
                            searchLocations(newInputValue, false);
                        }}
                        loading={searchLoading}
                        renderInput={(params) => (
                            <TextField
                                {...params}
                                label="Destination Location"
                                placeholder="Search destination location..."
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
                    />
                </FormControl>

                <DatePicker
                    label="Date"
                    value={date}
                    onChange={(newDate) => setDate(newDate)}
                />

                <Button
                    variant="contained"
                    onClick={handleSearch}
                    disabled={!origin || !destination || !date}
                >
                    Search Routes
                </Button>
            </Box>

            {/* Empty state message */}
            {routes.length === 0 && (
                <Paper sx={{ p: 3, mt: 2, textAlign: 'center' }}>
                    <Typography variant="h6" color="textSecondary">
                        {hasSearched 
                            ? "No routes found for the selected criteria. Please try different locations or date."
                            : "Please select origin, destination and date, then search to find available routes."}
                    </Typography>
                </Paper>
            )}

            {/* Route list with via locations */}
            {routes.map((route, index) => {
                const flightStep = route.find((t) => t.transportationType === "FLIGHT");
                const viaLocation = flightStep ? flightStep.destinationLocation : null;
                return (
                    <Paper key={index} sx={{ p: 2, mb: 2 }}>
                        <Typography variant="h6" gutterBottom>
                            Route {index + 1}
                        </Typography>
                        {viaLocation && (
                            <Button variant="outlined" onClick={() => setOpenRouteIndex(index)}>
                                Via {viaLocation.name} ({viaLocation.locationCode})
                            </Button>
                        )}
                    </Paper>
                );
            })}

            <RouteDetailsDrawer
                open={openRouteIndex !== null}
                onClose={() => setOpenRouteIndex(null)}
                route={openRouteIndex !== null ? routes[openRouteIndex] : null}
            />
        </Box>
    );
};

export default RoutesPage; 