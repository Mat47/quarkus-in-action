package org.acme.reservation.rest;

import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.inventory.InventoryClient;
import org.acme.reservation.rental.RentalClient;
import org.acme.reservation.reservation.Reservation;
import org.acme.reservation.reservation.ReservationsRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @Inject
    SecurityContext secContext;

    private final ReservationsRepository reservationsRepository;
    private final InventoryClient inventoryClient;
    private final RentalClient rentalClient;

    public ReservationResource(
            ReservationsRepository reservations,
            @GraphQLClient("inventory") GraphQLInventoryClient inventoryClient,
            @RestClient RentalClient rentalClient
    ) {
        this.reservationsRepository = reservations;
        this.inventoryClient = inventoryClient;
        this.rentalClient = rentalClient;
    }

    @GET
    @Path("availability")
    public Collection<Car> availability(
            @RestQuery LocalDate startDate,
            @RestQuery LocalDate endDate
    ) {
        var availableCars = inventoryClient.allCars();

        Map<Long, Car> carsById = new HashMap<>();
        for (Car car : availableCars) {
            carsById.put(car.id, car);
        }

        var reservations = reservationsRepository.findAll();
        reservations.forEach(res -> {
            if (res.isReserved(startDate, endDate)) {
                carsById.remove(res.carId());
            }
        });

        return carsById.values();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Reservation make(Reservation reservation) {
        var userRes = new Reservation(
                reservation.carId(),
                reservation.startDay(),
                reservation.endDay(),
                secContext.getUserPrincipal() != null
                        ? secContext.getUserPrincipal().getName()
                        : "anonymous"
        );
        var res = reservationsRepository.save(userRes);

        if (reservation.startDay().equals(LocalDate.now())) {
            var rental = rentalClient.start(res.userId(), res.id()); // starting rental => calling client interface method making the remote HTTP call
            Log.info("Successfully started rental: " + rental);
        }

        return res;
    }

    @GET
    @Path("all")
    public Collection<Reservation> allReservations() {
        String userId = secContext.getUserPrincipal() != null
                ? secContext.getUserPrincipal().getName()
                : null;

        return reservationsRepository.findAll()
                .stream()
                .filter(r -> userId == null || userId.equals(r.userId()))
                .toList();
    }

}
