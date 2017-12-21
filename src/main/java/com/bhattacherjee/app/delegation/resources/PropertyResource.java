package com.bhattacherjee.app.delegation.resources;

import com.bhattacherjee.app.delegation.api.Property;
import com.bhattacherjee.app.delegation.auth.OaccPrincipal;
import com.bhattacherjee.app.delegation.core.PropertyService;
import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.params.LongParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/properties")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PropertyResource {
    private final PropertyService propertyService;

    public PropertyResource(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @POST
    public Property createProperty(@Auth OaccPrincipal oaccPrincipal, Property property) {
        return propertyService.createProperty(oaccPrincipal.getAccessControlContext(), property);
    }

    @GET
    public List<Property> findPropertiesForAuthenticatedUser(@Auth OaccPrincipal oaccPrincipal) {
        return propertyService.findPropertiesForAuthenticatedUser(oaccPrincipal.getAccessControlContext());
    }

    @GET
    @Path("/{id}")
    public Property getPropertyById(@Auth OaccPrincipal oaccPrincipal, @PathParam("id") LongParam id) {
        return propertyService.getPropertyById(oaccPrincipal.getAccessControlContext(), id.get());
    }

    @PUT
    @Path("/{id}")
    public void shareProperty(@Auth OaccPrincipal oaccPrincipal,
                              @PathParam("id") LongParam id,
                              @QueryParam("share_with") String email) {
        propertyService.shareProperty(oaccPrincipal.getAccessControlContext(), id.get(), email);
    }
}
