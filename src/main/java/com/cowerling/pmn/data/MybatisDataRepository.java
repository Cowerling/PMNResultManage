package com.cowerling.pmn.data;

import com.cowerling.pmn.annotation.GenericData;
import com.cowerling.pmn.data.mapper.DataMapper;
import com.cowerling.pmn.domain.data.DataRecord;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.DataSqlProvider.*;

@Repository
public class MybatisDataRepository implements DataRepository {
    @Autowired
    @GenericData
    private SqlSessionFactory sqlSessionFactory;

    private SqlSession currentSession() {
        return sqlSessionFactory.openSession();
    }

    @Override
    public List<DataRecord> findDataRecords(Map<RecordField, Object> filters, List<Pair<RecordField, Order>> orders, int offset, int limit) {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);
            RowBounds rowBounds = new RowBounds(offset, limit);

            return dataMapper.selectDataRecords(filters, orders, rowBounds);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public Long findDataRecordCount(Map<RecordField, Object> filters) {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);

            return dataMapper.selectDataRecordCount(filters);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void saveDataRecord(DataRecord dataRecord) {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);
            dataMapper.insertDataRecord(dataRecord);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void updateDataRecord(DataRecord dataRecord) {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);
            dataMapper.updateDataRecord(dataRecord);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}
