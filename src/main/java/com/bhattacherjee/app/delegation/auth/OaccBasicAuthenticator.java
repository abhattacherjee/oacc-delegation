package com.bhattacherjee.app.delegation.auth;

import com.acciente.oacc.AccessControlContext;
import com.acciente.oacc.PasswordCredentials;
import com.acciente.oacc.Resources;
import com.bhattacherjee.app.delegation.AccessControlContextFactory;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Optional;

public class OaccBasicAuthenticator implements Authenticator<BasicCredentials, OaccPrincipal> {
    private final AccessControlContextFactory oaccFactory;

    public OaccBasicAuthenticator(AccessControlContextFactory oaccFactory) {
        this.oaccFactory = oaccFactory;
    }


    @Override
    public Optional<OaccPrincipal> authenticate(BasicCredentials basicCredentials) throws AuthenticationException {
        OaccPrincipal oaccPrincipal = null;

        if(basicCredentials != null) {
            final String normalizedEmail = basicCredentials.getUsername().trim().toLowerCase();

            AccessControlContext oaccContext = oaccFactory.build();

            try {
                oaccContext.authenticate(Resources.getInstance(normalizedEmail),
                        PasswordCredentials.newInstance(basicCredentials.getPassword().toCharArray()));
                oaccPrincipal = new OaccPrincipalImpl(oaccContext);
            } catch (Exception ex) {
                // swallow the exception
                ex.printStackTrace();
            }

            return Optional.ofNullable(oaccPrincipal);

        }


        return null;
    }
}
