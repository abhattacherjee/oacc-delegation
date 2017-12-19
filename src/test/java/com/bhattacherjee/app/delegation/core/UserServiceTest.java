package com.bhattacherjee.app.delegation.core;

import com.acciente.oacc.*;
import com.bhattacherjee.app.delegation.AccessControlContextFactory;
import com.bhattacherjee.app.delegation.api.User;
import com.bhattacherjee.app.delegation.db.UserDAO;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class UserServiceTest {
    private static final String EMAIL = "abhishek@gmail.com";
    private static final char[] PASSWORD_AS_CHARS = "changeIt".toCharArray();

    private UserService service;
    private UserDAO userDAO;
    private AccessControlContextFactory oaccContextFactory;
    private AccessControlContext oaccContext;

    @Before
    public void setup() throws Exception {
        userDAO = Mockito.mock(UserDAO.class);
        oaccContextFactory = Mockito.mock(AccessControlContextFactory.class);
        oaccContext = Mockito.mock(AccessControlContext.class);

        Mockito.when(oaccContextFactory.build()).thenReturn(oaccContext);
        service = new UserService(userDAO, oaccContextFactory);
    }

    @After
    public void teardown() throws Exception {

    }

    @Test(expected = NullPointerException.class)
    public void createUserWithNull() throws Exception {
        service.createUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserWithBlankEmail() throws Exception {
        service.createUser(new User(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserWithBlankPassword() throws Exception {
        service.createUser(new User(EMAIL));
    }

    @Test
    public void createUser() throws Exception {
        Resource createdResource = Resources.getInstance(1L, EMAIL);

        Mockito.when(oaccContext.createResource(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(Credentials.class))).thenReturn(createdResource);

        final User submittedUser = new User(EMAIL, PASSWORD_AS_CHARS);

        final User returnedUser = service.createUser(submittedUser);

        Assertions.assertThat(returnedUser).isEqualTo(submittedUser);
        Assertions.assertThat(returnedUser.getPassword()).isNull();

        Mockito.verify(oaccContext).createResource(SecurityModel.RESOURCECLASS_USER, SecurityModel.DOMAIN_DELEGATION,
                submittedUser.getEmail(), PasswordCredentials.newInstance(PASSWORD_AS_CHARS));

        Mockito.verify(oaccContextFactory, Mockito.times(2)).build();
        Mockito.verify(oaccContext).grantResourcePermissions(createdResource,
                                                                SecurityModel.RESOURCE_ROLE_OWNER,
                                                                SecurityModel.PERM_INHERIT);
        Mockito.verify(userDAO).insert(submittedUser);
        Mockito.verify(oaccContext, Mockito.never()).deleteResource(Mockito.any(Resource.class));

    }
}
