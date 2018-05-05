package com.cowerling.pmn.geodata;

import org.postgis.MultiPoint;

public interface GeoCityRepository {
    MultiPoint getLocationByName(String name);
    void saveLocation(String name, MultiPoint location);
}
