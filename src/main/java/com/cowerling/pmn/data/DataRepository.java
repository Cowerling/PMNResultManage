package com.cowerling.pmn.data;

import com.cowerling.pmn.domain.data.DataContent;
import com.cowerling.pmn.domain.data.DataRecord;
import com.cowerling.pmn.domain.data.DataRecordAuthority;
import com.cowerling.pmn.domain.data.DataRecordCategory;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import com.cowerling.pmn.exception.NoSuchDataRecordCategoryException;
import org.apache.commons.lang3.tuple.Pair;
import org.postgis.Point;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.DataSqlProvider.*;

public interface DataRepository {
    DataRecord findDataRecordsById(Long id);
    List<DataRecord> findDataRecordsByUser(User user, Map<RecordField, Object> filters, List<Pair<RecordField, Order>> orders, int offset, int limit);
    Long findDataRecordCountByUser(User user, Map<RecordField, Object> filters);
    List<DataRecordAuthority> findDataRecordAuthorities(DataRecord dataRecord, User associator);
    void saveDataRecordAuthority(DataRecord dataRecord, User associator, DataRecordAuthority dataRecordAuthority);
    void saveDataRecordAuthorities(DataRecord dataRecord, User associator, DataRecordAuthority[] dataRecordAuthorities);
    void removeDataRecordAuthorities(DataRecord dataRecord);
    void removeDataRecordAuthorities(DataRecord dataRecord, User associator);
    void updateDataRecordAuthorities(DataRecord dataRecord, User associator, DataRecordAuthority[] dataRecordAuthorities);
    void saveDataRecord(DataRecord dataRecord);
    void updateDataRecord(DataRecord dataRecord);
    void removeDataRecordSeparately(DataRecord dataRecord);
    void removeDataRecord(DataRecord dataRecord) throws NoSuchDataRecordCategoryException;

    List<? extends DataContent> findDataContentsByDataRecord(DataRecord dataRecord) throws NoSuchDataRecordCategoryException;
    void saveDataContentByDataRecord(DataRecord dataRecord, DataContent dataContent) throws NoSuchDataRecordCategoryException;
    void saveDataContentsByDataRecord(DataRecord dataRecord, List<? extends DataContent> dataContents) throws NoSuchDataRecordCategoryException;
    void removeDataContentsByDataRecord(DataRecord dataRecord) throws NoSuchDataRecordCategoryException;
    void removeDataContentById(DataRecordCategory dataRecordCategory, Long id) throws NoSuchDataRecordCategoryException;
    void updateDataContent(DataRecordCategory dataRecordCategory, DataContent dataContent) throws NoSuchDataRecordCategoryException;
    String findDataContentsAsGeoJsonByDataRecord(DataRecord dataRecord) throws NoSuchDataRecordCategoryException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException;
    Point findDataContentTransformPointByDataRecord(DataRecord dataRecord, DataContent dataContent) throws NoSuchDataRecordCategoryException;
}
