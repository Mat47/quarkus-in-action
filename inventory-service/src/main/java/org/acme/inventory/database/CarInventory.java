package org.acme.inventory.database;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.inventory.model.Car;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class CarInventory {

    private List<Car> cars;

    public static final AtomicLong ids = new AtomicLong(0);

    @PostConstruct /* called when framework instantiates beans - stub db */
    void initialize() {
        cars = new CopyOnWriteArrayList<>();
        initialData();
    }

    private void initialData() {
        var mazda = new Car(ids.incrementAndGet(), "ABC-123", "Mazda", "6");
        cars.add(mazda);

        var ford = new Car(ids.incrementAndGet(), "XYZ-789", "Ford", "Mustang");
        cars.add(ford);
    }

    public List<Car> getCars() {
        return cars;
    }

}
