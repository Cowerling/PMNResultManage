package com.cowerling.pmn.domain.user;

import java.util.Arrays;

public enum UserRole {
    SUPER_ADMIN("超级管理员"), ADMIN("管理员"), ADVAN_USER("高级用户"), USER("普通用户");

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
