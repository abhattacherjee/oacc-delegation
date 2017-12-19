package com.bhattacherjee.app.delegation.health;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.db.TimeBoundHealthCheck;
import io.dropwizard.util.Duration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;

public class DataSourceHealthCheck extends HealthCheck {
    private final DataSource dataSource;
    private final String validationQuery;
    private final TimeBoundHealthCheck timeBoundHealthCheck;

    public DataSourceHealthCheck(ExecutorService executorService, Duration duration, DataSource dataSource, String validationQuery) {
        this.dataSource = dataSource;
        this.validationQuery = validationQuery;
        timeBoundHealthCheck = new TimeBoundHealthCheck(executorService, duration);
    }

    @Override
    protected Result check() throws Exception {
        return timeBoundHealthCheck.check(() -> {
            try(
                Connection connection = dataSource.getConnection();
                final PreparedStatement statement = connection.prepareStatement(validationQuery);
                final ResultSet ignored = statement.executeQuery();) {
                return Result.healthy();

            }
        });
    }
}
