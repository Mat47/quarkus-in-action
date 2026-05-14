package org.acme.users.model;

public record Car(
        Long id,
        String licensePlateNumber,
        String manufacturer,
        String model
) {
}