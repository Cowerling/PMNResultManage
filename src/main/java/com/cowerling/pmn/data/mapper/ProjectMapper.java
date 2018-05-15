package com.cowerling.pmn.data.mapper;

import com.cowerling.pmn.domain.project.Project;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface ProjectMapper {
    @Select("SELECT t_project.id, name, t_project_category.category AS category, creator, create_time, manager, principal, remark, t_project_status.category AS status FROM t_project " +
            "LEFT OUTER JOIN t_project_category ON t_project.category = t_project_category.id " +
            "LEFT OUTER JOIN t_project_status ON t_project.status = t_project_status.id " +
            "WHERE creator = #{creatorId} " +
            "ORDER BY t_project.id")
    @ResultMap("com.cowerling.pmn.data.mapper.ProjectMapper.projectResult")
    List<Project> selectProjectsByCreatorId(Long creatorId, RowBounds rowBounds);

    @Select("SELECT COUNT(*) FROM t_project " +
            "WHERE creator = #{creatorId}")
    Long selectProjectCountByCreatorId(Long creatorId);

    @Select("SELECT t_project.id, name, t_project_category.category AS category, creator, create_time, manager, principal, remark, t_project_status.category AS status FROM t_project " +
            "LEFT OUTER JOIN t_project_category ON t_project.category = t_project_category.id " +
            "LEFT OUTER JOIN t_project_status ON t_project.status = t_project_status.id " +
            "WHERE manager = #{managerId} " +
            "ORDER BY t_project.id")
    @ResultMap("com.cowerling.pmn.data.mapper.ProjectMapper.projectResult")
    List<Project> selectProjectsByManagerId(Long managerId, RowBounds rowBounds);

    @Select("SELECT COUNT(*) FROM t_project " +
            "WHERE manager = #{managerId}")
    Long selectProjectCountByManagerId(Long managerId);

    @Select("SELECT t_project.id, name, t_project_category.category AS category, creator, create_time, manager, principal, remark, t_project_status.category AS status FROM t_project " +
            "LEFT OUTER JOIN t_project_category ON t_project.category = t_project_category.id " +
            "LEFT OUTER JOIN t_project_status ON t_project.status = t_project_status.id " +
            "WHERE principal = #{principalId} " +
            "ORDER BY t_project.id")
    @ResultMap("com.cowerling.pmn.data.mapper.ProjectMapper.projectResult")
    List<Project> selectProjectsByPrincipalId(Long principalId, RowBounds rowBounds);

    @Select("SELECT COUNT(*) FROM t_project " +
            "WHERE principal = #{principalId}")
    Long selectProjectCountByPrincipalId(Long principalId);

    @Select("SELECT t_project.id, name, t_project_category.category AS category, creator, create_time, manager, principal, remark, t_project_status.category AS status FROM t_project " +
            "LEFT OUTER JOIN t_project_category ON t_project.category = t_project_category.id " +
            "LEFT OUTER JOIN t_project_members ON t_project.id = t_project_members.project " +
            "WHERE t_project_members.member = #{participatorId} " +
            "ORDER BY t_project.id")
    @ResultMap("com.cowerling.pmn.data.mapper.ProjectMapper.projectResult")
    List<Project> selectProjectsByParticipatorId(Long participatorId, RowBounds rowBounds);

    @Select("SELECT COUNT(*) FROM t_project_members " +
            "WHERE member = #{participatorId}")
    Long selectProjectCountByParticipatorId(Long participatorId);
}
