package com.cowerling.pmn.data.mapper;

import com.cowerling.pmn.data.provider.DataSqlProvider;
import com.cowerling.pmn.domain.data.DataRecord;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.DataSqlProvider.*;

public interface DataMapper {
    @SelectProvider(type = DataSqlProvider.class, method = "selectDataRecords")
    @ResultMap("com.cowerling.pmn.data.mapper.DataMapper.dataRecordResult")
    List<DataRecord> selectDataRecords(Map<RecordField, Object> filters, List<Pair<RecordField, Order>> orders, RowBounds rowBounds);

    @SelectProvider(type = DataSqlProvider.class, method = "selectDataRecordCount")
    Long selectDataRecordCount(Map<RecordField, Object> filters);

    @Insert("INSERT INTO t_data_record(name, file, project, uploader, upload_time, status, remark) VALUES(#{name}, #{file}, #{project.id}, #{uploader.id}, #{uploadTime}, (SELECT id FROM t_data_record_status WHERE category = #{status}), #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertDataRecord(DataRecord dataRecord);

    @Update("UPDATE t_data_record " +
            "SET status = (SELECT id FROM t_data_record_status WHERE category = #{status}), remark = #{remark} " +
            "WHERE id = #{id}")
    void updateDataRecord(DataRecord dataRecord);
}
