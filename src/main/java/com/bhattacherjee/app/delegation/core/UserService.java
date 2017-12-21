package com.bhattacherjee.app.delegation.core;

import com.acciente.oacc.AccessControlContext;
import com.acciente.oacc.PasswordCredentials;
import com.acciente.oacc.Resource;
import com.bhattacherjee.app.delegation.AccessControlContextFactory;
import com.bhattacherjee.app.delegation.api.User;
import com.bhattacherjee.app.delegation.db.UserDAO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;

import java.util.Objects;

@Getter @Setter
public class UserService {
    public static final EmailValidator EMAIL_VALIDATOR = new EmailValidator();

    private final UserDAO userDAO;
    private final AccessControlContextFactory oaccFactory;

    public UserService(UserDAO userDAO, AccessControlContextFactory oaccFactory) {
        this.userDAO = userDAO;
        this.oaccFactory = oaccFactory;
    }


    public User createUser(User user) {
        assertUserIsValid(user);

        final User newUser = new User(user.getEmail().toLowerCase(), user.getPassword());

        //validate with OACC context
        final AccessControlContext oaccContext = oaccFactory.build();
        final Resource userResource = createUserResource(user, oaccContext);

        try {
            assignUserRoles(userResource);
            userDAO.insert(newUser);

        } catch (Exception ex) {
            oaccContext.deleteResource(userResource);
            throw ex;
        }

        return new User(newUser.getEmail());
    }


    private static Resource createUserResource(User user, AccessControlContext oaccContext) {
        final Resource userResource;
        try {
            userResource = oaccContext.createResource(SecurityModel.RESOURCECLASS_USER,
                    SecurityModel.DOMAIN_DELEGATION,
                    user.getEmail(),
                    PasswordCredentials.newInstance(user.getPassword()));
        }
        catch (IllegalArgumentException e) {
            if(e.getMessage() != null && e.getMessage().contains("External id is not unique")) {
                final String msg = String .format("A user with email %s already exists.", user.getEmail());
                throw new IllegalArgumentException(msg);
            }
            else
                throw e;
        }

        return userResource;

    }

    private void assignUserRoles(Resource userResource) {
        //assign roles to the created user
        final AccessControlContext roleHelperContext = oaccFactory.build();
        roleHelperContext.authenticate(SecurityModel.RESOURCE_ROLEHELPER_OWNER,
                SecurityModel.RESOURCEPASSWORD_ROLEHELPER_OWNER);

        roleHelperContext.grantResourcePermissions(userResource,
                SecurityModel.RESOURCE_ROLE_OWNER,
                SecurityModel.PERM_INHERIT);
    }

    private static void assertUserIsValid(User user) {
        Objects.requireNonNull(user, "User is required.");

        final String rawEmail = user.getEmail();

        if(rawEmail == null )
            throw new IllegalArgumentException("Email is required.");

        if(rawEmail.trim().length() != rawEmail.length())
            throw  new IllegalArgumentException("Email cannot start or end with whitespace.");

        if(!EMAIL_VALIDATOR.isValid(rawEmail, null))
            throw new IllegalArgumentException("Email must be well-formed email address");

        final char[] rawPassword = user.getPassword();

        if(rawPassword == null)
            throw new IllegalArgumentException("Password is required");

        if(rawPassword.length < 1)
            throw new IllegalArgumentException("Password length must be one or greater");

    }
}
