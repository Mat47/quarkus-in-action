package org.acme.reservation.reservation;

import java.time.LocalDate;

public record Reservation(
        Long id,
        Long carId,
        LocalDate startDay,
        LocalDate endDay
) {

    public Reservation(long carId, LocalDate start, LocalDate end) {
        this(null, carId, start, end);
    }

    public boolean isReserved(LocalDate startDay, LocalDate endDay) {
        return !(this.endDay.isBefore(startDay) || this.startDay.isAfter(endDay));
    }

}
