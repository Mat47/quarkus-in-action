package org.acme.reservation;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.reservation.reservation.Reservation;
import org.acme.reservation.reservation.ReservationsRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest // enables CDI
public class ReservationRepositoryTest {

    @Inject // resolves to `InMemoryReservationsRepository` - only available CDI bean
    ReservationsRepository repository;

    @Test
    public void testCreateReservation() {
        var reservation = repository.save(new Reservation(
                null,
                348L,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(12)
        ));

        assertAll(
                () -> assertNotNull(reservation.id()),
                () -> assertTrue(repository.findAll().contains(reservation))
        );
    }

}
