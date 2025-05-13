import React, { useEffect, useState } from 'react';
import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    FormControl,
    InputLabel,
    MenuItem,
    Paper,
    Select,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
    CircularProgress,
    Alert,
    TablePagination,
    TextField,
    InputAdornment,
    Autocomplete,
} from '@mui/material';
import { Location, Transportation, TransportationType, Page } from '../types';
import { locationApi, transportationApi } from '../services/api';
import TransportationFilter from '../components/TransportationFilter';
import SearchIcon from '@mui/icons-material/Search';

const TransportationsPage: React.FC = () => {
    const [transportations, setTransportations] = useState<Page<Transportation>>({
        content: [],
        pageable: {
            pageNumber: 0,
            pageSize: 10,
            sort: { empty: true, sorted: false, unsorted: true },
            offset: 0,
            paged: true,
            unpaged: false
        },
        totalPages: 0,
        totalElements: 0,
        last: true,
        size: 10,
        number: 0,
        sort: { empty: true, sorted: false, unsorted: true },
        first: true,
        numberOfElements: 0,
        empty: true
    });
    const [locations, setLocations] = useState<Location[]>([]);
    const [open, setOpen] = useState(false);
    const [formData, setFormData] = useState<Partial<Transportation>>({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [filters, setFilters] = useState<{
        originLocationCode: string;
        destinationLocationCode: string;
        date: string | null;
    }>({
        originLocationCode: '',
        destinationLocationCode: '',
        date: null
    });
    const [originSearch, setOriginSearch] = useState('');
    const [destinationSearch, setDestinationSearch] = useState('');
    const [originSearchResults, setOriginSearchResults] = useState<Location[]>([]);
    const [destinationSearchResults, setDestinationSearchResults] = useState<Location[]>([]);
    const [searchLoading, setSearchLoading] = useState(false);

    useEffect(() => {
        loadData();
    }, [page, rowsPerPage, filters]);

    const loadData = async () => {
        try {
            setError(null);
            const [transportationsData, locationsData] = await Promise.all([
                transportationApi.getAll({
                    ...filters,
                    page,
                    size: rowsPerPage
                }),
                locationApi.getAll(),
            ]);
            setTransportations(transportationsData);
            setLocations(locationsData.content);
        } catch (error) {
            setError('Error loading data. Please try again later.');
            console.error('Error loading data:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleFilterChange = (newFilters: {
        originLocationCode: string;
        destinationLocationCode: string;
        date: string | null;
    }) => {
        setFilters(newFilters);
        setPage(0); // Reset to first page when filters change
    };

    const handleChangePage = (event: unknown, newPage: number) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleOpen = (transportation?: Transportation) => {
        setFormData(transportation || {});
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
        setFormData({});
    };

    const handleSubmit = async () => {
        try {
            setError(null);
            if (formData.id) {
                await transportationApi.update(formData.id, formData as Partial<Transportation>);
            } else {
                await transportationApi.create(formData as Omit<Transportation, 'id'>);
            }
            handleClose();
            loadData();
        } catch (error) {
            setError('Error saving transportation. Please try again.');
            console.error('Error saving transportation:', error);
        }
    };

    const handleDelete = async (id: number) => {
        try {
            setError(null);
            await transportationApi.delete(id);
            loadData();
        } catch (error) {
            setError('Error deleting transportation. Please try again.');
            console.error('Error deleting transportation:', error);
        }
    };

    const filteredLocations = (searchTerm: string) => {
        return locations.filter(location => 
            location.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            location.locationCode.toLowerCase().includes(searchTerm.toLowerCase())
        );
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

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box>
            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            <TransportationFilter
                onFilterChange={handleFilterChange}
                locations={locations}
            />

            {loading ? (
                <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
                    <CircularProgress />
                </Box>
            ) : transportations.content.length === 0 ? (
                <Paper sx={{ p: 5, mt: 6, textAlign: 'center' }}>
                    <Typography variant="h5" color="textSecondary" sx={{ mb: 2 }}>
                        No transportations found.
                    </Typography>
                    <Button variant="contained" color="primary" onClick={() => handleOpen()}>
                        Add Transportation
                    </Button>
                </Paper>
            ) : (
                <>
                    <Button variant="contained" onClick={() => handleOpen()}>
                        Add Transportation
                    </Button>
                    <TableContainer component={Paper} sx={{ mt: 2 }}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Origin</TableCell>
                                    <TableCell>Destination</TableCell>
                                    <TableCell>Type</TableCell>
                                    <TableCell>Operating Days</TableCell>
                                    <TableCell>Actions</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {transportations.content.map((transportation) => (
                                    <TableRow key={transportation.id}>
                                        <TableCell>
                                            {transportation.originLocation.name} ({transportation.originLocation.locationCode})
                                        </TableCell>
                                        <TableCell>
                                            {transportation.destinationLocation.name} ({transportation.destinationLocation.locationCode})
                                        </TableCell>
                                        <TableCell>{transportation.transportationType}</TableCell>
                                        <TableCell>
                                            {transportation.operatingDays?.map(day => 
                                                ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'][day - 1]
                                            ).join(', ')}
                                        </TableCell>
                                        <TableCell>
                                            <Button onClick={() => handleOpen(transportation)}>Edit</Button>
                                            <Button onClick={() => handleDelete(transportation.id)}>Delete</Button>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                        <TablePagination
                            rowsPerPageOptions={[5, 10, 25]}
                            component="div"
                            count={transportations.totalElements}
                            rowsPerPage={rowsPerPage}
                            page={page}
                            onPageChange={handleChangePage}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                        />
                    </TableContainer>
                </>
            )}

            <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
                <DialogTitle>
                    {formData.id ? 'Edit Transportation' : 'Add Transportation'}
                </DialogTitle>
                <DialogContent>
                    <FormControl fullWidth sx={{ mt: 2 }}>
                        <Autocomplete
                            options={originSearchResults}
                            getOptionLabel={(option) => `${option.name} (${option.locationCode})`}
                            value={formData.originLocation || null}
                            onChange={(_, newValue) => {
                                setFormData({ ...formData, originLocation: newValue || undefined });
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

                    <FormControl fullWidth sx={{ mt: 2 }}>
                        <Autocomplete
                            options={destinationSearchResults}
                            getOptionLabel={(option) => `${option.name} (${option.locationCode})`}
                            value={formData.destinationLocation || null}
                            onChange={(_, newValue) => {
                                setFormData({ ...formData, destinationLocation: newValue || undefined });
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

                    <FormControl fullWidth sx={{ mt: 2 }}>
                        <InputLabel>Transportation Type</InputLabel>
                        <Select
                            value={formData.transportationType || ''}
                            label="Transportation Type"
                            onChange={(e) => setFormData({ ...formData, transportationType: e.target.value as TransportationType })}
                        >
                            {Object.values(TransportationType).map((type) => (
                                <MenuItem key={type} value={type}>
                                    {type}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <FormControl fullWidth sx={{ mt: 2 }}>
                        <InputLabel>Operating Days</InputLabel>
                        <Select
                            multiple
                            value={formData.operatingDays || []}
                            label="Operating Days"
                            onChange={(e) => setFormData({ ...formData, operatingDays: e.target.value as number[] })}
                        >
                            {[
                                { value: 1, label: 'Monday' },
                                { value: 2, label: 'Tuesday' },
                                { value: 3, label: 'Wednesday' },
                                { value: 4, label: 'Thursday' },
                                { value: 5, label: 'Friday' },
                                { value: 6, label: 'Saturday' },
                                { value: 7, label: 'Sunday' },
                            ].map((day) => (
                                <MenuItem key={day.value} value={day.value}>
                                    {day.label}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose}>Cancel</Button>
                    <Button onClick={handleSubmit}>Save</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default TransportationsPage; 