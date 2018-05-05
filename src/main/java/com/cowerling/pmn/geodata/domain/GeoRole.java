package com.cowerling.pmn.geodata.domain;

public class GeoRole {
    private String user;

    private GeoRole(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public static GeoRole admin() {
        return new GeoRole("admin");
    }
}
