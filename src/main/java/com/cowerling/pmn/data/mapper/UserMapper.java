package com.cowerling.pmn.data.mapper;

import com.cowerling.pmn.data.provider.UserSqlProvider;
import com.cowerling.pmn.domain.user.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.UserSqlProvider.*;

public interface UserMapper {
    @Select("SELECT t_user.id, name, password, email, alias, t_user_gender.category AS gender, birthday, phone, register_date, photo, t_user_role.category AS role, department FROM t_user " +
            "LEFT OUTER JOIN t_user_gender ON t_user.gender = t_user_gender.id " +
            "LEFT OUTER JOIN t_user_role ON t_user.role = t_user_role.id " +
            "WHERE t_user.name = #{name}")
    @ResultMap("com.cowerling.pmn.data.mapper.UserMapper.userResult")
    User selectUserByName(String name);

    @Select("SELECT t_user.id, name, password, email, alias, t_user_gender.category AS gender, birthday, phone, register_date, photo, t_user_role.category AS role, department FROM t_user " +
            "LEFT OUTER JOIN t_user_gender ON t_user.gender = t_user_gender.id " +
            "LEFT OUTER JOIN t_user_role ON t_user.role = t_user_role.id " +
            "WHERE t_user.id = #{id}")
    @ResultMap("com.cowerling.pmn.data.mapper.UserMapper.userResult")
    User selectUserById(Long id);

    @Select("SELECT t_user.id, name, password, email, alias, t_user_gender.category AS gender, birthday, phone, register_date, photo, t_user_role.category AS role, department FROM t_user " +
            "LEFT OUTER JOIN t_user_gender ON t_user.gender = t_user_gender.id " +
            "LEFT OUTER JOIN t_user_role ON t_user.role = t_user_role.id " +
            "WHERE alias = #{alias}")
    @ResultMap("com.cowerling.pmn.data.mapper.UserMapper.userResult")
    List<User> selectUsersByAlias(String alias);

    @Select("SELECT t_user.id, name, password, email, alias, t_user_gender.category AS gender, birthday, phone, register_date, photo, t_user_role.category AS role, department FROM t_user " +
            "LEFT OUTER JOIN t_user_gender ON t_user.gender = t_user_gender.id " +
            "LEFT OUTER JOIN t_user_role ON t_user.role = t_user_role.id " +
            "LEFT OUTER JOIN t_project_members ON t_user.id = t_project_members.member " +
            "WHERE t_project_members.project = #{projectId}")
    @ResultMap("com.cowerling.pmn.data.mapper.UserMapper.userResult")
    List<User> selectUsersByProjectId(Long projectId);

    @SelectProvider(type = UserSqlProvider.class, method = "selectUserCountByDepartmentId")
    int selectUserCountByDepartmentId(Long departmentId, Map<Field, Object> filters);

    @SelectProvider(type = UserSqlProvider.class, method = "selectUsersByDepartmentId")
    @ResultMap("com.cowerling.pmn.data.mapper.UserMapper.userResult")
    List<User> selectUsersByDepartmentId(Long departmentId, Map<Field, Object> filters);

    @Insert("INSERT INTO t_user(name, password, alias, phone, register_date, role) VALUES(#{name}, #{password}, #{alias}, #{phone}, #{registerDate}, (SELECT id FROM t_user_role WHERE category = #{userRole}))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);

    @Insert("INSERT INTO t_project_members(project, member) VALUES(#{projectId}, #{userId})")
    void insertMemberIdByProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Update("UPDATE t_user " +
            "SET name = #{name}, password = #{password}, email = #{email}, alias = #{alias}, gender = (SELECT id FROM t_user_gender WHERE category = #{userGender}), birthday = #{birthday}, phone = #{phone}, register_date = #{registerDate}, photo = #{photo}, role = (SELECT id FROM t_user_role WHERE category = #{userRole}) " +
            "WHERE id = #{id}")
    void updateUser(User user);

    @Delete("DELETE FROM t_project_members WHERE project = #{projectId} AND member = #{userId}")
    void deleteMemberIdByProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);
}
