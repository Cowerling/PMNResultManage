package com.cowerling.pmn.data;

import com.cowerling.pmn.annotation.GenericData;
import com.cowerling.pmn.data.mapper.UserMapper;
import com.cowerling.pmn.data.message.ExceptionMessage;
import com.cowerling.pmn.domain.department.Department;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.domain.user.UserRole;
import com.cowerling.pmn.exception.DuplicateMemberException;
import com.cowerling.pmn.exception.DuplicateUserException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.UserSqlProvider.*;

@Repository
public class MybatisUserRepository implements UserRepository {
    private static final String UNIQUE_CONSTRAINT_NAME_IN_USER  = "uc_name_in_user";
    private static final String PRIMARY_KEY_PROJECT_MEMBERS = "pk_project_members";

    @Autowired
    @GenericData
    private SqlSessionFactory sqlSessionFactory;

    private SqlSession currentSession() {
        return sqlSessionFactory.openSession();
    }

    @Override
    public User findUserByName(String name) {
        SqlSession sqlSession = currentSession();

        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            return userMapper.selectUserByName(name);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public User findUserById(Long id) {
        SqlSession sqlSession = currentSession();

        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            return userMapper.selectUserById(id);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<User> findUsersByAlias(String alias) {
        SqlSession sqlSession = currentSession();

        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            return userMapper.selectUsersByAlias(alias);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<User> findUsersByDepartmentId(Long departmentId) {
        SqlSession sqlSession = currentSession();

        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            return userMapper.selectUsersByDepartmentId(departmentId, null);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<User> findUsersByDepartmentId(Long departmentId, UserRole userRole) {
        SqlSession sqlSession = currentSession();

        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

            return userMapper.selectUsersByDepartmentId(departmentId, new HashMap<>() {
                {
                    put(Field.ROLE, userRole);
                }
            });
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public int findUserCountByDepartment(Department department) {
        SqlSession sqlSession = currentSession();

        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

            return userMapper.selectUserCountByDepartmentId(department.getId(), null);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public int findUserCountByDepartment(Department department, UserRole userRole) {
        SqlSession sqlSession = currentSession();

        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

            return userMapper.selectUserCountByDepartmentId(department.getId(), new HashMap<>() {
                {
                    put(Field.ROLE, userRole);
                }
            });
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void saveUser(User user) throws DuplicateUserException {
        SqlSession sqlSession = currentSession();

        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.insertUser(user);
            sqlSession.commit();
        } catch (Exception e) {
            if (e.getMessage().contains(UNIQUE_CONSTRAINT_NAME_IN_USER)) {
                throw new DuplicateUserException(ExceptionMessage.SAVE_USER_DUPLICATE);
            }
            else {
                throw e;
            }
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void saveMemberByProject(User user, Project project) throws DuplicateMemberException {
        SqlSession sqlSession = currentSession();

        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.insertMemberIdByProjectId(user.getId(), project.getId());
            sqlSession.commit();
        } catch (Exception e) {
            if (e.getMessage().contains(PRIMARY_KEY_PROJECT_MEMBERS)) {
                throw new DuplicateMemberException();
            }
            else {
                throw e;
            }
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void removeMemberByProject(User user, Project project) {
        SqlSession sqlSession = currentSession();

        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.deleteMemberIdByProjectId(user.getId(), project.getId());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void updateUser(User user)
    {
        SqlSession sqlSession = currentSession();

        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.updateUser(user);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}
