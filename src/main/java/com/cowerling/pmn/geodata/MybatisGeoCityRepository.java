package com.cowerling.pmn.geodata;

import com.cowerling.pmn.annotation.GeoData;
import com.cowerling.pmn.geodata.mapper.GeoCityMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.postgis.MultiPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MybatisGeoCityRepository implements GeoCityRepository {
    @Autowired
    @GeoData
    private SqlSessionFactory sqlSessionFactory;

    private SqlSession currentSession() {
        return sqlSessionFactory.openSession();
    }

    @Override
    public MultiPoint getLocationByName(String name) {
        SqlSession sqlSession = currentSession();

        try {
            GeoCityMapper geoCityMapper = sqlSession.getMapper(GeoCityMapper.class);
            return geoCityMapper.selectLocationByName(name);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void saveLocation(String name, MultiPoint location) {
        SqlSession sqlSession = currentSession();

        try {
            GeoCityMapper geoCityMapper = sqlSession.getMapper(GeoCityMapper.class);
            geoCityMapper.insertLocation(name, location);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}
