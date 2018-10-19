package com.cowerling.pmn.data.mapper;

import com.cowerling.pmn.data.provider.DataSqlProvider;
import com.cowerling.pmn.domain.data.CPIBaseDataContent;
import com.cowerling.pmn.domain.data.DataRecord;
import com.cowerling.pmn.domain.data.DataRecordAuthority;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.DataSqlProvider.*;

public interface DataMapper {
    @Select("SELECT t_data_record.id, t_data_record.name AS name, file, project, uploader, upload_time, t_data_record_status.category AS status, t_data_record_category.category AS category, t_data_record.remark AS remark " +
            "FROM t_data_record " +
            "LEFT OUTER JOIN t_data_record_status ON t_data_record.status = t_data_record_status.id " +
            "LEFT OUTER JOIN t_data_record_category ON t_data_record.category = t_data_record_category.id " +
            "WHERE t_data_record.id = #{id}")
    @ResultMap("com.cowerling.pmn.data.mapper.DataMapper.dataRecordResult")
    DataRecord selectDataRecordsById(Long id);

    @SelectProvider(type = DataSqlProvider.class, method = "selectDataRecordsByUserId")
    @ResultMap("com.cowerling.pmn.data.mapper.DataMapper.dataRecordResult")
    List<DataRecord> selectDataRecordsByUserId(Long userId, Map<RecordField, Object> filters, List<Pair<RecordField, Order>> orders, RowBounds rowBounds);

    @SelectProvider(type = DataSqlProvider.class, method = "selectDataRecordCountByUserId")
    Long selectDataRecordCountByUserId(Long userId, Map<RecordField, Object> filters);

    @Select("SELECT t_data_record_auth_category.category AS category " +
            "FROM t_data_record_auth " +
            "LEFT OUTER JOIN t_data_record_auth_category ON t_data_record_auth.category = t_data_record_auth_category.id " +
            "WHERE record = #{dataRecordId} AND associator = #{associatorId}")
    List<DataRecordAuthority> selectDataRecordAuthoritiesByDataRecordId(@Param("dataRecordId") Long dataRecordId, @Param("associatorId") Long associatorId);

    @Insert("INSERT INTO t_data_record_auth(record, associator, category) " +
            "VALUES(#{dataRecordId}, #{associatorId}, (SELECT id FROM t_data_record_auth_category WHERE category = #{dataRecordAuthority}))")
    void insertDataRecordAuthorityByDataRecordId(@Param("dataRecordId") Long dataRecordId, @Param("associatorId") Long associatorId, @Param("dataRecordAuthority") DataRecordAuthority dataRecordAuthority);

    @Delete("DELETE FROM t_data_record_auth " +
            "WHERE record = #{dataRecordId}")
    void deleteDataRecordAuthoritiesByDataRecordId(Long dataRecordId);

    @Delete("DELETE FROM t_data_record_auth " +
            "WHERE record = #{dataRecordId} AND associator = #{associatorId}")
    void deleteDataRecordAuthoritiesByAssociatorId(@Param("dataRecordId") Long dataRecordId, @Param("associatorId") Long associatorId);

    @Insert("INSERT INTO t_data_record(name, file, project, uploader, upload_time, status, category, remark) " +
            "VALUES(#{name}, #{file}, #{project.id}, #{uploader.id}, #{uploadTime}, (SELECT id FROM t_data_record_status WHERE category = #{status}), (SELECT id FROM t_data_record_category WHERE category = #{category}), #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertDataRecord(DataRecord dataRecord);

    @Update("UPDATE t_data_record " +
            "SET status = (SELECT id FROM t_data_record_status WHERE category = #{status}), remark = #{remark} " +
            "WHERE id = #{id}")
    void updateDataRecord(DataRecord dataRecord);

    @Delete("DELETE FROM t_data_record " +
            "WHERE id = #{id}")
    void deleteDataRecord(DataRecord dataRecord);

    @Select("SELECT id, x, y, h " +
            "FROM t_data_content_cpi_base " +
            "WHERE data_record = #{dataRecordId} " +
            "ORDER BY id ASC")
    @ResultMap("com.cowerling.pmn.data.mapper.DataMapper.cpiBaseDataContentResult")
    List<CPIBaseDataContent> selectCPIBaseDataContentsByDataRecordId(Long dataRecordId);

    @Insert("INSERT INTO t_data_content_cpi_base(data_record, x, y, h) " +
            "VALUES(#{dataRecordId}, #{cpiBaseDataContent.x}, #{cpiBaseDataContent.y}, #{cpiBaseDataContent.h})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCPIBaseDataContentByDataRecordId(@Param("dataRecordId") Long dataRecordId, @Param("cpiBaseDataContent") CPIBaseDataContent cpiBaseDataContent);

    @Delete("DELETE FROM t_data_content_cpi_base " +
            "WHERE data_record = #{dataRecordId}")
    void deleteCPIBaseDataContentsByDataRecordId(Long dataRecordId);

    @Delete("DELETE FROM t_data_content_cpi_base " +
            "WHERE id = #{id}")
    void deleteCPIBaseDataContent(@Param("id") Long id);

    @Update("UPDATE t_data_content_cpi_base " +
            "SET x = #{cpiBaseDataContent.x}, y = #{cpiBaseDataContent.y}, h = #{cpiBaseDataContent.h} " +
            "WHERE id = #{cpiBaseDataContent.id}")
    void updateCPIBaseDataContent(@Param("cpiBaseDataContent") CPIBaseDataContent cpiBaseDataContent);
}
