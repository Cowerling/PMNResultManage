package com.cowerling.pmn.geodata.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface GeoAuthenticationMapper {
    @Insert("INSERT INTO users(name, password, enabled) VALUES(#{name}, 'empty:', 'Y')")
    void insertUser(@Param("name") String name);

    @Delete("DELETE FROM users WHERE name = #{name}")
    void deleteUser(@Param("name") String name);

    @Insert("INSERT INTO group_members(groupname, username) VALUES(#{groupname}, #{username})")
    void insertGroupMember(@Param("groupname") String groupName, @Param("username") String username);

    @Delete("DELETE FROM group_members WHERE username = #{username}")
    void deleteGroupMember(@Param("username") String username);
}
