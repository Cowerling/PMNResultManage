package com.cowerling.pmn.data.mapper;

import com.cowerling.pmn.data.provider.ProjectSqlProvider;
import com.cowerling.pmn.domain.project.Project;
import javafx.util.Pair;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.ProjectSqlProvider.*;

public interface ProjectMapper {
    @SelectProvider(type = ProjectSqlProvider.class, method = "selectProjectsById")
    @ResultMap("com.cowerling.pmn.data.mapper.ProjectMapper.projectResult")
    Project selectProjectById(Long id);

    @SelectProvider(type = ProjectSqlProvider.class, method = "selectProjectsByUserId")
    @ResultMap("com.cowerling.pmn.data.mapper.ProjectMapper.projectResult")
    List<Project> selectProjectsByUserId(Long userId, FindMode findMode, Map<Field, Object> filters, List<Pair<Field, Order>> orders, RowBounds rowBounds);

    @SelectProvider(type = ProjectSqlProvider.class, method = "selectProjectCountByUserId")
    Long selectProjectCountByUserId(Long userId, FindMode findMode);

    @UpdateProvider(type = ProjectSqlProvider.class, method = "updateProject")
    void updateProject(Project project);

    @Insert("INSERT INTO t_project(name, category, creator, create_time, remark, status) VALUES(#{name}, (SELECT id FROM t_project_category WHERE category = #{category}), #{creator.id}, #{createTime}, #{remark}, (SELECT id FROM t_project_status WHERE category = #{status}))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertProject(Project project);

    @Delete("DELETE FROM t_project WHERE id = #{id}")
    void deleteProjectById(Long id);
}
