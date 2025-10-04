package org.acme.reservation.rest;

import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
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
        var res = reservationsRepository.save(reservation);
        final String userId = "x"; // dummy value for now
        if (reservation.startDay().equals(LocalDate.now())) {
            var rental = rentalClient.start(userId, res.id()); // starting rental = calling client interface method making the remote HTTP call
            Log.info("Successfully started rental: " + rental);
        }
        return res;
    }

}
