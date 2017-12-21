package com.bhattacherjee.app.delegation.auth;


import com.acciente.oacc.AccessControlContext;

import javax.security.auth.Subject;

public class OaccPrincipalImpl implements OaccPrincipal {

    private final AccessControlContext accessControlContext;

    public OaccPrincipalImpl(AccessControlContext accessControlContext) {
        this.accessControlContext = accessControlContext;
    }

    @Override
    public AccessControlContext getAccessControlContext() {
        return accessControlContext;
    }

    @Override
    public String getName() {
        return accessControlContext.getAuthenticatedResource().getExternalId();
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}
