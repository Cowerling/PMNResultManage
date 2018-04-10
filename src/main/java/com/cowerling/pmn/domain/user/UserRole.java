package com.cowerling.pmn.domain.user;

import java.util.Arrays;

public enum UserRole {
    ADMIN("Administrator"), ADVAN_USER("Advanced User"), USER("User");

    private String description;

    UserRole(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public String format() {
        return name().toLowerCase().replace('_', '-');
    }

    public UserRole[] subordinate() {
        return Arrays.stream(UserRole.values()).filter(x -> x.ordinal() > this.ordinal()).toArray(size -> new UserRole[size]);
    }
}
