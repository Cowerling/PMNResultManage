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
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.reflect.InvocationTargetException;
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
    public void removeDataRecordAuthorities(DataRecord dataRecord, User associator) {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);
            dataMapper.deleteDataRecordAuthoritiesByAssociatorId(dataRecord.getId(), associator.getId());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void updateDataRecordAuthorities(DataRecord dataRecord, User associator, DataRecordAuthority[] dataRecordAuthorities) {
        removeDataRecordAuthorities(dataRecord, associator);
        saveDataRecordAuthorities(dataRecord, associator, dataRecordAuthorities);
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
                case CP0:
                    return dataMapper.selectCP0DataContentsByDataRecordId(dataRecord.getId());
                case CPI_2D:
                    return dataMapper.selectCPI2DDataContentsByDataRecordId(dataRecord.getId());
                case CPI_3D:
                    return dataMapper.selectCPI3DDataContentsByDataRecordId(dataRecord.getId());
                case CPII:
                    return dataMapper.selectCPIIDataContentsByDataRecordId(dataRecord.getId());
                case CPIII:
                    return dataMapper.selectCPIIIDataContentsByDataRecordId(dataRecord.getId());
                case CPII_LE:
                    return dataMapper.selectCPIILEDataContentsByDataRecordId(dataRecord.getId());
                case TSIT:
                    return dataMapper.selectTSITDataContentsByDataRecordId(dataRecord.getId());
                case EC:
                    return dataMapper.selectECDataContentsByDataRecordId(dataRecord.getId());
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
                case CP0:
                    dataMapper.insertCP0DataContentByDataRecordId(dataRecord.getId(), (CP0DataContent) dataContent);
                    sqlSession.commit();
                    break;
                case CPI_2D:
                    dataMapper.insertCPI2DDataContentByDataRecordId(dataRecord.getId(), (CPI2DDataContent) dataContent);
                    sqlSession.commit();
                    break;
                case CPI_3D:
                    dataMapper.insertCPI3DDataContentByDataRecordId(dataRecord.getId(), (CPI3DDataContent) dataContent);
                    sqlSession.commit();
                    break;
                case CPII:
                    dataMapper.insertCPIIDataContentByDataRecordId(dataRecord.getId(), (CPIIDataContent) dataContent);
                    sqlSession.commit();
                    break;
                case CPIII:
                    dataMapper.insertCPIIIDataContentByDataRecordId(dataRecord.getId(), (CPIIIDataContent) dataContent);
                    sqlSession.commit();
                    break;
                case CPII_LE:
                    dataMapper.insertCPIILEDataContentByDataRecordId(dataRecord.getId(), (CPIILEDataContent) dataContent);
                    sqlSession.commit();
                    break;
                case TSIT:
                    dataMapper.insertTSITDataContentByDataRecordId(dataRecord.getId(), (TSITDataContent) dataContent);
                    sqlSession.commit();
                    break;
                case EC:
                    dataMapper.insertECDataContentByDataRecordId(dataRecord.getId(), (ECDataContent) dataContent);
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
                case CP0:
                    dataMapper.deleteCP0DataContentsByDataRecordId(dataRecord.getId());
                    sqlSession.commit();
                    break;
                case CPI_2D:
                    dataMapper.deleteCPI2DDataContentsByDataRecordId(dataRecord.getId());
                    sqlSession.commit();
                    break;
                case CPI_3D:
                    dataMapper.deleteCPI3DDataContentsByDataRecordId(dataRecord.getId());
                    sqlSession.commit();
                    break;
                case CPII:
                    dataMapper.deleteCPIIDataContentsByDataRecordId(dataRecord.getId());
                    sqlSession.commit();
                    break;
                case CPIII:
                    dataMapper.deleteCPIIIDataContentsByDataRecordId(dataRecord.getId());
                    sqlSession.commit();
                    break;
                case CPII_LE:
                    dataMapper.deleteCPIILEDataContentsByDataRecordId(dataRecord.getId());
                    sqlSession.commit();
                    break;
                case TSIT:
                    dataMapper.deleteTSITDataContentsByDataRecordId(dataRecord.getId());
                    sqlSession.commit();
                    break;
                case EC:
                    dataMapper.deleteECDataContentsByDataRecordId(dataRecord.getId());
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
                case CP0:
                    dataMapper.deleteCP0DataContent(id);
                    sqlSession.commit();
                    break;
                case CPI_2D:
                    dataMapper.deleteCPI2DDataContent(id);
                    sqlSession.commit();
                    break;
                case CPI_3D:
                    dataMapper.deleteCPI3DDataContent(id);
                    sqlSession.commit();
                    break;
                case CPII:
                    dataMapper.deleteCPIIDataContent(id);
                    sqlSession.commit();
                    break;
                case CPIII:
                    dataMapper.deleteCPIIIDataContent(id);
                    sqlSession.commit();
                    break;
                case CPII_LE:
                    dataMapper.deleteCPIILEDataContent(id);
                    sqlSession.commit();
                    break;
                case TSIT:
                    dataMapper.deleteTSITDataContent(id);
                    sqlSession.commit();
                    break;
                case EC:
                    dataMapper.deleteECDataContent(id);
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
                case CP0:
                    dataMapper.updateCP0DataContent((CP0DataContent) dataContent);
                    sqlSession.commit();
                    break;
                case CPI_2D:
                    dataMapper.updateCPI2DDataContent((CPI2DDataContent) dataContent);
                    sqlSession.commit();
                    break;
                case CPI_3D:
                    dataMapper.updateCPI3DDataContent((CPI3DDataContent) dataContent);
                    sqlSession.commit();
                    break;
                case CPII:
                    dataMapper.updateCPIIDataContent((CPIIDataContent) dataContent);
                    sqlSession.commit();
                    break;
                case CPIII:
                    dataMapper.updateCPIIIDataContent((CPIIIDataContent) dataContent);
                    sqlSession.commit();
                    break;
                case CPII_LE:
                    dataMapper.updateCPIILEDataContent((CPIILEDataContent) dataContent);
                    sqlSession.commit();
                    break;
                case TSIT:
                    dataMapper.updateTSITDataContent((TSITDataContent) dataContent);
                    sqlSession.commit();
                    break;
                case EC:
                    dataMapper.updateECDataContent((ECDataContent) dataContent);
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
    public String findDataContentsAsGeoJsonByDataRecord(DataRecord dataRecord) throws NoSuchDataRecordCategoryException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);

            Class<?> dataContentClass = null;
            String dataContentProperties = null;

            switch (dataRecord.getCategory()) {
                case CP0:
                    dataContentClass = CP0DataContent.class;
                    break;
                case CPI_2D:
                    dataContentClass = CPI2DDataContent.class;
                    break;
                case CPI_3D:
                    dataContentClass = CPI3DDataContent.class;
                    break;
                case CPII:
                    dataContentClass = CPIIDataContent.class;
                    break;
                case CPIII:
                    dataContentClass = CPIIIDataContent.class;
                    dataContentProperties = "name, x, y, zenith_height, prism_height";
                    break;
                case CPII_LE:
                    dataContentClass = CPIILEDataContent.class;
                    break;
                case TSIT:
                    dataContentClass = TSITDataContent.class;
                    break;
                default:
                    throw new NoSuchDataRecordCategoryException();
            }

            DataContent dataContent = (DataContent) dataContentClass.getConstructor().newInstance();

            return dataMapper.selectDataContentsAsGeoJson(
                    dataContent.getTableName(),
                    dataContentProperties != null ? dataContentProperties : String.join(", ", dataContent.attributeNames()),
                    dataRecord.getSourceProJ(),
                    dataRecord.getTargetEPSG());
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public Point findDataContentTransformPointByDataRecord(DataRecord dataRecord, DataContent dataContent) {
        SqlSession sqlSession = currentSession();

        try {
            DataMapper dataMapper = sqlSession.getMapper(DataMapper.class);
            return dataMapper.selectTransformPoint(dataContent.getGeometry(), dataRecord.getSourceProJ(), dataRecord.getTargetEPSG());
        } finally {
            sqlSession.close();
        }
    }
}
