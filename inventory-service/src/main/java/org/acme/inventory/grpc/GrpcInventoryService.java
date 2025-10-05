package org.acme.inventory.grpc;

import io.quarkus.grpc.GrpcService;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.acme.inventory.database.CarInventory;
import org.acme.inventory.model.*;

@GrpcService
public class GrpcInventoryService implements InventoryService {

    @Inject
    CarInventory inventory;

    @Override
    public Multi<CarResponse> add(Multi<InsertCarRequest> requests) {

        return requests
                .map(req -> new Car(
                        CarInventory.ids.incrementAndGet(),
                        req.getLicensePlateNumber(),
                        req.getManufacturer(),
                        req.getModel()
                ))
                .onItem().invoke(c -> {
                    Log.info("Persisting %s".formatted(c));
                    inventory.getCars().add(c);
                })
                .map(c -> CarResponse.newBuilder()
                        .setId(c.id())
                        .setLicensePlateNumber(c.licensePlateNumber())
                        .setManufacturer(c.manufacturer())
                        .setModel(c.model())
                        .build()
                );

        //        var car = new Car(
//                CarInventory.ids.incrementAndGet(),
//                request.getLicensePlateNumber(),
//                request.getManufacturer(),
//                request.getModel()
//        );
//
//        Log.info("Persisting %s".formatted(car));
//        inventory.getCars().add(car);
//
//        return Uni.createFrom().item(
//                CarResponse.newBuilder()
//                        .setLicensePlateNumber(car.licensePlateNumber())
//                        .setManufacturer(car.manufacturer())
//                        .setModel(car.model())
//                        .setId(car.id())
//                        .build()
//        );
    }

    @Override
    public Uni<CarResponse> remove(RemoveCarRequest request) {
        var optCar = inventory.getCars().stream()
                .filter(c -> request.getLicensePlateNumber().equals(c.licensePlateNumber()))
                .findFirst();

        if (optCar.isEmpty()) return Uni.createFrom().nullItem();

        var removedCar = optCar.get();
        Log.info("Removing %s".formatted(removedCar));
        inventory.getCars().remove(removedCar);

        return Uni.createFrom().item(
                CarResponse.newBuilder()
                        .setLicensePlateNumber(removedCar.licensePlateNumber())
                        .setManufacturer(removedCar.manufacturer())
                        .setModel(removedCar.model())
                        .setId(removedCar.id())
                        .build()
        );
    }
}
