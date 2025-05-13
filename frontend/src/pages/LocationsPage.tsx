import React, { useEffect, useState } from 'react';
import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField,
    Typography,
    CircularProgress,
    Alert,
    TablePagination,
    TableSortLabel,
} from '@mui/material';
import { Location, Page } from '../types';
import { locationApi } from '../services/api';

const LocationsPage: React.FC = () => {
    const [locations, setLocations] = useState<Page<Location>>({
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
    const [open, setOpen] = useState(false);
    const [formData, setFormData] = useState<Partial<Location>>({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [sortBy, setSortBy] = useState<string>('id');
    const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');
    const [search, setSearch] = useState<string>('');
    const [searchInput, setSearchInput] = useState<string>('');

    useEffect(() => {
        loadLocations();
    }, [page, rowsPerPage, sortBy, sortDirection, search]);

    const loadLocations = async () => {
        try {
            setLoading(true);
            setError(null);
            const locationsData = await locationApi.getAll({
                page,
                size: rowsPerPage,
                sortBy,
                sortDirection,
                search: search.trim() || undefined
            });
            setLocations(locationsData);
        } catch (error) {
            setError('Error loading locations. Please try again later.');
            console.error('Error loading locations:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleChangePage = (event: unknown, newPage: number) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleSort = (property: string) => {
        const isAsc = sortBy === property && sortDirection === 'asc';
        setSortDirection(isAsc ? 'desc' : 'asc');
        setSortBy(property);
    };

    const handleOpen = (location?: Location) => {
        setFormData(location || {});
        setOpen(true);
    };

    const handleClose = () => {
        setError(null)
        setOpen(false);
        setFormData({});
    };

    const handleSubmit = async () => {
        try {
            setError(null);
            if (formData.id) {
                await locationApi.update(formData.id, formData as Omit<Location, 'id'>);
            } else {
                await locationApi.create(formData as Omit<Location, 'id'>);
            }
            handleClose();
            loadLocations();
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Error saving location. Please try again.');
            console.error('Error saving location:', error);
        }
    };

    const handleDelete = async (id: number) => {
        try {
            setError(null);
            await locationApi.delete(id);
            loadLocations();
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Error deleting location. Please try again.');
            console.error('Error deleting location:', error);
        }
    };

    const handleSearch = () => {
        setSearch(searchInput);
        setPage(0); // Reset to first page when search changes
    };

    const handleSearchInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSearchInput(event.target.value);
    };

    const handleClearSearch = () => {
        setSearchInput('');
        setSearch('');
        setPage(0);
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
            {error && !open && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
                <TextField
                    label="Search"
                    variant="outlined"
                    size="small"
                    value={searchInput}
                    onChange={handleSearchInputChange}
                    placeholder="Search by name or location code"
                    sx={{ minWidth: 300 }}
                />
                <Button 
                    variant="contained" 
                    onClick={handleSearch}
                    disabled={!searchInput.trim()}
                >
                    Search
                </Button>
                <Button 
                    variant="outlined" 
                    onClick={handleClearSearch}
                    disabled={!searchInput.trim() && !search.trim()}
                >
                    Clear
                </Button>
                <Button variant="contained" onClick={() => handleOpen()}>
                    Add Location
                </Button>
            </Box>

            {locations.content.length === 0 ? (
                <Paper sx={{ p: 5, mt: 6, textAlign: 'center' }}>
                    <Typography variant="h5" color="textSecondary" sx={{ mb: 2 }}>
                        No locations found.
                    </Typography>
                    <Button variant="contained" color="primary" onClick={() => handleOpen()}>
                        Add Location
                    </Button>
                </Paper>
            ) : (
                <>
                    <TableContainer component={Paper} sx={{ mt: 2 }}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortBy === 'name'}
                                            direction={sortBy === 'name' ? sortDirection : 'asc'}
                                            onClick={() => handleSort('name')}
                                        >
                                            Name
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortBy === 'country'}
                                            direction={sortBy === 'country' ? sortDirection : 'asc'}
                                            onClick={() => handleSort('country')}
                                        >
                                            Country
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortBy === 'city'}
                                            direction={sortBy === 'city' ? sortDirection : 'asc'}
                                            onClick={() => handleSort('city')}
                                        >
                                            City
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortBy === 'locationCode'}
                                            direction={sortBy === 'locationCode' ? sortDirection : 'asc'}
                                            onClick={() => handleSort('locationCode')}
                                        >
                                            Location Code
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>Actions</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {locations.content.map((location) => (
                                    <TableRow key={location.id}>
                                        <TableCell>{location.name}</TableCell>
                                        <TableCell>{location.country}</TableCell>
                                        <TableCell>{location.city}</TableCell>
                                        <TableCell>{location.locationCode}</TableCell>
                                        <TableCell>
                                            <Button onClick={() => handleOpen(location)}>Edit</Button>
                                            <Button onClick={() => handleDelete(location.id)}>Delete</Button>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                        <TablePagination
                            rowsPerPageOptions={[5, 10, 25]}
                            component="div"
                            count={locations.totalElements}
                            rowsPerPage={rowsPerPage}
                            page={page}
                            onPageChange={handleChangePage}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                        />
                    </TableContainer>
                </>
            )}

            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>{formData.id ? 'Edit Location' : 'Add Location'}</DialogTitle>
                <DialogContent>
                    {error && (
                        <div
                            style={{
                                background: '#ff4d4f',
                                color: 'white',
                                padding: '10px 16px',
                                borderRadius: '4px',
                                marginBottom: '16px',
                                textAlign: 'center',
                                fontWeight: 500,
                            }}
                        >
                            {error}
                        </div>
                    )}
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Name"
                        fullWidth
                        value={formData.name || ''}
                        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    />
                    <TextField
                        margin="dense"
                        label="Country"
                        fullWidth
                        value={formData.country || ''}
                        onChange={(e) => setFormData({ ...formData, country: e.target.value })}
                    />
                    <TextField
                        margin="dense"
                        label="City"
                        fullWidth
                        value={formData.city || ''}
                        onChange={(e) => setFormData({ ...formData, city: e.target.value })}
                    />
                    <TextField
                        margin="dense"
                        label="Location Code"
                        fullWidth
                        value={formData.locationCode || ''}
                        onChange={(e) => setFormData({ ...formData, locationCode: e.target.value })}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose}>Cancel</Button>
                    <Button onClick={handleSubmit}>Save</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default LocationsPage; 