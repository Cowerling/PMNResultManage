package com.cowerling.pmn.data;

import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.ProjectSqlProvider.*;

public interface ProjectRepository {
    Project findProjectById(Long id);
    List<Project> findProjectsByUser(User user, FindMode findMode, Map<Field, Object> filters, List<Pair<Field, Order>> orders, int offset, int limit);
    Long findProjectCountByUser(User user, FindMode findMode);
    void updateProject(Project project);
    void saveProject(Project project);
    void removeProjectById(Long id);
}
