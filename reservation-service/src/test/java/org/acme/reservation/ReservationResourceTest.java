package org.acme.reservation;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.DisabledOnIntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.reservation.Reservation;
import org.acme.reservation.rest.ReservationResource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;

@QuarkusTest
class ReservationResourceTest {

    @TestHTTPEndpoint(ReservationResource.class) // injects available REST URLs
    @TestHTTPResource // also works in native mode
    URL reservationResource;

    @TestHTTPEndpoint(ReservationResource.class)
    @TestHTTPResource("availability")
    URL availability;

    @InjectMock
    GraphQLInventoryClient mockInventoryClient;

    @Test
    void testReservationIds() {
        var reservation = new Reservation(
                null,
                12345L,
                LocalDate.parse("2025-03-20"),
                LocalDate.parse("2025-03-29")
        );

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(reservation)
                .when()
                .post(reservationResource)
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisabledOnIntegrationTest(forArtifactTypes = DisabledOnIntegrationTest.ArtifactType.NATIVE_BINARY)
    void making_a_reservation_and_check_availability() {
        var peugeot = new Car(1L, "ABC123", "Peugeot", "406");

        Mockito.when(mockInventoryClient.allCars())
                .thenReturn(List.of(peugeot));

        var startDate = "2022-01-01";
        var endDate = "2022-01-10";

        var availableCars = RestAssured
                .given()
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .when()
                .get(availability)
                .then()
                .statusCode(200)
                .extract().as(Car[].class);

        var car = availableCars[0];
        var reservation = new Reservation(car.id, LocalDate.parse(startDate), LocalDate.parse(endDate));

        // reserve car
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(reservation)
                .when()
                .post(reservationResource)
                .then()
                .statusCode(200)
                .body("carId", is(car.id.intValue()));

        // verify car is unavailable
        RestAssured
                .given()
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .when()
                .get(availability)
                .then()
                .statusCode(200)
                .body("findAll { car -> car.id == " + car.id + "}", hasSize(0));
    }

}