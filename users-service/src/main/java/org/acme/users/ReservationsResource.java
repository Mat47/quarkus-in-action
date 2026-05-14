package org.acme.users;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;

@Path("/")
public class ReservationsResource {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance index(
                LocalDate startDate,
                LocalDate endDate,
                String name
        );
    }

    @Inject
    SecurityContext securityContext;

    @RestClient
    ReservationsClient client;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index(
            @RestQuery LocalDate startDate,
            @RestQuery LocalDate endDate
    ) {
        if (startDate == null) startDate = LocalDate.now().plusDays(1);
        if (endDate == null) endDate = LocalDate.now().plusDays(7);

        return Templates.index(
                startDate,
                endDate,
                securityContext.getUserPrincipal().getName()
        );
    }

}
