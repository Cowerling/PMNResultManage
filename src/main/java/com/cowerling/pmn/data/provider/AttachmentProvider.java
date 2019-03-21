package com.cowerling.pmn.data.provider;

import com.cowerling.pmn.domain.attachment.AttachmentAuthority;
import com.cowerling.pmn.utils.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.jdbc.SQL;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.utils.SQLUtils.INNER_OR_WHERE;

public class AttachmentProvider {
    public enum Field {
        NAME("t_attachment.name"),
        PROJECT("project"),
        UPLOADER("uploader"),
        UPLOAD_TIME("upload_time"),
        START_UPLOAD_TIME("upload_time"),
        END_UPLOAD_TIME("upload_time"),
        REMARK("t_attachment.remark"),
        PROJECT_NAME("t_project.name"),
        UPLOADER_NAME("t_user.name");

        private String sqlExpression;

        Field(String sqlExpression) {
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

    private void FILTER_WHERE(SQL sql, Map<AttachmentProvider.Field, Object> filters) {
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
                    sql.WHERE(String.format("upload_time >= '%s'", DateUtils.format((Date) value)));
                    break;
                case END_UPLOAD_TIME:
                    sql.AND();
                    sql.WHERE(String.format("upload_time <= '%s'", DateUtils.format((Date) value)));
                    break;
                case UPLOADER_NAME:
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

    public String selectAttachmentsByUserId(Map<String, Object> parameters) {
        Long userId = (Long) parameters.get("arg0");

        return new SQL() {
            {
                SELECT("t_attachment.id, t_attachment.name AS name, file, project, uploader, upload_time, t_attachment.remark AS remark");
                FROM("t_attachment");
                LEFT_OUTER_JOIN("t_attachment_auth ON t_attachment.id = t_attachment_auth.attachment");
                LEFT_OUTER_JOIN("t_project ON t_attachment.project = t_project.id");
                LEFT_OUTER_JOIN("t_user ON t_attachment.uploader = t_user.id");
                LEFT_OUTER_JOIN("t_attachment_auth_category ON t_attachment_auth.category = t_attachment_auth_category.id");
                WHERE(String.format("t_attachment_auth_category.category = '%s'", AttachmentAuthority.BASIS.name()));
                WHERE("associator = " + userId);
                if (parameters.get("arg1") != null) {
                    Map<AttachmentProvider.Field, Object> filters = (Map<AttachmentProvider.Field, Object>) parameters.get("arg1");

                    FILTER_WHERE(this, filters);
                }
                if (parameters.get("arg2") != null) {
                    List<Pair<AttachmentProvider.Field, AttachmentProvider.Order>> orders = (List<Pair<AttachmentProvider.Field, AttachmentProvider.Order>>) parameters.get("arg2");

                    orders.forEach(order -> ORDER_BY(order.getKey() + " " + order.getValue()));
                } else {
                    ORDER_BY("t_attachment.upload_time");
                }
            }
        }.toString();
    }

    public String selectAttachmentCountByUserId(Map<String, Object> parameters) {
        Long userId = (Long) parameters.get("arg0");

        return new SQL() {
            {
                SELECT("COUNT(*)");
                FROM("t_attachment");
                LEFT_OUTER_JOIN("t_attachment_auth ON t_attachment.id = t_attachment_auth.attachment");
                LEFT_OUTER_JOIN("t_project ON t_attachment.project = t_project.id");
                LEFT_OUTER_JOIN("t_user ON t_attachment.uploader = t_user.id");
                LEFT_OUTER_JOIN("t_attachment_auth_category ON t_attachment_auth.category = t_attachment_auth_category.id");
                WHERE(String.format("t_attachment_auth_category.category = '%s'", AttachmentAuthority.BASIS.name()));
                WHERE("associator = " + userId);
                if (parameters.get("arg1") != null) {
                    Map<AttachmentProvider.Field, Object> filters = (Map<AttachmentProvider.Field, Object>) parameters.get("arg1");

                    FILTER_WHERE(this, filters);
                }
            }
        }.toString();
    }
}
