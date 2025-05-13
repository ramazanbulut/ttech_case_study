import React from 'react';
import { Drawer, List, ListItem, ListItemIcon, ListItemText, Toolbar } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import {
    LocationOn as LocationIcon,
    DirectionsBus as TransportationIcon,
    Route as RouteIcon,
} from '@mui/icons-material';

const drawerWidth = 240;

const Sidebar: React.FC = () => {
    const navigate = useNavigate();

    const menuItems = [
        { text: 'Locations', icon: <LocationIcon />, path: '/locations' },
        { text: 'Transportations', icon: <TransportationIcon />, path: '/transportations' },
        { text: 'Routes', icon: <RouteIcon />, path: '/routes' },
    ];

    return (
        <Drawer
            variant="permanent"
            sx={{
                width: drawerWidth,
                flexShrink: 0,
                '& .MuiDrawer-paper': {
                    width: drawerWidth,
                    boxSizing: 'border-box',
                },
            }}
        >
            <Toolbar />
            <List>
                {menuItems.map((item) => (
                    <ListItem
                        key={item.text}
                        onClick={() => navigate(item.path)}
                        sx={{ cursor: 'pointer' }}
                    >
                        <ListItemIcon>{item.icon}</ListItemIcon>
                        <ListItemText primary={item.text} />
                    </ListItem>
                ))}
            </List>
        </Drawer>
    );
};

export default Sidebar; 