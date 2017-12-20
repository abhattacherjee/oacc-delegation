package com.bhattacherjee.app.delegation.auth;


import com.acciente.oacc.AccessControlContext;

import java.security.Principal;

public interface OaccPrincipal extends Principal {
    AccessControlContext getAccessControlContext();
}
