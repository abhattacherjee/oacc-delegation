package com.bhattacherjee.app.delegation.resources.exceptions;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import io.dropwizard.jersey.errors.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    private final Meter exceptions;

    public IllegalArgumentExceptionMapper(MetricRegistry metrics) {
        exceptions = metrics.meter(getClass().getCanonicalName() + " exceptions");
    }

    @Override
    public Response toResponse(IllegalArgumentException e) {
        exceptions.mark();

        if (e.getMessage().matches("Resource .* not found!")) {
            return Response
                    .status(Response.Status.NOT_FOUND) // 404
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new ErrorMessage(Response.Status.UNAUTHORIZED.getStatusCode(), e.getMessage()))
                    .build();
        }

        return Response
                .status(422) // UNPROCESSABLE_ENTITY
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorMessage(422, e.getMessage()))
                .build();
    }
}
