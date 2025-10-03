package org.acme.inventory.service;

import jakarta.inject.Inject;
import org.acme.inventory.database.CarInventory;
import org.acme.inventory.model.Car;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;
import java.util.Optional;

@GraphQLApi
public class GraphQLInventoryService {

    @Inject
    CarInventory inventory;

    @Query
    public List<Car> cars() {
        return inventory.getCars();
    }

    @Mutation
    public Car register(Car car) {
        return new Car(
                CarInventory.ids.incrementAndGet(),
                car.licensePlateNumber(),
                car.manufacturer(),
                car.model()
        );
    }

    @Mutation
    public boolean remove(String licensePlateNumber) {
        List<Car> cars = inventory.getCars();
        Optional<Car> toBeRemoved = cars.stream()
                .filter(c -> c.licensePlateNumber().equals(licensePlateNumber))
                .findAny();

        return toBeRemoved.map(cars::remove).orElse(false);
    }

}
