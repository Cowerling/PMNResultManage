package com.cowerling.pmn.data.provider;

import com.cowerling.pmn.domain.user.UserRole;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class UserSqlProvider {
    public enum Field {
        ROLE("t_user_role.category");

        private String sqlExpression;

        Field(String sqlExpression) {
            this.sqlExpression = sqlExpression;
        }

        @Override
        public String toString() {
            return sqlExpression;
        }
    }

    public String selectUserCountByDepartmentId(Map<String, Object> parameters) {
        Long departmentId = (Long) parameters.get("arg0");

        return new SQL() {
            {
                SELECT("COUNT(*)");
                FROM("t_user");
                LEFT_OUTER_JOIN("t_user_role ON t_user.role = t_user_role.id");
                WHERE("department = " + departmentId);
                if (parameters.get("arg1") != null) {
                    Map<Field, Object> filters = (Map<Field, Object>) parameters.get("arg1");

                    filters.forEach((key, value) -> {
                        switch (key) {
                            case ROLE:
                                WHERE(String.format("%s = '%s'", key, ((UserRole) value).name()));
                                break;
                            default:
                                break;
                        }
                    });
                }
            }
        }.toString();
    }

    public String selectUsersByDepartmentId(Map<String, Object> parameters) {
        Long departmentId = (Long) parameters.get("arg0");

        return new SQL() {
            {
                SELECT("t_user.id, name, password, email, alias, t_user_gender.category AS gender, birthday, phone, register_date, photo, t_user_role.category AS role, department");
                FROM("t_user");
                LEFT_OUTER_JOIN("t_user_gender ON t_user.gender = t_user_gender.id");
                LEFT_OUTER_JOIN("t_user_role ON t_user.role = t_user_role.id");
                LEFT_OUTER_JOIN("t_project_members ON t_user.id = t_project_members.member");
                WHERE("department = " + departmentId);
                if (parameters.get("arg1") != null) {
                    Map<Field, Object> filters = (Map<Field, Object>) parameters.get("arg1");

                    filters.forEach((key, value) -> {
                        switch (key) {
                            case ROLE:
                                WHERE(String.format("%s = '%s'", key, ((UserRole) value).name()));
                                break;
                            default:
                                break;
                        }
                    });
                }
            }
        }.toString();
    }
}
