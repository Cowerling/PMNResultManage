package com.cowerling.pmn.data.mapper;

import com.cowerling.pmn.data.provider.ProjectSqlProvider;
import com.cowerling.pmn.domain.project.Project;
import javafx.util.Pair;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;
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
}
