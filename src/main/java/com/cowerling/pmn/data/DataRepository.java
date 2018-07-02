package com.cowerling.pmn.data;

import com.cowerling.pmn.domain.data.DataRecord;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.DataSqlProvider.*;

public interface DataRepository {
    List<DataRecord> findDataRecords(Map<RecordField, Object> filters, List<Pair<RecordField, Order>> orders, int offset, int limit);
    Long findDataRecordCount(Map<RecordField, Object> filters);
    void saveDataRecord(DataRecord dataRecord);
    void updateDataRecord(DataRecord dataRecord);
}
