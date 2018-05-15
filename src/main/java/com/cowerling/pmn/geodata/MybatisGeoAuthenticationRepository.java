package com.cowerling.pmn.geodata;

import com.cowerling.pmn.annotation.GeoData;
import com.cowerling.pmn.geodata.mapper.GeoAuthenticationMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MybatisGeoAuthenticationRepository implements GeoAuthenticationRepository {
    private final static String BASE_GROUP_NAME = "pmn";

    @Autowired
    @GeoData
    private SqlSessionFactory sqlSessionFactory;

    private SqlSession currentSession() {
        return sqlSessionFactory.openSession();
    }

    @Override
    public void saveUser(String username) {
        SqlSession sqlSession = currentSession();

        try {
            GeoAuthenticationMapper geoAuthenticationMapper = sqlSession.getMapper(GeoAuthenticationMapper.class);

            geoAuthenticationMapper.insertUser(username);
            geoAuthenticationMapper.insertGroupMember(BASE_GROUP_NAME, username);

            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}
