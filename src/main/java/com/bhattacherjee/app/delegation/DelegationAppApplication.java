package com.bhattacherjee.app.delegation;

import com.acciente.oacc.AccessControlContext;
import com.acciente.oacc.PasswordCredentials;
import com.acciente.oacc.ResourcePermissions;
import com.acciente.oacc.Resources;
import com.bhattacherjee.app.delegation.core.SecurityModel;
import io.dropwizard.Application;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.migrations.CloseableLiquibase;
import io.dropwizard.migrations.CloseableLiquibaseWithClassPathMigrationsFile;
import io.dropwizard.migrations.DbCommand;
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
        bootstrap.addBundle(new InitializingMigrationBundle<DelegationAppConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(DelegationAppConfiguration delegationAppConfiguration) {
                return delegationAppConfiguration.getOaccdb();
            }

            @Override
            protected void setupApplicationDomainData(DelegationAppConfiguration configuration, Environment environment) throws Exception {
                final AccessControlContextFactory accessControlContextFactory = configuration.getOaccFactory();
                accessControlContextFactory.iniitialize(environment, configuration.getOaccdb(), "oacc");
                AccessControlContext oaccContext = accessControlContextFactory.build();

                // authenticate the context as root (oaccuser in this case)
                oaccContext.authenticate(Resources.getInstance(0),
                        PasswordCredentials.newInstance(configuration.getOaccdb().getPassword().toCharArray()));

                // register Application Domain resources and roles
                oaccContext.createDomain(SecurityModel.DOMAIN_DELEGATION);

                oaccContext.createResourceClass(SecurityModel.RESOURCECLASS_USER, true, true);
                oaccContext.createResourceClass(SecurityModel.RESOURCECLASS_ROLEHELPER, false, false);
                oaccContext.createResourceClass(SecurityModel.RESOURCECLASS_ROLE, false, false);
                oaccContext.createResourceClass(SecurityModel.RESOURCECLASS_PROPERTY, false, false);
                oaccContext.createResourceClass(SecurityModel.RESOURCECLASS_PROPERTYRATE, false, false);
                oaccContext.createResourceClass(SecurityModel.RESOURCECLASS_PROPERTYAVAILABILITY, false, false);

                oaccContext.createResourcePermission(SecurityModel.RESOURCECLASS_ROLE,
                        SecurityModel.PERM_INHERITWITHGRANT.getPermissionName());

                oaccContext.createResourcePermission(SecurityModel.RESOURCECLASS_PROPERTY,
                        SecurityModel.PERM_VIEW.getPermissionName());
                oaccContext.createResourcePermission(SecurityModel.RESOURCECLASS_PROPERTY,
                        SecurityModel.PERM_EDIT.getPermissionName());
                oaccContext.createResourcePermission(SecurityModel.RESOURCECLASS_PROPERTY,
                        SecurityModel.PERM_DELETE.getPermissionName());

                oaccContext.createResourcePermission(SecurityModel.RESOURCECLASS_PROPERTYRATE,
                        SecurityModel.PERM_VIEW.getPermissionName());
                oaccContext.createResourcePermission(SecurityModel.RESOURCECLASS_PROPERTYRATE,
                        SecurityModel.PERM_EDIT.getPermissionName());
                oaccContext.createResourcePermission(SecurityModel.RESOURCECLASS_PROPERTYRATE,
                        SecurityModel.PERM_DELETE.getPermissionName());

                oaccContext.createResourcePermission(SecurityModel.RESOURCECLASS_PROPERTYAVAILABILITY,
                        SecurityModel.PERM_VIEW.getPermissionName());
                oaccContext.createResourcePermission(SecurityModel.RESOURCECLASS_PROPERTYAVAILABILITY,
                        SecurityModel.PERM_EDIT.getPermissionName());
                oaccContext.createResourcePermission(SecurityModel.RESOURCECLASS_PROPERTYAVAILABILITY,
                        SecurityModel.PERM_DELETE.getPermissionName());


                // un-authenticate as root
                oaccContext.unauthenticate();

            }

            @Override
            public String getName() {
                return "oaccdb";
            }

            @Override
            public String getMigrationsFileName() {
                return "migrations_oaccdb.xml";
            }
        });
    }

    @Override
    public void run(final DelegationAppConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }


    private abstract class InitializingMigrationBundle<T extends DelegationAppConfiguration>
            implements ConfiguredBundle<T>, DatabaseConfiguration<T> {
        private static final String DEFAULT_NAME = "db";
        private static final String DEFAULT_MIGRATION_FILE = "migrations.xml";

        @Override
        public void initialize(Bootstrap<?> bootstrap) {
            final Class<T> clazz = (Class<T>) bootstrap.getApplication().getConfigurationClass();
            bootstrap.addCommand(new DbCommand<>(getName(), this, clazz, getMigrationsFileName()));
        }

        @Override
        public void run(T configuration, Environment environment) throws Exception {
            CloseableLiquibase liquibase = new CloseableLiquibaseWithClassPathMigrationsFile(getDataSourceFactory(configuration)
                    .build(environment.metrics(), getName()),
                    getMigrationsFileName());
            // add application domain specific data
            setupApplicationDomainData(configuration, environment);



        }

        protected abstract void setupApplicationDomainData(T configuration, Environment environment) throws Exception;

        public String getName() {
            return DEFAULT_NAME;
        }

        public String getMigrationsFileName() {
            return DEFAULT_MIGRATION_FILE;
        }
    }

}
