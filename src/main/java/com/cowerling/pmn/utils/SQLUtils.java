package com.cowerling.pmn.utils;

import org.apache.ibatis.jdbc.SQL;

import java.util.List;

public class SQLUtils {
    public static <T> void INNER_OR_WHERE(SQL sql, List<?> conditions, String format, T key) {
        StringBuilder stringBuilder = new StringBuilder();
        (conditions).forEach(item -> {
            stringBuilder.append(String.format(format, key.toString(), item));
            if (conditions.indexOf(item) != (conditions).size() - 1) {
                stringBuilder.append(" OR ");
            }
        });

        if (stringBuilder.length() != 0) {
            sql.AND();
            sql.WHERE(stringBuilder.toString());
        }
    }
}
