package com.cowerling.pmn.data;

import com.cowerling.pmn.domain.department.Department;

import java.util.List;

public interface DepartmentRepository {
    Department findDepartmentById(Long id);
    Department findDepartmentByName(String name);
    List<Department> findDepartments();
}
