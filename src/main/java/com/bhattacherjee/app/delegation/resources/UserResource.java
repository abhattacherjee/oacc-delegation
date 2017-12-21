package com.bhattacherjee.app.delegation.resources;

import com.bhattacherjee.app.delegation.api.User;
import com.bhattacherjee.app.delegation.core.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private final UserService service;

    public UserResource(UserService service) {
        this.service = service;
    }

    @POST
    public User createUser(User user) {
        return service.createUser(user);
    }
}
