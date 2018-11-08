package com.cowerling.pmn.data;

import com.cowerling.pmn.domain.data.DataRecordCategory;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.project.ProjectVerification;
import com.cowerling.pmn.domain.user.User;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.ProjectSqlProvider.*;

public interface ProjectRepository {
    Project findProjectById(Long id);
    List<Project> findProjectsByUser(User user, FindMode findMode, Map<Field, Object> filters, List<Pair<Field, Order>> orders, int offset, int limit);
    List<Project> findProjectsByUser(User user, FindMode findMode, Map<Field, Object> filters, List<Pair<Field, Order>> orders);
    Long findProjectCountByUser(User user, FindMode findMode, Map<Field, Object> filters);
    void updateProject(Project project);
    void saveProject(Project project);
    void removeProjectSeparately(Project project);
    void removeProject(Project project);
    List<DataRecordCategory> findDataRecordCategoriesByProject(Project project);

    void saveProjectVerification(Project project, ProjectVerification projectVerification);
    void updateProjectVerification(Project project, ProjectVerification projectVerification);
    void removeProjectVerification(Project project);
}
