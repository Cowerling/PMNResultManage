package com.cowerling.pmn.data.provider;

import com.cowerling.pmn.domain.project.Project;
import com.cowerling.pmn.utils.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.jdbc.SQL;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.cowerling.pmn.utils.SQLUtils.*;

public class ProjectSqlProvider {
    public enum FindMode {
        CREATOR("creator"), MANAGER("manager"), PRINCIPAL("principal"), PARTICIPATOR("participator");

        private String fieldName;

        FindMode(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public String toString() {
            return fieldName;
        }
    }

    public enum Field {
        NAME("name"),
        CREATOR("creator"),
        MANAGER("manager"),
        PRINCIPAL("principal"),
        CATEGORY("t_project_category.category"),
        CREATE_TIME("create_time"),
        START_CREATE_TIME("create_time"),
        END_CREATE_TIME("create_time"),
        REMARK("remark"),
        STATUS("t_project_status.category");

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

    public String selectProjectsById(final Long id) {
        return new SQL() {
            {
                SELECT("t_project.id, name, t_project_category.category AS category, creator, create_time, manager, principal, remark, t_project_status.category AS status");
                FROM("t_project");
                LEFT_OUTER_JOIN("t_project_category ON t_project.category = t_project_category.id");
                LEFT_OUTER_JOIN("t_project_status ON t_project.status = t_project_status.id");
                WHERE("t_project.id = " + id);
            }
        }.toString();
    }

    public String selectProjectsByUserId(Map<String, Object> parameters) {
        Long userId = (Long) parameters.get("arg0");
        FindMode findMode = (FindMode) parameters.get("arg1");

        return new SQL() {
            {
                SELECT("t_project.id, name, t_project_category.category AS category, creator, create_time, manager, principal, remark, t_project_status.category AS status");
                FROM("t_project");
                LEFT_OUTER_JOIN("t_project_category ON t_project.category = t_project_category.id");
                LEFT_OUTER_JOIN("t_project_status ON t_project.status = t_project_status.id");
                if (findMode == FindMode.PARTICIPATOR) {
                    LEFT_OUTER_JOIN("t_project_members ON t_project.id = t_project_members.project");
                }
                if (findMode != FindMode.PARTICIPATOR) {
                    WHERE(findMode + " = " + userId);
                } else {
                    WHERE("t_project_members.member = " + userId);
                }
                if (parameters.get("arg2") != null) {
                    Map<Field, Object> filters = (Map<Field, Object>) parameters.get("arg2");

                    filters.forEach((key, value) -> {
                        switch (key) {
                            case NAME:
                                INNER_OR_WHERE(this, (List<String>) value, "%s LIKE '%%%s%%'", key);
                                break;
                            case CREATOR:
                            case MANAGER:
                            case PRINCIPAL:
                                INNER_OR_WHERE(this, (List<Long>) value, "%s = %d", key);
                                break;
                            case REMARK:
                                AND();
                                WHERE(String.format("%s LIKE '%%%s%%'", key, value));
                                break;
                            case CATEGORY:
                            case STATUS:
                                INNER_OR_WHERE( this, (List<String>) value, "%s = '%s'", key);
                                break;
                            case START_CREATE_TIME:
                                AND();
                                WHERE(String.format("create_time >= '%s'", DateUtils.format((Date) value)));
                                break;
                            case END_CREATE_TIME:
                                AND();
                                WHERE(String.format("create_time <= '%s'", DateUtils.format((Date) value)));
                                break;
                            default:
                                break;
                        }
                    });
                }
                if (parameters.get("arg3") != null) {
                    List<Pair<Field, Order>> orders = (List<Pair<Field, Order>>) parameters.get("arg3");

                    orders.forEach(order -> {
                        ORDER_BY(order.getKey() + " " + order.getValue());
                    });
                } else {
                    ORDER_BY("t_project.id");
                }
            }
        }.toString();
    }

    public String selectProjectCountByUserId(Map<String, Object> parameters) {
        Long userId = (Long) parameters.get("arg0");
        FindMode findMode = (FindMode) parameters.get("arg1");

        return new SQL() {
            {
                if (findMode != FindMode.PARTICIPATOR) {
                    SELECT("COUNT(*) FROM t_project");
                    WHERE(findMode + " = " + userId);
                } else {
                    SELECT("COUNT(*) FROM t_project_members");
                    WHERE("member = " + userId);
                }
            }
        }.toString();
    }

    public String updateProject(final Project project) {
        return new SQL() {
            {
                UPDATE("t_project");
                if (project.getManager() != null) {
                    SET("manager = #{manager.id}");
                }
                if (project.getPrincipal()!= null) {
                    SET("principal = #{principal.id}");
                }
                SET("remark = #{remark}");
                SET("status = (SELECT id FROM t_project_status WHERE category = #{status})");
                WHERE("id = #{id}");
            }
        }.toString();
    }
}
