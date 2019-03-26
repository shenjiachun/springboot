package com.lbc.demo.Sharding.constants;


import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;


public class DateCtrlUtil {
    public static final FastDateFormat DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    public static final FastDateFormat YYYYMMDD = FastDateFormat.getInstance("yyyyMMdd");
    public static final FastDateFormat YYYYMM = FastDateFormat.getInstance("yyyyMM");
    public static final FastDateFormat YM = FastDateFormat.getInstance("yyyy_MM_");
    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateCtrlUtil() {
    }

    public static String getCurrentTimeString() {
        Date currentDate = new Date();
        return DATETIME_FORMAT.format(currentDate);
    }

    public static Long getCurrentTimeLong() {
        Date currentDate = new Date();
        return currentDate.getTime();
    }

    public static Long stringToLong(String string) throws ParseException {
        return localDate2Date(LocalDate.parse(string, dateFormat)).getTime();
    }

    public static String longToString(Long dateLong) {
        Date date = new Date(dateLong);
        return DATETIME_FORMAT.format(date);
    }

    public static String dateString(Date date) {
        return YYYYMMDD.format(date);
    }

    public static String monthString(Date date) {
        return YYYYMM.format(date);
    }

    public static Date localDate2Date(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static String getYear(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return String.valueOf(instance.get(Calendar.YEAR));
    }

    public static Date strToDate(String time) {
        return localDate2Date(LocalDate.parse(time, dateFormat));
    }
}
