package org.acme.inventory;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.acme.inventory.model.InsertCarRequest;
import org.acme.inventory.model.InventoryService;
import org.acme.inventory.model.RemoveCarRequest;

@QuarkusMain
public class InventoryCommand implements QuarkusApplication {

    private static final String USAGE = "Usage: inventory <add>|<remove> <license plate number> <manufacturer> <model>";

    @GrpcClient("inventory") // specifies which client to inject acc. to (.properties) config
    InventoryService inventory; // selects the generated service stub to use

    @Override
    public int run(String... args) {
        var action = args.length > 0 ? args[0] : null;

        return switch (action) {
            case "add" -> {
                if (args.length < 4) yield 1;

                add(args[1], args[2], args[3]);
                yield 0;
            }
            case "remove" -> {
                if (args.length < 2) yield 1;

                remove(args[1]);
                yield 0;
            }
            case null, default -> {
                System.err.println(USAGE);
                yield 1;
            }
        };
    }

    public void add(
            String licensePlateNumber,
            String manufacturer,
            String model
    ) {
        inventory.add(InsertCarRequest.newBuilder()
                        .setLicensePlateNumber(licensePlateNumber)
                        .setManufacturer(manufacturer)
                        .setModel(model)
                        .build()
                )
                .onItem()
                .invoke(carRes -> System.out.printf("New car inserted: %n%s%n", carRes))
                .await()
                .indefinitely();
    }

    public void remove(String licensePlateNumber) {
        inventory.remove(RemoveCarRequest.newBuilder()
                        .setLicensePlateNumber(licensePlateNumber)
                        .build()
                )
                .onItem()
                .invoke(carRes -> System.out.printf("Removed car: %n%s%n", carRes))
                .await()
                .indefinitely();
    }

}
