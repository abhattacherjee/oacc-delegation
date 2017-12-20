package com.bhattacherjee.app.delegation.db.mappers;

import com.bhattacherjee.app.delegation.api.Property;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PropertyMapper implements ResultSetMapper<Property> {

    @Override
    public Property map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return new Property(resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getInt("postalCd"));
    }
}
