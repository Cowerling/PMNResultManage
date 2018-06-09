package com.cowerling.pmn.data;

import com.cowerling.pmn.annotation.GenericData;
import com.cowerling.pmn.data.mapper.DepartmentMapper;
import com.cowerling.pmn.domain.department.Department;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MybatisDepartmentRepository implements DepartmentRepository {
    @Autowired
    @GenericData
    private SqlSessionFactory sqlSessionFactory;

    private SqlSession currentSession() {
        return sqlSessionFactory.openSession();
    }

    @Override
    public Department findDepartmentByName(String name) {
        SqlSession sqlSession = currentSession();

        try {
            DepartmentMapper departmentMapper = sqlSession.getMapper(DepartmentMapper.class);

            return departmentMapper.selectDepartmentByName(name);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<Department> findDepartments() {
        SqlSession sqlSession = currentSession();

        try {
            DepartmentMapper departmentMapper = sqlSession.getMapper(DepartmentMapper.class);

            return departmentMapper.selectDepartments();
        } finally {
            sqlSession.close();
        }
    }
}
