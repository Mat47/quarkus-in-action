package org.acme.reservation.reservation;

import java.time.LocalDate;

public record Reservation(
        Long id,
        Long carId,
        LocalDate startDay,
        LocalDate endDay,
        String userId
) {

    public Reservation(long carId, LocalDate start, LocalDate end) {
        this(null, carId, start, end, null);
    }

    public Reservation(long carId, LocalDate start, LocalDate end, String userId) {
        this(null, carId, start, end, userId);
    }

    public boolean isReserved(LocalDate startDay, LocalDate endDay) {
        return !(this.endDay.isBefore(startDay) || this.startDay.isAfter(endDay));
    }

}
