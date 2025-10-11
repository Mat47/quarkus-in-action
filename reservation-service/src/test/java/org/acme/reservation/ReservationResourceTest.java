package org.acme.reservation;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.acme.reservation.reservation.Reservation;
import org.acme.reservation.rest.ReservationResource;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.time.LocalDate;

import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class ReservationResourceTest {

    @TestHTTPEndpoint(ReservationResource.class) // injects available REST URLs
    @TestHTTPResource // also works in native mode
    URL reservationResource;

    @Test
    public void testReservationIds() {
        var reservation = new Reservation(
                null,
                12345L,
                LocalDate.parse("2025-03-20"),
                LocalDate.parse("2025-03-29")
        );

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(reservation)
                .when()
                .post(reservationResource)
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

}