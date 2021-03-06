package com.cowerling.pmn.data.provider;

import com.cowerling.pmn.domain.data.DataRecordAuthority;
import com.cowerling.pmn.utils.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.jdbc.SQL;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.utils.SQLUtils.*;

public class DataSqlProvider {
    public enum RecordField {
        NAME("t_data_record.name"),
        PROJECT("project"),
        UPLOADER("uploader"),
        UPLOAD_TIME("upload_time"),
        START_UPLOAD_TIME("upload_time"),
        END_UPLOAD_TIME("upload_time"),
        STATUS("t_data_record_status.category"),
        REMARK("t_data_record.remark"),
        PROJECT_NAME("t_project.name"),
        UPLOADER_NAME("t_user.name");

        private String sqlExpression;

        RecordField(String sqlExpression) {
            this.sqlExpression = sqlExpression;
        }

        @Override
        public String toString() {
            return sqlExpression;
        }
    }

    public enum Order {
        ASCENDING("ASC"), DESCENDING("DESC");

        private String sqlExpression;

        Order(String sqlExpression) {
            this.sqlExpression = sqlExpression;
        }

        @Override
        public String toString() {
            return sqlExpression;
        }
    }

    private void FILTER_WHERE(SQL sql, Map<RecordField, Object> filters) {
        filters.forEach((key, value) -> {
            switch (key) {
                case NAME:
                case PROJECT_NAME:
                    INNER_OR_WHERE(sql, (List<String>) value, "%s LIKE '%%%s%%'", key);
                    break;
                case PROJECT:
                case UPLOADER:
                    INNER_OR_WHERE(sql, (List<Long>) value, "%s = %d", key);
                    break;
                case START_UPLOAD_TIME:
                    sql.AND();
                    sql.WHERE(String.format("create_time >= '%s'", DateUtils.format((Date) value)));
                    break;
                case END_UPLOAD_TIME:
                    sql.AND();
                    sql.WHERE(String.format("create_time <= '%s'", DateUtils.format((Date) value)));
                    break;
                case UPLOADER_NAME:
                case STATUS:
                    INNER_OR_WHERE(sql, (List<String>) value, "%s = '%s'", key);
                    break;
                case REMARK:
                    sql.AND();
                    sql.WHERE(String.format("%s LIKE '%%%s%%'", key, value));
                    break;
                default:
                    break;
            }
        });
    }

    public String selectDataRecordsByUserId(Map<String, Object> parameters) {
        Long userId = (Long) parameters.get("arg0");

        return new SQL() {
            {
                SELECT("t_data_record.id, t_data_record.name AS name, file, project, uploader, upload_time, t_data_record_status.category AS status, t_data_record_category.category AS category, t_data_record.remark AS remark, source_proj");
                FROM("t_data_record");
                LEFT_OUTER_JOIN("t_data_record_status ON t_data_record.status = t_data_record_status.id");
                LEFT_OUTER_JOIN("t_data_record_auth ON t_data_record.id = t_data_record_auth.record");
                LEFT_OUTER_JOIN("t_data_record_category ON t_data_record.category = t_data_record_category.id");
                LEFT_OUTER_JOIN("t_project ON t_data_record.project = t_project.id");
                LEFT_OUTER_JOIN("t_user ON t_data_record.uploader = t_user.id");
                LEFT_OUTER_JOIN("t_data_record_auth_category ON t_data_record_auth.category = t_data_record_auth_category.id");
                WHERE(String.format("t_data_record_auth_category.category = '%s'", DataRecordAuthority.BASIS.name()));
                WHERE("associator = " + userId);
                if (parameters.get("arg1") != null) {
                    Map<RecordField, Object> filters = (Map<RecordField, Object>) parameters.get("arg1");

                    FILTER_WHERE(this, filters);
                }
                if (parameters.get("arg2") != null) {
                    List<Pair<RecordField, Order>> orders = (List<Pair<RecordField, Order>>) parameters.get("arg2");

                    orders.forEach(order -> ORDER_BY(order.getKey() + " " + order.getValue()));
                } else {
                    ORDER_BY("t_data_record.upload_time");
                }
            }
        }.toString();
    }

    public String selectDataRecordCountByUserId(Map<String, Object> parameters) {
        Long userId = (Long) parameters.get("arg0");

        return new SQL() {
            {
                SELECT("COUNT(*)");
                FROM("t_data_record");
                LEFT_OUTER_JOIN("t_data_record_status ON t_data_record.status = t_data_record_status.id");
                LEFT_OUTER_JOIN("t_data_record_auth ON t_data_record.id = t_data_record_auth.record");
                LEFT_OUTER_JOIN("t_data_record_category ON t_data_record.category = t_data_record_category.id");
                LEFT_OUTER_JOIN("t_project ON t_data_record.project = t_project.id");
                LEFT_OUTER_JOIN("t_user ON t_data_record.uploader = t_user.id");
                LEFT_OUTER_JOIN("t_data_record_auth_category ON t_data_record_auth.category = t_data_record_auth_category.id");
                WHERE(String.format("t_data_record_auth_category.category = '%s'", DataRecordAuthority.BASIS.name()));
                WHERE("associator = " + userId);
                if (parameters.get("arg1") != null) {
                    Map<RecordField, Object> filters = (Map<RecordField, Object>) parameters.get("arg1");

                    FILTER_WHERE(this, filters);
                }
            }
        }.toString();
    }

    public String selectDataContentsAsGeoJsonByDataRecordId(Map<String, Object> parameters) {
        Long dataRecordId = (Long) parameters.get("arg0");
        String dataContentTableName = (String) parameters.get("arg1");
        String dataContentProperties = (String) parameters.get("arg2");
        String sourceProJ = (String) parameters.get("arg3");
        Integer targetEPSG = (Integer) parameters.get("arg4");

        String c = "SELECT row_to_json(fc) " +
                "FROM (" +
                "SELECT 'FeatureCollection' AS type, array_to_json(array_agg(f)) AS features " +
                "FROM (" +
                "SELECT 'Feature' As type, ST_AsGeoJSON(ST_Transform(geometry, '" + sourceProJ + "', " + targetEPSG + "))::json AS geometry, row_to_json((SELECT l FROM (SELECT " + dataContentProperties + ") AS l)) AS properties " +
                "FROM " + dataContentTableName + " " +
                "AS lg WHERE data_record = " + dataRecordId + ") " +
                "AS f) " +
                "AS fc";

        return "SELECT row_to_json(fc) " +
                "FROM (" +
                "SELECT 'FeatureCollection' AS type, array_to_json(array_agg(f)) AS features " +
                "FROM (" +
                "SELECT 'Feature' As type, ST_AsGeoJSON(ST_Transform(geometry, '" + sourceProJ + "', " + targetEPSG + "))::json AS geometry, row_to_json((SELECT l FROM (SELECT " + dataContentProperties + ") AS l)) AS properties " +
                "FROM " + dataContentTableName + " " +
                "AS lg WHERE data_record = " + dataRecordId + ") " +
                "AS f) " +
                "AS fc";
    }
}
