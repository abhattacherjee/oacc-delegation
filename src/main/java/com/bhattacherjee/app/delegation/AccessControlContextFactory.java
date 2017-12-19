package com.bhattacherjee.app.delegation;

import com.acciente.oacc.AccessControlContext;
import com.acciente.oacc.encryptor.bcrypt.BCryptPasswordEncryptor;
import com.acciente.oacc.sql.SQLAccessControlContextFactory;
import com.acciente.oacc.sql.SQLProfile;
import com.bhattacherjee.app.delegation.health.DataSourceHealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter @Setter
public class AccessControlContextFactory {

    @JsonProperty
    private String schemaName;

    @NotEmpty @JsonProperty
    private String sqlProfile;

    private ManagedDataSource dataSource;
    private BCryptPasswordEncryptor bCryptPasswordEncryptor;

    public void initialize(Environment environment, PooledDataSourceFactory dataSourceFactory, String name) {
        dataSource = dataSourceFactory.build(environment.metrics(), name);
        bCryptPasswordEncryptor = BCryptPasswordEncryptor.newInstance(12);
        environment.lifecycle().manage(dataSource);
        environment.healthChecks().register(name, new DataSourceHealthCheck(environment.getHealthCheckExecutorService(),
                dataSourceFactory.getValidationQueryTimeout().orElse(Duration.seconds(5)),
                dataSource,
                dataSourceFactory.getValidationQuery()));
    }

    public AccessControlContext build() {
        return SQLAccessControlContextFactory.getAccessControlContext(dataSource,
                schemaName,
                SQLProfile.valueOf(getSqlProfile()),
                bCryptPasswordEncryptor);
    }


}
