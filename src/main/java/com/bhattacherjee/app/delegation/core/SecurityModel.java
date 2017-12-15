package com.bhattacherjee.app.delegation.core;

import com.acciente.oacc.ResourcePermission;
import com.acciente.oacc.ResourcePermissions;

public class SecurityModel {
    public static final String DOMAIN_DELEGATION = "delegation";

    public static final String RESOURCECLASS_USER = "user";
    public static final String RESOURCECLASS_PROPERTY = "property";
    public static final String RESOURCECLASS_ROLEHELPER = "role-helper";
    public static final String RESOURCECLASS_ROLE = "role";
    public static final String RESOURCECLASS_PROPERTYRATE = "rate";
    public static final String RESOURCECLASS_PROPERTYAVAILABILITY = "availability";

    public static final ResourcePermission PERM_INHERIT = ResourcePermissions.getInstance(ResourcePermissions.INHERIT);
    public static final ResourcePermission PERM_VIEW = ResourcePermissions.getInstance("VIEW");
    public static final ResourcePermission PERM_EDIT = ResourcePermissions.getInstance("EDIT");
    public static final ResourcePermission PERM_DELETE = ResourcePermissions.getInstance("DELETE");
    public static final ResourcePermission PERM_INHERITWITHGRANT = ResourcePermissions.getInstanceWithGrantOption(ResourcePermissions.INHERIT);
    public static final ResourcePermission PERM_VIEWWITHGRANT = ResourcePermissions.getInstance("VIEW");

}
