import axios, { AxiosError, AxiosResponse } from 'axios';
import { Location, Transportation, RouteSearchRequest, Page } from '../types';

// API response type
export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data: T;
}

// API Service
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Response interceptor
api.interceptors.response.use(
    (response: AxiosResponse) => {
        // Return the entire response object
        return response;
    },
    (error: AxiosError<ApiResponse<any>>) => {
        // Handle different types of errors
        if (error.response) {
            // The request was made and the server responded with a status code
            // that falls out of the range of 2xx
            const errorMessage = error.response.data?.message || 'An error occurred';
            return Promise.reject(new Error(errorMessage));
        } else if (error.request) {
            // The request was made but no response was received
            return Promise.reject(new Error('No response received from server'));
        } else {
            // Something happened in setting up the request that triggered an Error
            return Promise.reject(new Error('Error setting up request'));
        }
    }
);

// Location API
export const locationApi = {
    getAll: async (params?: {
        page?: number;
        size?: number;
        sortBy?: string;
        sortDirection?: 'asc' | 'desc';
        search?: string;
    }): Promise<Page<Location>> => {
        const queryParams = new URLSearchParams();
        if (params?.page !== undefined) {
            queryParams.append('page', params.page.toString());
        }
        if (params?.size !== undefined) {
            queryParams.append('size', params.size.toString());
        }
        if (params?.sortBy) {
            queryParams.append('sortBy', params.sortBy);
        }
        if (params?.sortDirection) {
            queryParams.append('sortOrder', params.sortDirection.toUpperCase());
        }
        if (params?.search) {
            queryParams.append('search', params.search);
        }
        const response = await api.get<ApiResponse<Page<Location>>>(`/locations?${queryParams.toString()}`);
        return response.data.data;
    },

    getById: async (id: number): Promise<Location> => {
        const response = await api.get<ApiResponse<Location>>(`/locations/${id}`);
        return response.data.data;
    },

    create: async (location: Omit<Location, 'id'>): Promise<Location> => {
        const response = await api.post<ApiResponse<Location>>('/locations', location);
        return response.data.data;
    },

    update: async (id: number, location: Partial<Location>): Promise<Location> => {
        const response = await api.put<ApiResponse<Location>>(`/locations/${id}`, location);
        return response.data.data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/locations/${id}`);
    },
};

// Transportation API
export const transportationApi = {
    getAll: async (filters?: {
        originLocationCode?: string;
        destinationLocationCode?: string;
        date?: string | null;
        page?: number;
        size?: number;
    }): Promise<Page<Transportation>> => {
        const params = new URLSearchParams();
        if (filters?.originLocationCode) {
            params.append('originLocationCode', filters.originLocationCode);
        }
        if (filters?.destinationLocationCode) {
            params.append('destinationLocationCode', filters.destinationLocationCode);
        }
        if (filters?.date) {
            params.append('date', filters.date);
        }
        if (filters?.page !== undefined) {
            params.append('page', filters.page.toString());
        }
        if (filters?.size !== undefined) {
            params.append('size', filters.size.toString());
        }
        const response = await api.get<ApiResponse<Page<Transportation>>>(`/transportations?${params.toString()}`);
        return response.data.data;
    },

    getById: async (id: number): Promise<Transportation> => {
        const response = await api.get<ApiResponse<Transportation>>(`/transportations/${id}`);
        return response.data.data;
    },

    create: async (transportation: Omit<Transportation, 'id'>): Promise<Transportation> => {
        const response = await api.post<ApiResponse<Transportation>>('/transportations', transportation);
        return response.data.data;
    },

    update: async (id: number, transportation: Partial<Transportation>): Promise<Transportation> => {
        const response = await api.put<ApiResponse<Transportation>>(`/transportations/${id}`, transportation);
        return response.data.data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/transportations/${id}`);
    },
};

// Route API
export const routeApi = {
    search: async (request: RouteSearchRequest): Promise<Transportation[][]> => {
        const params = new URLSearchParams();
        params.append('originLocationCode', request.originLocationCode);
        params.append('destinationLocationCode', request.destinationLocationCode);
        params.append('date', request.date.toISOString().split('T')[0]);
        const response = await api.get<ApiResponse<Transportation[][]>>(`/routes/search?${params.toString()}`);
        return response.data.data;
    },
};

export default api; 