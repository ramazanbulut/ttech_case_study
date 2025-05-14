package com.ttech.service;

import com.ttech.dto.requests.RouteSearchRequest;
import com.ttech.model.Location;
import com.ttech.model.Transportation;
import com.ttech.repository.LocationRepository;
import com.ttech.repository.TransportationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private TransportationRepository transportationRepository;

    @InjectMocks
    private RouteService routeService;

    private Location istanbul, ankara, izmir;
    private final LocalDate fixedDate = LocalDate.of(2024, 5, 13);

    @BeforeEach
    void setUp() {
        istanbul = createLocation(1L, "IST", "Istanbul");
        ankara = createLocation(2L, "ANK", "Ankara");
        izmir = createLocation(3L, "IZM", "Izmir");
    }

    private Location createLocation(Long id, String code, String name) {
        Location l = new Location();
        l.setId(id);
        l.setLocationCode(code);
        l.setName(name);
        l.setCity(name);
        l.setCountry("Turkey");
        return l;
    }

    private Transportation createTransport(Location from, Location to, Transportation.TransportationType type,
            List<Integer> days) {
        Transportation t = new Transportation();
        t.setId((long) (Math.random() * 1000));
        t.setOriginLocation(from);
        t.setDestinationLocation(to);
        t.setTransportationType(type);
        t.setOperatingDays(days);
        return t;
    }

    @Test
    void shouldReturnValidRoute_WhenFlightAndBusExist() {
        RouteSearchRequest request = new RouteSearchRequest("IST", "IZM", fixedDate);

        Transportation flight = createTransport(istanbul, ankara, Transportation.TransportationType.FLIGHT, allDays());
        Transportation bus = createTransport(ankara, izmir, Transportation.TransportationType.BUS, allDays());

        when(locationRepository.findByLocationCode("IST")).thenReturn(istanbul);
        when(locationRepository.findByLocationCode("IZM")).thenReturn(izmir);
        when(transportationRepository.findByOriginLocation(istanbul)).thenReturn(List.of(flight));
        when(transportationRepository.findByOriginLocation(ankara)).thenReturn(List.of(bus));

        List<List<Transportation>> routes = routeService.findValidRoutes(request);

        assertEquals(1, routes.size());
        assertEquals(2, routes.get(0).size());
    }

    @Test
    void shouldUseToday_WhenDateNotProvided() {
        RouteSearchRequest request = new RouteSearchRequest("IST", "IZM", null);

        Transportation flight = createTransport(istanbul, ankara, Transportation.TransportationType.FLIGHT, allDays());
        Transportation bus = createTransport(ankara, izmir, Transportation.TransportationType.BUS, allDays());

        when(locationRepository.findByLocationCode("IST")).thenReturn(istanbul);
        when(locationRepository.findByLocationCode("IZM")).thenReturn(izmir);
        when(transportationRepository.findByOriginLocation(istanbul)).thenReturn(List.of(flight));
        when(transportationRepository.findByOriginLocation(ankara)).thenReturn(List.of(bus));

        List<List<Transportation>> routes = routeService.findValidRoutes(request);

        assertEquals(1, routes.size());
    }

    @Test
    void shouldThrowException_WhenOriginLocationInvalid() {
        RouteSearchRequest request = new RouteSearchRequest("INVALID", "IZM", fixedDate);

        when(locationRepository.findByLocationCode("INVALID")).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            routeService.findValidRoutes(request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid originLocationCode: INVALID", exception.getReason());
    }


    @Test
    void shouldReturnEmpty_WhenNoTransportsAvailable() {
        RouteSearchRequest request = new RouteSearchRequest("IST", "IZM", fixedDate);

        when(locationRepository.findByLocationCode("IST")).thenReturn(istanbul);
        when(locationRepository.findByLocationCode("IZM")).thenReturn(izmir);
        when(transportationRepository.findByOriginLocation(any())).thenReturn(Collections.emptyList());

        List<List<Transportation>> routes = routeService.findValidRoutes(request);

        assertTrue(routes.isEmpty());
    }

    @Test
    void shouldIgnoreSubwayOnWeekend() {
        RouteSearchRequest request = new RouteSearchRequest("IST", "ANK", LocalDate.of(2024, 3, 3)); // Sunday

        Transportation flight = createTransport(istanbul, ankara, Transportation.TransportationType.FLIGHT, allDays());
        Transportation subway = createTransport(istanbul, ankara, Transportation.TransportationType.SUBWAY,
                weekdaysOnly());

        when(locationRepository.findByLocationCode("IST")).thenReturn(istanbul);
        when(locationRepository.findByLocationCode("ANK")).thenReturn(ankara);
        when(transportationRepository.findByOriginLocation(istanbul)).thenReturn(List.of(flight, subway));

        List<List<Transportation>> routes = routeService.findValidRoutes(request);

        assertEquals(1, routes.size());
        assertEquals(1, routes.get(0).size());
        assertEquals(Transportation.TransportationType.FLIGHT, routes.get(0).get(0).getTransportationType());
    }

    @Test
    void shouldRejectMultipleFlights() {
        RouteSearchRequest request = new RouteSearchRequest("IST", "IZM", fixedDate);

        Transportation flight1 = createTransport(istanbul, ankara, Transportation.TransportationType.FLIGHT, allDays());
        Transportation flight2 = createTransport(ankara, izmir, Transportation.TransportationType.FLIGHT, allDays());

        when(locationRepository.findByLocationCode("IST")).thenReturn(istanbul);
        when(locationRepository.findByLocationCode("IZM")).thenReturn(izmir);
        when(transportationRepository.findByOriginLocation(istanbul)).thenReturn(List.of(flight1));
        when(transportationRepository.findByOriginLocation(ankara)).thenReturn(List.of(flight2));

        List<List<Transportation>> routes = routeService.findValidRoutes(request);

        assertTrue(routes.isEmpty());
    }

    @Test
    void shouldPreventCycles() {
        RouteSearchRequest request = new RouteSearchRequest("IST", "IZM", fixedDate);

        Transportation flight = createTransport(istanbul, ankara, Transportation.TransportationType.FLIGHT, allDays());
        Transportation back = createTransport(ankara, istanbul, Transportation.TransportationType.BUS, allDays());
        Transportation bus = createTransport(ankara, izmir, Transportation.TransportationType.BUS, allDays());

        when(locationRepository.findByLocationCode("IST")).thenReturn(istanbul);
        when(locationRepository.findByLocationCode("IZM")).thenReturn(izmir);
        when(transportationRepository.findByOriginLocation(istanbul)).thenReturn(List.of(flight));
        when(transportationRepository.findByOriginLocation(ankara)).thenReturn(List.of(back, bus));

        List<List<Transportation>> routes = routeService.findValidRoutes(request);

        assertEquals(1, routes.size());
        assertEquals(2, routes.get(0).size());
    }

    @Test
    void shouldRejectRoute_WhenOverThreeSegments() {
        Location b = createLocation(4L, "B", "B");
        Location c = createLocation(5L, "C", "C");
        Location d = createLocation(6L, "D", "D");
        Location e = createLocation(7L, "E", "E");

        Transportation t1 = createTransport(istanbul, b, Transportation.TransportationType.BUS, allDays());
        Transportation t2 = createTransport(b, c, Transportation.TransportationType.BUS, allDays());
        Transportation t3 = createTransport(c, d, Transportation.TransportationType.FLIGHT, allDays());
        Transportation t4 = createTransport(d, e, Transportation.TransportationType.BUS, allDays());

        RouteSearchRequest request = new RouteSearchRequest("IST", "E", fixedDate);

        when(locationRepository.findByLocationCode("IST")).thenReturn(istanbul);
        when(locationRepository.findByLocationCode("E")).thenReturn(e);
        when(transportationRepository.findByOriginLocation(istanbul)).thenReturn(List.of(t1));
        when(transportationRepository.findByOriginLocation(b)).thenReturn(List.of(t2));
        when(transportationRepository.findByOriginLocation(c)).thenReturn(List.of(t3));
        when(transportationRepository.findByOriginLocation(d)).thenReturn(List.of(t4));

        List<List<Transportation>> routes = routeService.findValidRoutes(request);

        assertTrue(routes.isEmpty());
    }

    @Test
    void shouldRejectRoute_WhenMoreThanOneGroundBeforeFlight() {
        Location b = createLocation(4L, "B", "B");

        Transportation bus1 = createTransport(istanbul, b, Transportation.TransportationType.BUS, allDays());
        Transportation bus2 = createTransport(b, ankara, Transportation.TransportationType.BUS, allDays());
        Transportation flight = createTransport(ankara, izmir, Transportation.TransportationType.FLIGHT, allDays());

        RouteSearchRequest request = new RouteSearchRequest("IST", "IZM", fixedDate);

        when(locationRepository.findByLocationCode("IST")).thenReturn(istanbul);
        when(locationRepository.findByLocationCode("IZM")).thenReturn(izmir);
        when(transportationRepository.findByOriginLocation(istanbul)).thenReturn(List.of(bus1));
        when(transportationRepository.findByOriginLocation(b)).thenReturn(List.of(bus2));
        when(transportationRepository.findByOriginLocation(ankara)).thenReturn(List.of(flight));

        List<List<Transportation>> routes = routeService.findValidRoutes(request);

        assertTrue(routes.isEmpty());
    }

    @Test
    void shouldRejectRoute_WhenNoFlightPresent() {
        Transportation subway = createTransport(istanbul, ankara, Transportation.TransportationType.SUBWAY, allDays());
        Transportation bus = createTransport(ankara, izmir, Transportation.TransportationType.BUS, allDays());

        RouteSearchRequest request = new RouteSearchRequest("IST", "IZM", fixedDate);

        when(locationRepository.findByLocationCode("IST")).thenReturn(istanbul);
        when(locationRepository.findByLocationCode("IZM")).thenReturn(izmir);
        when(transportationRepository.findByOriginLocation(istanbul)).thenReturn(List.of(subway));
        when(transportationRepository.findByOriginLocation(ankara)).thenReturn(List.of(bus));

        List<List<Transportation>> routes = routeService.findValidRoutes(request);

        assertTrue(routes.isEmpty());
    }

    private List<Integer> allDays() {
        return Arrays.asList(1, 2, 3, 4, 5, 6, 7);
    }

    private List<Integer> weekdaysOnly() {
        return Arrays.asList(1, 2, 3, 4, 5);
    }
}
