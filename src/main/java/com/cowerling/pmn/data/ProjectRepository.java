package com.cowerling.pmn.data;

import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;

import java.util.List;

public interface ProjectRepository {
    enum FindMode {
        CREATOR, MANAGER, PRINCIPAL, PARTICIPATOR
    }

    List<Project> findProjectsByUser(User user, FindMode findMode, int offset, int limit);
    Long findProjectCountByUser(User user, FindMode findMode);
}
