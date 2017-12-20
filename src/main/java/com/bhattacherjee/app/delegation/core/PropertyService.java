package com.bhattacherjee.app.delegation.core;

import com.acciente.oacc.AccessControlContext;
import com.acciente.oacc.Resource;
import com.acciente.oacc.Resources;
import com.bhattacherjee.app.delegation.api.Property;
import com.bhattacherjee.app.delegation.db.PropertyDAO;

import java.util.*;
import java.util.stream.Collectors;

public class PropertyService {
    private PropertyDAO propertyDAO;

    public PropertyService(PropertyDAO propertyDAO) {
        this.propertyDAO = propertyDAO;
    }

    public Property createProperty(AccessControlContext oaccContext, Property newProperty) {
        assertPropertyIsValidForCreation(newProperty);
        final long newId = propertyDAO.insert(newProperty);

        final Property property;
        try {
            property = propertyDAO.findById(newId);
            oaccContext.createResource(SecurityModel.RESOURCECLASS_PROPERTY,
                    SecurityModel.DOMAIN_DELEGATION,
                    String.valueOf(property.getId()));
        } catch(Exception ex) {
            ex.printStackTrace();
            propertyDAO.delete(newId);
            throw ex;
        }
        return property;
    }

    public List<Property> findPropertiesForAuthenticatedUser(AccessControlContext oaccContext) {
        final Set<Resource> propertyResources = oaccContext.getResourcesByResourcePermissions(
                                                        oaccContext.getSessionResource(),
                                                        SecurityModel.RESOURCECLASS_PROPERTY,
                                                        SecurityModel.PERM_VIEW);

        if(propertyResources.isEmpty())
            return Collections.emptyList();
        else {
            final List<Long> propertyIds = propertyResources.
                                                stream()
                                                .map(propertyResource -> Long.valueOf(propertyResource.getExternalId()))
                                                .collect(Collectors.toList());
            return propertyDAO.findByIds(propertyIds);
        }
    }

    public Property getPropertyById(AccessControlContext oaccContext, long propertyId) {
        oaccContext.assertResourcePermissions(oaccContext.getSessionResource(),
                                                Resources.getInstance(String.valueOf(propertyId)),
                                                SecurityModel.PERM_VIEW);
        return propertyDAO.findById(propertyId);
    }

    private static void assertPropertyIsValidForCreation(Property property) {
        Objects.requireNonNull(property, "Property item is required");

        final String rawTitle = property.getTitle();

        if(rawTitle == null)
            throw new IllegalArgumentException("Title is required.");

        if(rawTitle.trim().isEmpty())
            throw new IllegalArgumentException("Title cannot be blank.");
    }


}
