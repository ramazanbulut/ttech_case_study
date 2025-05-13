export interface Location {
    id: number;
    name: string;
    country: string;
    city: string;
    locationCode: string;
}

export interface Page<T> {
    content: T[];
    pageable: {
        pageNumber: number;
        pageSize: number;
        sort: {
            empty: boolean;
            sorted: boolean;
            unsorted: boolean;
        };
        offset: number;
        paged: boolean;
        unpaged: boolean;
    };
    totalPages: number;
    totalElements: number;
    last: boolean;
    size: number;
    number: number;
    sort: {
        empty: boolean;
        sorted: boolean;
        unsorted: boolean;
    };
    first: boolean;
    numberOfElements: number;
    empty: boolean;
}

export interface Transportation {
    id: number;
    originLocation: Location;
    destinationLocation: Location;
    transportationType: TransportationType;
    operatingDays?: number[];
}

export enum TransportationType {
    FLIGHT = 'FLIGHT',
    BUS = 'BUS',
    SUBWAY = 'SUBWAY',
    UBER = 'UBER'
}

export interface RouteSearchRequest {
    originLocationCode: string;
    destinationLocationCode: string;
    date: Date;
}

export interface Route {
    transportations: Transportation[];
} 