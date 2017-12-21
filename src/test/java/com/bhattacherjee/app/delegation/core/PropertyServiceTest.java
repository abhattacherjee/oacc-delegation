package com.bhattacherjee.app.delegation.core;

import com.acciente.oacc.AccessControlContext;
import com.acciente.oacc.Resource;
import com.bhattacherjee.app.delegation.api.Property;
import com.bhattacherjee.app.delegation.db.PropertyDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;



public class PropertyServiceTest {
    private PropertyDAO dao;
    private AccessControlContext oaccContext;
    private PropertyService service;

    @Before
    public void setup() throws Exception {
        dao = mock(PropertyDAO.class);
        oaccContext = mock(AccessControlContext.class);


        service = new PropertyService(dao);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCreateProperty_Success() throws Exception {

        Property submittedProperty =  new Property(1L, "test-property", 112233);

        when(dao.findById(1)).thenReturn(new Property(1L, "test-property", 112233));
        when(dao.insert(submittedProperty)).thenReturn(1L);

        Property retrievedProperty = service.createProperty(oaccContext, submittedProperty);

        assertThat(retrievedProperty).isEqualTo(submittedProperty);

        verify(dao).insert(submittedProperty);
        verify(dao, times(1)).findById(1L);
        verify(oaccContext, times(1))
                .createResource(SecurityModel.RESOURCECLASS_PROPERTY, SecurityModel.DOMAIN_DELEGATION, "1");
        verify(oaccContext, never()).deleteResource(any(Resource.class));

    }
}
