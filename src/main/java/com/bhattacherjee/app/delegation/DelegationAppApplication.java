package com.bhattacherjee.app.delegation;

import com.acciente.oacc.*;
import com.acciente.oacc.encryptor.PasswordEncryptor;
import com.acciente.oacc.encryptor.bcrypt.BCryptPasswordEncryptor;
import com.acciente.oacc.sql.SQLAccessControlSystemInitializer;
import com.bhattacherjee.app.delegation.auth.OaccBasicAuthenticator;
import com.bhattacherjee.app.delegation.auth.OaccPrincipal;
import com.bhattacherjee.app.delegation.core.PropertyService;
import com.bhattacherjee.app.delegation.core.SecurityModel;
import com.bhattacherjee.app.delegation.core.UserService;
import com.bhattacherjee.app.delegation.db.PropertyDAO;
import com.bhattacherjee.app.delegation.db.UserDAO;
import com.bhattacherjee.app.delegation.resources.PropertyResource;
import com.bhattacherjee.app.delegation.resources.UserResource;
import com.bhattacherjee.app.delegation.resources.exceptions.AuthorizationExceptionMapper;
import com.bhattacherjee.app.delegation.resources.exceptions.IllegalArgumentExceptionMapper;
import com.bhattacherjee.app.delegation.resources.exceptions.InvalidCredentialsExceptionMapper;
import com.bhattacherjee.app.delegation.resources.exceptions.NotAuthenticatedExceptionMapper;
import io.dropwizard.Application;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.db.DatabaseConfiguration;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.CloseableLiquibase;
import io.dropwizard.migrations.CloseableLiquibaseWithClassPathMigrationsFile;
import io.dropwizard.migrations.DbCommand;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

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

                SQLAccessControlSystemInitializer.initializeOACC(configuration.getOaccdb().getUrl(),
                                                                    configuration.getOaccdb().getUser(),
                                                                    configuration.getOaccdb().getPassword(),
                                                                    "OACC",
                                                                    "changeit".toCharArray(),
                                                                    BCryptPasswordEncryptor.newInstance(12));



                accessControlContextFactory.initialize(environment, configuration.getOaccdb(), "oacc-setup");
                AccessControlContext oaccContext = accessControlContextFactory.build();

                // authenticate the context as root
                oaccContext.authenticate(Resources.getInstance(0),
                        PasswordCredentials.newInstance("changeit".toCharArray()));

                // register Application Domain resources and roles
                oaccContext.createDomain(SecurityModel.DOMAIN_DELEGATION);

                oaccContext.createResourceClass(SecurityModel.RESOURCECLASS_USER, true, true);
                oaccContext.createResourceClass(SecurityModel.RESOURCECLASS_ROLEHELPER, true, false);
                oaccContext.createResourceClass(SecurityModel.RESOURCECLASS_ROLE, true, false);
                oaccContext.createResourceClass(SecurityModel.RESOURCECLASS_PROPERTY, false, false);
                oaccContext.createResourceClass(SecurityModel.RESOURCECLASS_PROPERTYRATE, false, false);
                oaccContext.createResourceClass(SecurityModel.RESOURCECLASS_PROPERTYAVAILABILITY, false, false);

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

                final Resource ownerRole = oaccContext.createResource(SecurityModel.RESOURCECLASS_ROLE,
                                                                        SecurityModel.DOMAIN_DELEGATION,
                                                                        SecurityModel.RESOURCENAME_ROLE_OWNER,
                                                                        SecurityModel.RESOURCEPASSWORD_ROLE_OWNER);

                final Resource ownerRoleHelper = oaccContext.createResource(SecurityModel.RESOURCECLASS_ROLEHELPER,
                                                                        SecurityModel.DOMAIN_DELEGATION,
                                                                        SecurityModel.RESOURCENAME_ROLEHELPER_OWNER,
                                                                        SecurityModel.RESOURCEPASSWORD_ROLEHELPER_OWNER);

                //owner-role-helper should be able to grant inheritance for ROLE class
                Set<ResourcePermission> permissionsOwnerRoleHelper = new HashSet<>();
                permissionsOwnerRoleHelper.add(SecurityModel.PERM_INHERITWITHGRANT);

                oaccContext.setGlobalResourcePermissions(ownerRoleHelper,
                                                            SecurityModel.RESOURCECLASS_ROLE,
                                                            SecurityModel.DOMAIN_DELEGATION,
                                                            permissionsOwnerRoleHelper);

                //owner-role should be able to create/view/edit/delete properties
                Set<ResourceCreatePermission> permissionCreateOwnerRole = new HashSet<>();
                permissionCreateOwnerRole.add(SecurityModel.PERM_CREATE);
                permissionCreateOwnerRole.add(SecurityModel.PERM_POSTCREATE_VIEWWITHGRANT);
                permissionCreateOwnerRole.add(SecurityModel.PERM_POSTCREATE_DELETE);
                permissionCreateOwnerRole.add(SecurityModel.PERM_POSTCREATE_EDIT);
                permissionCreateOwnerRole.add(ResourceCreatePermissions
                        .getInstance(ResourcePermissions.getInstance(ResourcePermissions.QUERY)));

                oaccContext.setResourceCreatePermissions(Resources.getInstance(SecurityModel.RESOURCENAME_ROLE_OWNER),
                        SecurityModel.RESOURCECLASS_PROPERTY,
                        SecurityModel.DOMAIN_DELEGATION,
                        permissionCreateOwnerRole);

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
        bootstrap.addBundle(new InitializingMigrationBundle<DelegationAppConfiguration>() {
            @Override
            protected void setupApplicationDomainData(DelegationAppConfiguration configuration, Environment environment) throws Exception {
                // no setup required
            }

            @Override
            public PooledDataSourceFactory getDataSourceFactory(DelegationAppConfiguration delegationAppConfiguration) {
                return delegationAppConfiguration.getPropertiesdb();
            }

            @Override
            public String getName() {
                return "propertiesdb";
            }

            @Override
            public String getMigrationsFileName() {
                return "migrations_propertiesdb.xml";
            }
        });

    }

    @Override
    public void run(final DelegationAppConfiguration configuration,
                    final Environment environment) {
        final DBIFactory dbiFactory = new DBIFactory();
        final DBI propertiesDbJdbi = dbiFactory.build(environment, configuration.getPropertiesdb(), "propertiesDb");
        final UserDAO userDAO = propertiesDbJdbi.onDemand(UserDAO.class);
        final PropertyDAO propertyDAO = propertiesDbJdbi.onDemand(PropertyDAO.class);

        final AccessControlContextFactory accessControlContextFactory = configuration.getOaccFactory();
        accessControlContextFactory.initialize(environment, configuration.getOaccdb(), "oacc");

        environment.jersey().register(new UserResource(new UserService(userDAO, accessControlContextFactory)));
        environment.jersey().register(new PropertyResource(new PropertyService(propertyDAO)));

        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<OaccPrincipal>()
                        .setAuthenticator(new OaccBasicAuthenticator(accessControlContextFactory))
                        .setRealm("OACC Basic Authentication")
                        .buildAuthFilter()));

        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(OaccPrincipal.class));

        environment.jersey().register(new IllegalArgumentExceptionMapper(environment.metrics()));
        environment.jersey().register(new AuthorizationExceptionMapper(environment.metrics()));
        environment.jersey().register(new InvalidCredentialsExceptionMapper(environment.metrics()));
        environment.jersey().register(new NotAuthenticatedExceptionMapper(environment.metrics()));

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
            liquibase.update("");

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
