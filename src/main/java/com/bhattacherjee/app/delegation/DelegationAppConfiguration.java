package com.bhattacherjee.app.delegation;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.*;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Getter @Setter
public class DelegationAppConfiguration extends Configuration {

    @Valid @NotNull @JsonProperty("oaccDb")
    private DataSourceFactory oaccdb = new DataSourceFactory();

    @Valid @NotNull @JsonProperty("oacc")
    private AccessControlContextFactory oaccFactory = new AccessControlContextFactory();

}
