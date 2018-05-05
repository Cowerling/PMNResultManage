package com.cowerling.pmn.geodata.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.postgis.MultiPoint;

public interface GeoCityMapper {
    @Select("SELECT geom FROM city_point WHERE name LIKE CONCAT('%', #{name}, '%')")
    MultiPoint selectLocationByName(String name);

    @Insert("INSERT INTO city_point(name, geom) VALUES(#{name}, #{location})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertLocation(@Param("name") String name, @Param("location") MultiPoint location);
}
