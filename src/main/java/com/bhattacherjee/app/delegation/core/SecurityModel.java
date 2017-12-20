package com.bhattacherjee.app.delegation.core;

import com.acciente.oacc.*;

public class SecurityModel {
    public static final String DOMAIN_DELEGATION = "delegation";

    public static final String RESOURCECLASS_USER = "user";
    public static final String RESOURCECLASS_PROPERTY = "property";
    public static final String RESOURCECLASS_ROLEHELPER = "role-helper";
    public static final String RESOURCECLASS_ROLE = "role";
    public static final String RESOURCECLASS_PROPERTYRATE = "rate";
    public static final String RESOURCECLASS_PROPERTYAVAILABILITY = "availability";

    public static final ResourceCreatePermission PERM_CREATE = ResourceCreatePermissions.getInstance(ResourceCreatePermissions.CREATE);
    public static final ResourcePermission PERM_INHERIT = ResourcePermissions.getInstance(ResourcePermissions.INHERIT);
    public static final ResourcePermission PERM_VIEW = ResourcePermissions.getInstance("VIEW");
    public static final ResourcePermission PERM_EDIT = ResourcePermissions.getInstance("EDIT");
    public static final ResourcePermission PERM_DELETE = ResourcePermissions.getInstance("DELETE");

    public static final ResourceCreatePermission PERM_POSTCREATE_VIEW = ResourceCreatePermissions.getInstance(PERM_VIEW);
    public static final ResourceCreatePermission PERM_POSTCREATE_EDIT = ResourceCreatePermissions.getInstance(PERM_EDIT);
    public static final ResourceCreatePermission PERM_POSTCREATE_DELETE = ResourceCreatePermissions.getInstance(PERM_DELETE);

    public static final ResourcePermission PERM_INHERITWITHGRANT = ResourcePermissions.getInstanceWithGrantOption(ResourcePermissions.INHERIT);
    public static final ResourcePermission PERM_VIEWWITHGRANT = ResourcePermissions.getInstance("VIEW");

    public static final String RESOURCENAME_ROLE_OWNER = "owner-role";
    public static final String RESOURCENAME_ROLEHELPER_OWNER = "owner-role-helper";

    public static final Credentials RESOURCEPASSWORD_ROLE_OWNER = PasswordCredentials.newInstance("ownerRolePassword".toCharArray());
    public static final Credentials RESOURCEPASSWORD_ROLEHELPER_OWNER = PasswordCredentials.newInstance("ownerRoleHelperPassword".toCharArray());

    public static final Resource RESOURCE_ROLEHELPER_OWNER = Resources.getInstance(RESOURCENAME_ROLEHELPER_OWNER);
    public static final Resource RESOURCE_ROLE_OWNER = Resources.getInstance(RESOURCENAME_ROLE_OWNER);

}
