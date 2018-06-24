package com.cowerling.pmn.data;

import com.cowerling.pmn.domain.department.Department;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.domain.user.UserRole;
import com.cowerling.pmn.exception.DuplicateMemberException;
import com.cowerling.pmn.exception.DuplicateUserException;

import java.util.List;

public interface UserRepository {
    User findUserByName(String name);
    User findUserById(Long id);
    List<User> findUsersByAlias(String alias);
    List<User> findUsersByDepartmentId(Long departmentId);
    List<User> findUsersByDepartmentId(Long departmentId, UserRole userRole);
    int findUserCountByDepartment(Department department);
    int findUserCountByDepartment(Department department, UserRole userRole);
    void saveUser(User user) throws DuplicateUserException;
    void saveMemberByProject(User user, Project project) throws DuplicateMemberException;
    void updateUser(User user);
}
