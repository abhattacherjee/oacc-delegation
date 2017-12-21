package com.bhattacherjee.app.delegation.db;

import com.bhattacherjee.app.delegation.api.Property;
import com.bhattacherjee.app.delegation.db.mappers.PropertyMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.Collection;
import java.util.List;

@UseStringTemplate3StatementLocator
@RegisterMapper(PropertyMapper.class)
public interface PropertyDAO {
    @SqlUpdate("INSERT INTO properties.property (title, postalCd) VALUES (:title, :postalCd)")
    @GetGeneratedKeys
    long insert(@BindBean Property property);

    @SqlUpdate("UPDATE properties.property SET title = :title, postalCd = :postalCd WHERE id = :id")
    int update(@BindBean Property property);

    @SqlUpdate("DELETE FROM properties.property WHERE id = :id")
    int delete(@Bind("id") long id);

    @SqlQuery("SELECT * FROM properties.property WHERE id = :id")
    Property findById(@Bind("id") long id);

    @SqlQuery("SELECT * FROM properties.property WHERE id in (<ids>)")
    List<Property> findByIds(@BindIn("ids") Collection<Long> ids);
}
