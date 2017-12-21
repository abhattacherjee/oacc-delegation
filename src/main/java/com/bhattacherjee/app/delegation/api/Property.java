package com.bhattacherjee.app.delegation.api;

import com.bhattacherjee.app.delegation.resources.PropertyResource;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.core.UriBuilder;

@Getter @Setter @JsonAutoDetect
public class Property {

    private final long id;
    private final String title;
    private final int postalCd;
    private final String url;

    private Property() {
        id = -1;
        title = null;
        postalCd = -1;
        url = null;
    }

    public Property(long id, String title, int postalCd) {
        this.id = id;
        this.title = title;
        this.postalCd = postalCd;
        this.url = UriBuilder.fromResource(PropertyResource.class).path("{propertyId}").build(id).toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;

        if(o == null || getClass() !=o.getClass())
            return false;

        if(title != null && !title.equals(((Property) o).title))
            return  false;

        return true;
    }
}
