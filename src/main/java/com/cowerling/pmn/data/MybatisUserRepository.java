package com.cowerling.pmn.data;

import com.cowerling.pmn.annotation.GenericData;
import com.cowerling.pmn.data.mapper.UserMapper;
import com.cowerling.pmn.data.message.ExceptionMessage;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.DuplicateUserException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MybatisUserRepository implements UserRepository {
    private static final String UNIQUE_CONSTRAINT_NAME_IN_USER  = "uc_name_in_user";

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
