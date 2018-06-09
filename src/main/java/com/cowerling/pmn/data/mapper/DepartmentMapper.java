package com.cowerling.pmn.data.mapper;

import com.cowerling.pmn.domain.department.Department;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DepartmentMapper {
    @Select("SELECT id, name FROM t_department " +
            "WHERE id = #{id}")
    @ResultMap("com.cowerling.pmn.data.mapper.DepartmentMapper.departmentResult")
    Department selectDepartmentById(Long id);

    @Select("SELECT id, name FROM t_department " +
            "WHERE name = #{name}")
    @ResultMap("com.cowerling.pmn.data.mapper.DepartmentMapper.departmentResult")
    Department selectDepartmentByName(String name);

    @Select("SELECT id, name FROM t_department")
    @ResultMap("com.cowerling.pmn.data.mapper.DepartmentMapper.departmentResult")
    List<Department> selectDepartments();
}
