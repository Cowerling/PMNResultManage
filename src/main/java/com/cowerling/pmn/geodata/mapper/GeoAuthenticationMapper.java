package com.cowerling.pmn.geodata.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface GeoAuthenticationMapper {
    @Insert("INSERT INTO users(name, password, enabled) VALUES(#{name}, 'empty:', 'Y')")
    void insertUser(@Param("name") String name);

    @Insert("INSERT INTO group_members(groupname, username) VALUES(#{groupname}, #{username})")
    void insertGroupMember(@Param("groupname") String groupName, @Param("username") String username);
}
