package com.cowerling.pmn.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final int MILLISECOND_TO_SECOND = 1000;
    private static final int SECOND_TO_MINUTE = 60;
    private static final int MINUTE_TO_HOUR = 60;
    private static final int HOUR_TO_DAY = 24;
    private final static String DATE_DAY = "天";
    private final static String DATE_HOUR = "小时";
    private final static String DATE_MINUTE = "分";
    private final static String DATE_SECOND = "秒";

    private static SimpleDateFormat simpleDateFormat;

    static {
        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    }

    public static String format(Date date) {
        return simpleDateFormat.format(date);
    }

    public static Date parse(String text) throws ParseException {
        return simpleDateFormat.parse(text);
    }

    public static String beforePresent(Date date) {
        long interval = (new Date().getTime() - date.getTime()) / MILLISECOND_TO_SECOND;
        int day = (int) (interval / SECOND_TO_MINUTE / MINUTE_TO_HOUR / HOUR_TO_DAY);
        interval = interval - day * HOUR_TO_DAY * MINUTE_TO_HOUR * SECOND_TO_MINUTE;
        int hour = (int) (interval / SECOND_TO_MINUTE / MINUTE_TO_HOUR);

        return day + DATE_DAY + hour + DATE_HOUR;
    }
}
