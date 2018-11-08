package com.cowerling.pmn.data.mapper;

import com.cowerling.pmn.data.provider.DataSqlProvider;
import com.cowerling.pmn.domain.data.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;
import org.postgis.Geometry;
import org.postgis.Point;

import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.data.provider.DataSqlProvider.*;

public interface DataMapper {
    @Select("SELECT t_data_record.id, t_data_record.name AS name, file, project, uploader, upload_time, t_data_record_status.category AS status, t_data_record_category.category AS category, t_data_record.remark AS remark, source_proj " +
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

    @Insert("INSERT INTO t_data_record(name, file, project, uploader, upload_time, status, category, remark, source_proj) " +
            "VALUES(#{name}, #{file}, #{project.id}, #{uploader.id}, #{uploadTime}, (SELECT id FROM t_data_record_status WHERE category = #{status}), (SELECT id FROM t_data_record_category WHERE category = #{category}), #{remark}, #{sourceProJ})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertDataRecord(DataRecord dataRecord);

    @Update("UPDATE t_data_record " +
            "SET status = (SELECT id FROM t_data_record_status WHERE category = #{status}), remark = #{remark} " +
            "WHERE id = #{id}")
    void updateDataRecord(DataRecord dataRecord);

    @Delete("DELETE FROM t_data_record " +
            "WHERE id = #{id}")
    void deleteDataRecord(DataRecord dataRecord);

    @SelectProvider(type = DataSqlProvider.class, method = "selectDataContentsAsGeoJson")
    String selectDataContentsAsGeoJson(String dataContentTableName, String dataContentProperties, String sourceProJ, Integer targetEPSG);

    @Select("SELECT ST_Transform(#{geometry}, #{sourceProJ}, #{targetEPSG})")
    Point selectTransformPoint(@Param("geometry") Geometry geometry, @Param("sourceProJ") String sourceProJ, @Param("targetEPSG") Integer targetEPSG);

    /* CP0DataContent */
    @Select("SELECT id, name, x, y, z, mx, my, mz " +
            "FROM t_data_content_cpo " +
            "WHERE data_record = #{dataRecordId} " +
            "ORDER BY id ASC")
    @ResultMap("com.cowerling.pmn.data.mapper.DataMapper.cp0DataContentResult")
    List<CP0DataContent> selectCP0DataContentsByDataRecordId(Long dataRecordId);

    @Insert("INSERT INTO t_data_content_cpo(data_record, name, x, y, z, mx, my, mz, geometry) " +
            "VALUES(#{dataRecordId}, #{cp0DataContent.name}, #{cp0DataContent.x}, #{cp0DataContent.y}, #{cp0DataContent.z}, #{cp0DataContent.mx}, #{cp0DataContent.my}, #{cp0DataContent.mz}, #{cp0DataContent.geometry})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCP0DataContentByDataRecordId(@Param("dataRecordId") Long dataRecordId, @Param("cp0DataContent") CP0DataContent cp0DataContent);

    @Delete("DELETE FROM t_data_content_cpo " +
            "WHERE data_record = #{dataRecordId}")
    void deleteCP0DataContentsByDataRecordId(Long dataRecordId);

    @Delete("DELETE FROM t_data_content_cpo " +
            "WHERE id = #{id}")
    void deleteCP0DataContent(@Param("id") Long id);

    @Update("UPDATE t_data_content_cpo " +
            "SET name = #{cp0DataContent.name}, x = #{cp0DataContent.x}, y = #{cp0DataContent.y}, z = #{cp0DataContent.z}, mx = #{cp0DataContent.mx}, my = #{cp0DataContent.my}, mz = #{cp0DataContent.mz}, geometry = #{cp0DataContent.geometry} " +
            "WHERE id = #{cp0DataContent.id}")
    void updateCP0DataContent(@Param("cp0DataContent") CP0DataContent cp0DataContent);

    /* CPI2DDataContent */
    @Select("SELECT id, name, x, y, mx, my, mp " +
            "FROM t_data_content_cpi_two_dimension " +
            "WHERE data_record = #{dataRecordId} " +
            "ORDER BY id ASC")
    @ResultMap("com.cowerling.pmn.data.mapper.DataMapper.cpi2dDataContentResult")
    List<CPI2DDataContent> selectCPI2DDataContentsByDataRecordId(Long dataRecordId);

    @Insert("INSERT INTO t_data_content_cpi_two_dimension(data_record, name, x, y, mx, my, mp, geometry) " +
            "VALUES(#{dataRecordId}, #{cpi2dDataContent.name}, #{cpi2dDataContent.x}, #{cpi2dDataContent.y}, #{cpi2dDataContent.mx}, #{cpi2dDataContent.my}, #{cpi2dDataContent.mp}, #{cpi2dDataContent.geometry})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCPI2DDataContentByDataRecordId(@Param("dataRecordId") Long dataRecordId, @Param("cpi2dDataContent") CPI2DDataContent cpi2dDataContent);

    @Delete("DELETE FROM t_data_content_cpi_two_dimension " +
            "WHERE data_record = #{dataRecordId}")
    void deleteCPI2DDataContentsByDataRecordId(Long dataRecordId);

    @Delete("DELETE FROM t_data_content_cpi_two_dimension " +
            "WHERE id = #{id}")
    void deleteCPI2DDataContent(@Param("id") Long id);

    @Update("UPDATE t_data_content_cpi_two_dimension " +
            "SET name = #{cpi2dDataContent.name}, x = #{cpi2dDataContent.x}, y = #{cpi2dDataContent.y}, mx = #{cpi2dDataContent.mx}, my = #{cpi2dDataContent.my}, mp = #{cpi2dDataContent.mp}, geometry = #{cpi2dDataContent.geometry} " +
            "WHERE id = #{cpi2dDataContent.id}")
    void updateCPI2DDataContent(@Param("cpi2dDataContent") CPI2DDataContent cpi2dDataContent);

    /* CPI3DDataContent */
    @Select("SELECT id, name, x, y, z, mx, my, mz " +
            "FROM t_data_content_cpi_three_dimension " +
            "WHERE data_record = #{dataRecordId} " +
            "ORDER BY id ASC")
    @ResultMap("com.cowerling.pmn.data.mapper.DataMapper.cpi3dDataContentResult")
    List<CPI3DDataContent> selectCPI3DDataContentsByDataRecordId(Long dataRecordId);

    @Insert("INSERT INTO t_data_content_cpi_three_dimension(data_record, name, x, y, z, mx, my, mz, geometry) " +
            "VALUES(#{dataRecordId}, #{cpi3dDataContent.name}, #{cpi3dDataContent.x}, #{cpi3dDataContent.y}, #{cpi3dDataContent.z}, #{cpi3dDataContent.mx}, #{cpi3dDataContent.my}, #{cpi3dDataContent.mz}, #{cpi3dDataContent.geometry})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCPI3DDataContentByDataRecordId(@Param("dataRecordId") Long dataRecordId, @Param("cpi3dDataContent") CPI3DDataContent cpi3dDataContent);

    @Delete("DELETE FROM t_data_content_cpi_three_dimension " +
            "WHERE data_record = #{dataRecordId}")
    void deleteCPI3DDataContentsByDataRecordId(Long dataRecordId);

    @Delete("DELETE FROM t_data_content_cpi_three_dimension " +
            "WHERE id = #{id}")
    void deleteCPI3DDataContent(@Param("id") Long id);

    @Update("UPDATE t_data_content_cpi_three_dimension " +
            "SET name = #{cpi3dDataContent.name}, x = #{cpi3dDataContent.x}, y = #{cpi3dDataContent.y}, z = #{cpi3dDataContent.z}, mx = #{cpi3dDataContent.mx}, my = #{cpi3dDataContent.my}, mz = #{cpi3dDataContent.mz}, geometry = #{cpi3dDataContent.geometry} " +
            "WHERE id = #{cpi3dDataContent.id}")
    void updateCPI3DDataContent(@Param("cpi3dDataContent") CPI3DDataContent cpi3dDataContent);

    /* CPIIDataContent */
    @Select("SELECT id, name, x, y, mx, my, mp " +
            "FROM t_data_content_cpii " +
            "WHERE data_record = #{dataRecordId} " +
            "ORDER BY id ASC")
    @ResultMap("com.cowerling.pmn.data.mapper.DataMapper.cpiiDataContentResult")
    List<CPIIDataContent> selectCPIIDataContentsByDataRecordId(Long dataRecordId);

    @Insert("INSERT INTO t_data_content_cpii(data_record, name, x, y, mx, my, mp, geometry) " +
            "VALUES(#{dataRecordId}, #{cpiiDataContent.name}, #{cpiiDataContent.x}, #{cpiiDataContent.y}, #{cpiiDataContent.mx}, #{cpiiDataContent.my}, #{cpiiDataContent.mp}, #{cpiiDataContent.geometry})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCPIIDataContentByDataRecordId(@Param("dataRecordId") Long dataRecordId, @Param("cpiiDataContent") CPIIDataContent cpiiDataContent);

    @Delete("DELETE FROM t_data_content_cpii " +
            "WHERE data_record = #{dataRecordId}")
    void deleteCPIIDataContentsByDataRecordId(Long dataRecordId);

    @Delete("DELETE FROM t_data_content_cpii " +
            "WHERE id = #{id}")
    void deleteCPIIDataContent(@Param("id") Long id);

    @Update("UPDATE t_data_content_cpii " +
            "SET name = #{cpiiDataContent.name}, x = #{cpiiDataContent.x}, y = #{cpiiDataContent.y}, mx = #{cpiiDataContent.mx}, my = #{cpiiDataContent.my}, mp = #{cpiiDataContent.mp}, geometry = #{cpiiDataContent.geometry} " +
            "WHERE id = #{cpiiDataContent.id}")
    void updateCPIIDataContent(@Param("cpiiDataContent") CPIIDataContent cpiiDataContent);

    /* CPIIIDataContent */
    @Select("SELECT id, name, x, y, zenith_height, prism_height " +
            "FROM t_data_content_cpiii " +
            "WHERE data_record = #{dataRecordId} " +
            "ORDER BY id ASC")
    @ResultMap("com.cowerling.pmn.data.mapper.DataMapper.cpiiiDataContentResult")
    List<CPIIIDataContent> selectCPIIIDataContentsByDataRecordId(Long dataRecordId);

    @Insert("INSERT INTO t_data_content_cpiii(data_record, name, x, y, zenith_height, prism_height, geometry) " +
            "VALUES(#{dataRecordId}, #{cpiiiDataContent.name}, #{cpiiiDataContent.x}, #{cpiiiDataContent.y}, #{cpiiiDataContent.zenithHeight}, #{cpiiiDataContent.prismHeight}, #{cpiiiDataContent.geometry})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCPIIIDataContentByDataRecordId(@Param("dataRecordId") Long dataRecordId, @Param("cpiiiDataContent") CPIIIDataContent cpiiiDataContent);

    @Delete("DELETE FROM t_data_content_cpiii " +
            "WHERE data_record = #{dataRecordId}")
    void deleteCPIIIDataContentsByDataRecordId(Long dataRecordId);

    @Delete("DELETE FROM t_data_content_cpiii " +
            "WHERE id = #{id}")
    void deleteCPIIIDataContent(@Param("id") Long id);

    @Update("UPDATE t_data_content_cpiii " +
            "SET name = #{cpiiiDataContent.name}, x = #{cpiiiDataContent.x}, y = #{cpiiiDataContent.y}, zenith_height = #{cpiiiDataContent.zenithHeight}, prism_height = #{cpiiiDataContent.prismHeight}, geometry = #{cpiiiDataContent.geometry} " +
            "WHERE id = #{cpiiiDataContent.id}")
    void updateCPIIIDataContent(@Param("cpiiiDataContent") CPIIIDataContent cpiiiDataContent);

    /* CPIILEDataContent */
    @Select("SELECT id, name, x, y, mx, my, mp " +
            "FROM t_data_content_cpii_le " +
            "WHERE data_record = #{dataRecordId} " +
            "ORDER BY id ASC")
    @ResultMap("com.cowerling.pmn.data.mapper.DataMapper.cpiileDataContentResult")
    List<CPIILEDataContent> selectCPIILEDataContentsByDataRecordId(Long dataRecordId);

    @Insert("INSERT INTO t_data_content_cpii_le(data_record, name, x, y, mx, my, mp, geometry) " +
            "VALUES(#{dataRecordId}, #{cpiileDataContent.name}, #{cpiileDataContent.x}, #{cpiileDataContent.y}, #{cpiileDataContent.mx}, #{cpiileDataContent.my}, #{cpiileDataContent.mp}, #{cpiileDataContent.geometry})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCPIILEDataContentByDataRecordId(@Param("dataRecordId") Long dataRecordId, @Param("cpiileDataContent") CPIILEDataContent cpiileDataContent);

    @Delete("DELETE FROM t_data_content_cpii_le " +
            "WHERE data_record = #{dataRecordId}")
    void deleteCPIILEDataContentsByDataRecordId(Long dataRecordId);

    @Delete("DELETE FROM t_data_content_cpii_le " +
            "WHERE id = #{id}")
    void deleteCPIILEDataContent(@Param("id") Long id);

    @Update("UPDATE t_data_content_cpii_le " +
            "SET name = #{cpiileDataContent.name}, x = #{cpiileDataContent.x}, y = #{cpiileDataContent.y}, mx = #{cpiileDataContent.mx}, my = #{cpiileDataContent.my}, mp = #{cpiileDataContent.mp}, geometry = #{cpiileDataContent.geometry} " +
            "WHERE id = #{cpiileDataContent.id}")
    void updateCPIILEDataContent(@Param("cpiileDataContent") CPIILEDataContent cpiileDataContent);

    /* TSITDataContent */
    @Select("SELECT id, name, x, y, mx, my " +
            "FROM t_data_content_tsit " +
            "WHERE data_record = #{dataRecordId} " +
            "ORDER BY id ASC")
    @ResultMap("com.cowerling.pmn.data.mapper.DataMapper.tsitDataContentResult")
    List<TSITDataContent> selectTSITDataContentsByDataRecordId(Long dataRecordId);

    @Insert("INSERT INTO t_data_content_tsit(data_record, name, x, y, mx, my, geometry) " +
            "VALUES(#{dataRecordId}, #{tsitDataContent.name}, #{tsitDataContent.x}, #{tsitDataContent.y}, #{tsitDataContent.mx}, #{tsitDataContent.my}, #{tsitDataContent.geometry})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertTSITDataContentByDataRecordId(@Param("dataRecordId") Long dataRecordId, @Param("tsitDataContent") TSITDataContent tsitDataContent);

    @Delete("DELETE FROM t_data_content_tsit " +
            "WHERE data_record = #{dataRecordId}")
    void deleteTSITDataContentsByDataRecordId(Long dataRecordId);

    @Delete("DELETE FROM t_data_content_tsit " +
            "WHERE id = #{id}")
    void deleteTSITDataContent(@Param("id") Long id);

    @Update("UPDATE t_data_content_tsit " +
            "SET name = #{tsitDataContent.name}, x = #{tsitDataContent.x}, y = #{tsitDataContent.y}, mx = #{tsitDataContent.mx}, my = #{tsitDataContent.my}, geometry = #{tsitDataContent.geometry} " +
            "WHERE id = #{tsitDataContent.id}")
    void updateTSITDataContent(@Param("tsitDataContent") TSITDataContent tsitDataContent);

    /* ECDataContent */
    @Select("SELECT id, name, mean_deviation, square_error " +
            "FROM t_data_content_ec " +
            "WHERE data_record = #{dataRecordId} " +
            "ORDER BY id ASC")
    @ResultMap("com.cowerling.pmn.data.mapper.DataMapper.ecDataContentResult")
    List<ECDataContent> selectECDataContentsByDataRecordId(Long dataRecordId);

    @Insert("INSERT INTO t_data_content_ec(data_record, name, mean_deviation, square_error) " +
            "VALUES(#{dataRecordId}, #{ecDataContent.name}, #{ecDataContent.meanDeviation}, #{ecDataContent.squareError})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertECDataContentByDataRecordId(@Param("dataRecordId") Long dataRecordId, @Param("ecDataContent") ECDataContent ecDataContent);

    @Delete("DELETE FROM t_data_content_ec " +
            "WHERE data_record = #{dataRecordId}")
    void deleteECDataContentsByDataRecordId(Long dataRecordId);

    @Delete("DELETE FROM t_data_content_ec " +
            "WHERE id = #{id}")
    void deleteECDataContent(@Param("id") Long id);

    @Update("UPDATE t_data_content_ec " +
            "SET name = #{ecDataContent.name}, mean_deviation = #{ecDataContent.meanDeviation}, square_error = #{ecDataContent.squareError} " +
            "WHERE id = #{ecDataContent.id}")
    void updateECDataContent(@Param("ecDataContent") ECDataContent ecDataContent);
}
