package com.cowerling.pmn.utils;

import com.cowerling.pmn.data.provider.DataSqlProvider;
import com.cowerling.pmn.data.provider.ProjectSqlProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

public class SQLUtils {
    private static void INNER_OR_WHERE(SQL sql, List<?> conditions, String format, String key) {
        StringBuilder stringBuilder = new StringBuilder();
        (conditions).forEach(item -> {
            stringBuilder.append(String.format(format, key, item));
            if (conditions.indexOf(item) != (conditions).size() - 1) {
                stringBuilder.append(" OR ");
            }
        });

        if (stringBuilder.length() != 0) {
            sql.AND();
            sql.WHERE(stringBuilder.toString());
        }
    }

    public static void INNER_OR_WHERE(SQL sql, List<?> conditions, String format, DataSqlProvider.RecordField key) {
        INNER_OR_WHERE(sql, conditions, format, key.toString());
    }

    public static void INNER_OR_WHERE(SQL sql, List<?> conditions, String format, ProjectSqlProvider.Field key) {
        INNER_OR_WHERE(sql, conditions, format, key.toString());
    }
}
