package com.bhattacherjee.app.delegation.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class User {
    private final String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final char[] password;

    private User() {
        email = null;
        password = null;
    }

    public User(String email) {
        this.email = email;
        this.password = null;
    }

    public User(String email, char[] password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;

        User user = (User)obj;
        return email != null ? email.equals(user.email) : user.email == null;
    }
}
