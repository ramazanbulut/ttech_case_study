import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import MainLayout from './components/layout/MainLayout';
import LocationsPage from './pages/LocationsPage';
import TransportationsPage from './pages/TransportationsPage';
import RoutesPage from './pages/RoutesPage';

function App() {
  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Router>
        <MainLayout>
          <Routes>
            <Route path="/" element={<RoutesPage />} />
            <Route path="/locations" element={<LocationsPage />} />
            <Route path="/transportations" element={<TransportationsPage />} />
            <Route path="/routes" element={<RoutesPage />} />
          </Routes>
        </MainLayout>
      </Router>
    </LocalizationProvider>
  );
}

export default App;
