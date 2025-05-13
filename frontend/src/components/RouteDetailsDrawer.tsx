import React from 'react';
import {
    Box,
    Button,
    Drawer,
    Typography,
} from '@mui/material';
import { Transportation } from '../types';

interface RouteDetailsDrawerProps {
    open: boolean;
    onClose: () => void;
    route: Transportation[] | null;
}

const RouteDetailsDrawer: React.FC<RouteDetailsDrawerProps> = ({ open, onClose, route }) => {
    if (!route) return null;

    const allSteps = [
        ...route.map((t) => ({
            label: `${t.originLocation.name} (${t.originLocation.locationCode})`,
            type: t.transportationType,
            isLast: false,
        })),
        {
            label: `${route[route.length - 1].destinationLocation.name} (${route[route.length - 1].destinationLocation.locationCode})`,
            type: null,
            isLast: true,
        }
    ];

    return (
        <Drawer
            anchor="right"
            open={open}
            onClose={onClose}
        >
            <Box sx={{ width: 350, p: 3 }}>
                <Typography variant="h6" gutterBottom>
                    Route Details
                </Typography>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                    {allSteps.map((step, idx) => (
                        <Box
                            key={idx}
                            sx={{ display: 'flex', position: 'relative', gap: 2 }}
                        >
                            {/* Timeline Icon + Line */}
                            <Box sx={{
                                display: 'flex',
                                flexDirection: 'column',
                                alignItems: 'center',
                                position: 'relative'
                            }}>
                                <Box sx={{
                                    width: 24,
                                    height: 24,
                                    borderRadius: '50%',
                                    border: '2px solid #000',
                                    backgroundColor: '#fff',
                                    zIndex: 1
                                }} />
                                {!step.isLast && (
                                    <Box sx={{
                                        width: 2,
                                        flexGrow: 1,
                                        mt: 0.5,
                                        mb: -1.5,
                                        borderLeft: '2px dotted #aaa',
                                    }} />
                                )}
                            </Box>

                            {/* Content */}
                            <Box sx={{ flexGrow: 1 }}>
                                <Typography fontWeight="bold">
                                    {step.label}
                                </Typography>
                                {step.type && (
                                    <Typography variant="body2" marginY={3} color="text.secondary">
                                        {step.type}
                                    </Typography>
                                )}
                            </Box>
                        </Box>
                    ))}
                </Box>
                <Button onClick={onClose} sx={{ mt: 2 }}>
                    Close
                </Button>
            </Box>
        </Drawer>
    );
};

export default RouteDetailsDrawer; 