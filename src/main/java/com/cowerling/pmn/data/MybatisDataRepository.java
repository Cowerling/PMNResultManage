package com.cowerling.pmn.data;

import com.cowerling.pmn.annotation.GenericData;
import com.cowerling.pmn.data.mapper.DataMapper;
import com.cowerling.pmn.domain.data.*;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.NoSuchDataRecordCategoryException;
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
    public DataRecord findDataRecordsById(Long id) {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);
            return dataMapper.selectDataRecordsById(id);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<DataRecord> findDataRecordsByUser(User user, Map<RecordField, Object> filters, List<Pair<RecordField, Order>> orders, int offset, int limit) {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);
            RowBounds rowBounds = new RowBounds(offset, limit);

            return dataMapper.selectDataRecordsByUserId(user.getId(), filters, orders, rowBounds);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public Long findDataRecordCountByUser(User user, Map<RecordField, Object> filters) {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);

            return dataMapper.selectDataRecordCountByUserId(user.getId(), filters);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<DataRecordAuthority> findDataRecordAuthorities(DataRecord dataRecord, User associator) {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);

            return dataMapper.selectDataRecordAuthoritiesByDataRecordId(dataRecord.getId(), associator.getId());
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void saveDataRecordAuthority(DataRecord dataRecord, User associator, DataRecordAuthority dataRecordAuthority) {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);
            dataMapper.insertDataRecordAuthorityByDataRecordId(dataRecord.getId(), associator.getId(), dataRecordAuthority);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void saveDataRecordAuthorities(DataRecord dataRecord, User associator, DataRecordAuthority[] dataRecordAuthorities) {
        for (DataRecordAuthority dataRecordAuthority: dataRecordAuthorities) {
            saveDataRecordAuthority(dataRecord ,associator, dataRecordAuthority);
        }
    }

    @Override
    public void removeDataRecordAuthorities(DataRecord dataRecord) {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);
            dataMapper.deleteDataRecordAuthoritiesByDataRecordId(dataRecord.getId());
            sqlSession.commit();
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

    @Override
    public void removeDataRecordSeparately(DataRecord dataRecord) {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);
            dataMapper.deleteDataRecord(dataRecord);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void removeDataRecord(DataRecord dataRecord) throws NoSuchDataRecordCategoryException {
        removeDataRecordAuthorities(dataRecord);
        removeDataContentsByDataRecord(dataRecord);
        removeDataRecordSeparately(dataRecord);
    }

    @Override
    public List<? extends DataContent> findDataContentsByDataRecord(DataRecord dataRecord) throws NoSuchDataRecordCategoryException {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);

            switch (dataRecord.getCategory()) {
                case CPI_BASE:
                    return dataMapper.selectCPIBaseDataContentsByDataRecordId(dataRecord.getId());
                default:
                    throw new NoSuchDataRecordCategoryException();
            }
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void saveDataContentByDataRecord(DataRecord dataRecord, DataContent dataContent) throws NoSuchDataRecordCategoryException {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);

            switch (dataRecord.getCategory()) {
                case CPI_BASE:
                    dataMapper.insertCPIBaseDataContentByDataRecordId(dataRecord.getId(), (CPIBaseDataContent) dataContent);
                    sqlSession.commit();
                    break;
                default:
                    throw new NoSuchDataRecordCategoryException();
            }
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void saveDataContentsByDataRecord(DataRecord dataRecord, List<? extends DataContent> dataContents) throws NoSuchDataRecordCategoryException {
        for (DataContent dataContent: dataContents) {
            saveDataContentByDataRecord(dataRecord, dataContent);
        }
    }

    @Override
    public void removeDataContentsByDataRecord(DataRecord dataRecord) throws NoSuchDataRecordCategoryException {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);

            switch (dataRecord.getCategory()) {
                case CPI_BASE:
                    dataMapper.deleteCPIBaseDataContentsByDataRecordId(dataRecord.getId());
                    sqlSession.commit();
                    break;
                default:
                    throw new NoSuchDataRecordCategoryException();
            }
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void removeDataContentById(DataRecordCategory dataRecordCategory, Long id) throws NoSuchDataRecordCategoryException {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);

            switch (dataRecordCategory) {
                case CPI_BASE:
                    dataMapper.deleteCPIBaseDataContent(id);
                    sqlSession.commit();
                    break;
                default:
                    throw new NoSuchDataRecordCategoryException();
            }
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void updateDataContent(DataRecordCategory dataRecordCategory, DataContent dataContent) throws NoSuchDataRecordCategoryException {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);

            switch (dataRecordCategory) {
                case CPI_BASE:
                    dataMapper.updateCPIBaseDataContent((CPIBaseDataContent) dataContent);
                    sqlSession.commit();
                    break;
                default:
                    throw new NoSuchDataRecordCategoryException();
            }
        } finally {
            sqlSession.close();
        }
    }
}
