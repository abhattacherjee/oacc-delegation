package com.bhattacherjee.app.delegation;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DelegationAppApplication extends Application<DelegationAppConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DelegationAppApplication().run(args);
    }

    @Override
    public String getName() {
        return "DelegationApp";
    }

    @Override
    public void initialize(final Bootstrap<DelegationAppConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final DelegationAppConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
