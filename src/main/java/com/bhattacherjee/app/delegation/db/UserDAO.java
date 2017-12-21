package com.bhattacherjee.app.delegation.db;

import com.bhattacherjee.app.delegation.api.User;
import com.bhattacherjee.app.delegation.db.mappers.UserMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(UserMapper.class)
public interface UserDAO {
    @SqlUpdate("INSERT INTO properties.user(email) VALUES (:email)")
    int insert(@BindBean User user);

    @SqlQuery("SELECT * FROM properties.user WHERE email=:email")
    User findByEmail(@Bind("email") String email);

    @SqlQuery("SELECT * FROM properties.user")
    List<User> findAll();
}
