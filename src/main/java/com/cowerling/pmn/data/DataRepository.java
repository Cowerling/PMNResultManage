package com.cowerling.pmn.data;

import com.cowerling.pmn.domain.data.DataRecord;
import com.cowerling.pmn.domain.data.DataRecordAuthority;
import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.domain.user.User;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.DataSqlProvider.*;

public interface DataRepository {
    List<DataRecord> findDataRecordsByUser(User user, Map<RecordField, Object> filters, List<Pair<RecordField, Order>> orders, int offset, int limit);
    Long findDataRecordCountByUser(User user, Project project);
    Long findDataRecordCountByUser(User user, Long projectId);
    Long findDataRecordCountByUser(User user);
    List<DataRecordAuthority> findDataRecordAuthorities(DataRecord dataRecord, User associator);
    void saveDataRecord(DataRecord dataRecord);
    void updateDataRecord(DataRecord dataRecord);
}
