package com.cowerling.pmn.geodata;

public interface GeoAuthenticationRepository {
    void saveUser(String username);
    void removeUser(String username);
}
