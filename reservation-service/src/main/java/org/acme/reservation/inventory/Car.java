package org.acme.reservation.inventory;

public record Car(
        Long id,
        String licensePlateNumber,
        String manufacturer,
        String model
) {
}
