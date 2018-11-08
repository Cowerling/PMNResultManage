package com.cowerling.pmn.data.mapper;

import com.cowerling.pmn.data.provider.ProjectSqlProvider;
import com.cowerling.pmn.domain.data.DataRecordCategory;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.project.ProjectCategory;
import com.cowerling.pmn.domain.project.ProjectVerification;
import org.apache.commons.lang3.tuple.Pair;
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
    Long selectProjectCountByUserId(Long userId, FindMode findMode, Map<Field, Object> filters);

    @UpdateProvider(type = ProjectSqlProvider.class, method = "updateProject")
    void updateProject(Project project);

    @Insert("INSERT INTO t_project(name, category, creator, create_time, remark, status) VALUES(#{name}, (SELECT id FROM t_project_category WHERE category = #{category}), #{creator.id}, #{createTime}, #{remark}, (SELECT id FROM t_project_status WHERE category = #{status}))")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertProject(Project project);

    @Delete("DELETE FROM t_project WHERE id = #{id}")
    void deleteProjectById(Long id);

    @Select("SELECT t_data_record_category.category AS data_record_category " +
            "FROM t_project_relate_data_record " +
            "LEFT OUTER JOIN t_data_record_category ON t_project_relate_data_record.data_record_category = t_data_record_category.id " +
            "LEFT OUTER JOIN t_project_category ON t_project_relate_data_record.project_category = t_project_category.id " +
            "WHERE t_project_category.category = #{projectCategory}")
    List<DataRecordCategory> selectDataRecordCategoriesByProjectCategory(ProjectCategory projectCategory);

    @Select("SELECT principal_adopt, manager_adopt, creator_adopt, principal_remark, manager_remark, creator_remark " +
            "FROM t_project_verification " +
            "WHERE project = #{projectId}")
    @ResultMap("com.cowerling.pmn.data.mapper.ProjectMapper.projectVerificationResult")
    ProjectVerification selectProjectVerificationByProjectId(Long projectId);

    @Insert("INSERT INTO t_project_verification(project, principal_adopt, manager_adopt, creator_adopt) " +
            "VALUES(#{projectId}, #{projectVerification.principalAdopt}, #{projectVerification.managerAdopt}, #{projectVerification.creatorAdopt})")
    void insertProjectVerification(@Param("projectId") Long projectId, @Param("projectVerification") ProjectVerification projectVerification);

    @Update("UPDATE t_project_verification " +
            "SET principal_adopt = #{projectVerification.principalAdopt}, manager_adopt = #{projectVerification.managerAdopt}, creator_adopt = #{projectVerification.creatorAdopt}, principal_remark = #{projectVerification.principalRemark}, manager_remark = #{projectVerification.managerRemark}, creator_remark = #{projectVerification.creatorRemark} " +
            "WHERE project = #{projectId}")
    void updateProjectVerification(@Param("projectId") Long projectId, @Param("projectVerification") ProjectVerification projectVerification);

    @Delete("DELETE FROM t_project_verification WHERE project = #{projectId}")
    void deleteProjectVerificationByProjectId(Long projectId);
}
