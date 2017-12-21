package com.bhattacherjee.app.delegation.resources.exceptions;


import com.acciente.oacc.AuthorizationException;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import io.dropwizard.jersey.errors.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class AuthorizationExceptionMapper implements ExceptionMapper<AuthorizationException> {
    private final Meter exceptions;

    public AuthorizationExceptionMapper(MetricRegistry metrics) {
        exceptions = metrics.meter(getClass().getCanonicalName() + " exceptions");
    }

    @Override
    public Response toResponse(AuthorizationException e) {
        exceptions.mark();
        return Response
                .status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorMessage(Response.Status.FORBIDDEN.getStatusCode(), null))
                .build();

    }
}
