package org.acme.reservation.inventory;

import io.smallrye.graphql.client.core.Document;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

import static io.smallrye.graphql.client.core.Document.document;
import static io.smallrye.graphql.client.core.Field.field;
import static io.smallrye.graphql.client.core.Operation.operation;

@GraphQLClientApi(configKey = "inventory")
public interface GraphQLInventoryClient extends InventoryClient {

    // typesafe client
    @Query("cars")
    List<Car> allCars();

    // dynamic client
    Document cars = document(
            operation(
                    "cars",
                    field("id"),
                    field("licensePlateNumber"),
                    field("manufacturer"),
                    field("model")
            )
    );

}
