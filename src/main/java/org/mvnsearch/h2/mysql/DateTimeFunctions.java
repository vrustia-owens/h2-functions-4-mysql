package org.mvnsearch.h2.mysql;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * date time functions for mysql
 *
 * @author linux_china
 */
public class DateTimeFunctions {
    public static LocalDateTime ZERO_START_TIME = LocalDateTime.of(0, 1, 1, 0, 0, 0);
    public static LocalDateTime UNIX_START_TIME = LocalDateTime.of(1970, 1, 1, 0, 0, 0);

    /**
     * function for UNIX_TIMESTAMP
     *
     * @return current time millis
     */
    public static Long unixTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * function for UNIX_TIMESTAMP
     *
     * @return current time millis
     */
    public static Long unixTimestamp(String text) throws Exception {
        return DateUtils.parseDate(text, "YYYY-MM-DD", "YYYY-MM-DD HH:mm:ss", "YYYY-MM-DD HH:mm:ss.S").getTime();
    }

    public static Date fromUnixTime(Long unixTime) {
        return new Date(unixTime * 1000);
    }

    public static Date addDate(String dateText, Integer days) throws Exception {
        Date date = parseDate(dateText);
        return new Date(date.getTime() + days * 24 * 60 * 60 * 1000);
    }

    public static Date subDate(String dateText, Integer days) throws Exception {
        Date date = parseDate(dateText);
        return new Date(date.getTime() - days * 24L * 60L * 60L * 1000L);
    }

    public static Date addTime(String dateText, String timeText) throws Exception {
        Date date = DateUtils.parseDate(dateText, "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.S", "HH:mm:ss", "HH:mm:ss.S", "dd HH:mm:ss", "dd HH:mm:ss.S");
        Date time = DateUtils.parseDate(timeText, "HH:mm:ss", "HH:mm:ss.S", "dd HH:mm:ss", "dd HH:mm:ss.S");
        return new Date(date.getTime() + time.getTime());
    }

    public static Date subTime(String dateText, String timeText) throws Exception {
        Date date = DateUtils.parseDate(dateText, "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.S", "HH:mm:ss", "HH:mm:ss.S", "dd HH:mm:ss", "dd HH:mm:ss.S");
        Date time = DateUtils.parseDate(timeText, "HH:mm:ss", "HH:mm:ss.S", "dd HH:mm:ss", "dd HH:mm:ss.S");
        return new Date(date.getTime() - time.getTime());
    }


    public static String date(String text) throws Exception {
        Date date = parseDate(text);
        return DateFormatUtils.format(date, "yyyy-MM-dd");
    }

    public static String utcTimestamp() {
        return DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"));
    }

    public static String utcDate() {
        return DateFormatUtils.format(new Date(), "yyyy-MM-dd", TimeZone.getTimeZone("UTC"));
    }

    public static String utcTime() {
        return DateFormatUtils.format(new Date(), "HH:mm:ss", TimeZone.getTimeZone("UTC"));
    }

    public static Date fromDays(Integer days) {
        return java.sql.Date.valueOf(ZERO_START_TIME.plusDays(days).toLocalDate());
    }

    public static Long toDays(String timeText) throws Exception {
        LocalDate startDate = ZERO_START_TIME.toLocalDate();
        Date date = DateUtils.parseDate(timeText, "yyyy-MM-dd", "yy-MM-dd");
        LocalDate endDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return (ChronoUnit.DAYS.between(startDate, endDate));
    }

    public static Long toSeconds(String timeText) throws Exception {
        Date date = parseDate(timeText);
        long days = ChronoUnit.DAYS.between(ZERO_START_TIME.toLocalDate(), UNIX_START_TIME.toLocalDate());
        return date.getTime() / 1000 + days * 24 * 60 * 60;
    }

    public static Long timeToSeconds(String timeText) throws Exception {
        Date date = DateUtils.parseDate("1970-01-01 " + timeText + " UTC", "yyyy-MM-dd HH:mm:ss z");
        return date.getTime() / 1000;
    }

    public static String secondsToTime(Integer totalSeconds) {
        long seconds = totalSeconds % 60;
        long minutes = totalSeconds / 60 % 60;
        long hours = totalSeconds / (3600) % 24;
        return padNumber(hours) + ":" + padNumber(minutes) + ":" + padNumber(seconds);
    }

    public static String time(String timeText) throws Exception {
        Date date = parseDate(timeText);
        return DateFormatUtils.format(date, " HH:mm:ss");
    }

    public static String dateFormat(String timeText, String mysqlPattern) throws Exception {
        Date date = parseDate(timeText);
        String javaPattern = mysqlPattern;
        for (Map.Entry<String, String> entry : mysqlToJavaDateFormat().entrySet()) {
            javaPattern = javaPattern.replace(entry.getKey(), entry.getValue());
        }
        return DateFormatUtils.format(date, javaPattern);
    }

    public static String timeFormat(String timeText, String mysqlPattern) throws Exception {
        Date date = DateUtils.parseDate(timeText, "yyyy-MM-dd HH:mm:ss", "HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS", "HH:mm:ss.SSS");
        String javaPattern = mysqlPattern;
        for (Map.Entry<String, String> entry : mysqlToJavaDateFormat().entrySet()) {
            javaPattern = javaPattern.replace(entry.getKey(), entry.getValue());
        }
        return DateFormatUtils.format(date, javaPattern);
    }

    public static String lastDay(String dateText) throws Exception {
        LocalDate localDate = parseLocalDate(dateText);
        LocalDate lastDay = localDate.with(TemporalAdjusters.lastDayOfMonth());
        return lastDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static Date now() {
        return new Date();
    }

    public static Date makeDate(Integer year, Integer days) {
        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime end = start.plusDays(days - 1);
        return java.sql.Date.valueOf(end.toLocalDate());
    }

    public static String makeTime(Integer hours, Integer minutes, Integer seconds) {
        return padNumber((long) hours) + ":" + padNumber((long) minutes) + ":" + padNumber((long) seconds);
    }

    public static Integer sleep(Integer seconds) throws Exception {
        Thread.sleep(seconds * 1000);
        return 0;
    }

    public static String strToDate(String dateStr, String mysqlPattern) throws Exception {
        String javaPattern = mysqlPattern;
        for (Map.Entry<String, String> entry : mysqlToJavaDateFormat().entrySet()) {
            javaPattern = javaPattern.replace(entry.getKey(), entry.getValue());
        }
        Date date = DateUtils.parseDate(dateStr, javaPattern);
        if (mysqlPattern.toLowerCase().contains("%y")) {
            return DateFormatUtils.format(date, "yyyy-MM-dd");
        } else {
            return DateFormatUtils.format(date, "HH:mm:ss");
        }
    }

    public static Integer yearWeek(String dateStr, Integer mode) throws Exception {
        Date date = DateUtils.parseDate(dateStr, "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS");
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int day = localDate.getDayOfWeek().getValue();
        if (day > 0) {
            localDate = localDate.minusDays(day);
        }
        int year = localDate.getYear();
        int offset = mode == 0 ? 1 : 0;
        int weekNumber = localDate.get(WeekFields.of(Locale.getDefault()).weekOfYear()) - offset;
        return year * 100 + weekNumber;
    }


    public static Integer yearWeek(String dateStr) throws Exception {
        return yearWeek(dateStr, 0);
    }

    public static Integer weekOfYear(String dateStr) throws Exception {
        LocalDate localDate = parseLocalDate(dateStr);
        return localDate.get(WeekFields.of(Locale.getDefault()).weekOfYear());
    }

    public static Integer weekDay(String dateStr) throws Exception {
        LocalDate localDate = parseLocalDate(dateStr);
        int firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek().getValue();
        if (firstDayOfWeek == 7) {
            return localDate.get(WeekFields.of(Locale.getDefault()).dayOfWeek()) - 2;
        } else {
            return localDate.get(WeekFields.of(Locale.getDefault()).dayOfWeek()) - 1;
        }
    }

    public static Long microSecond(String dateStr) throws Exception {
        Date date = parseDate(dateStr);
        return date.getTime() % 1000;

    }

    public static String convertTZ(String dateStr, String originTZ, String targetTZ) throws Exception {
        Date date = DateUtils.parseDate(dateStr + " " + originTZ, "yyyy-MM-dd HH:mm:ss z", "yyyy-MM-dd HH:mm:ss Z", "yyyy-MM-dd HH:mm:ss XXX");
        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(targetTZ);
        } catch (Exception e) {
            zoneId = ZoneOffset.of(targetTZ);
        }
        LocalDate localDate = date.toInstant().atZone(zoneId).toLocalDate();
        return DateFormatUtils.format(java.sql.Date.valueOf(localDate), "yyyy-MM-dd HH:mm:ss");
    }

    public static Integer periodAdd(Integer yearAndMonth, Integer months) throws Exception {
        String text = String.valueOf(yearAndMonth);
        Date date = DateUtils.parseDate(text, "yyyyMM", "yyMM");
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate newDate = localDate.plusMonths(months);
        return newDate.getYear() * 100 + newDate.getMonthValue();
    }

    public static Integer periodDiff(Integer yearAndMonth1, Integer yearAndMonth2) {
        int months1 = ((yearAndMonth1 / 100) * 12) + yearAndMonth1 % 100;
        int months2 = ((yearAndMonth2 / 100) * 12) + yearAndMonth2 % 100;
        return months1 - months2;
    }

    public static String timeDiff(String dateStr1, String dateStr2) throws Exception {
        Date date1 = parseDate(dateStr1);
        Date date2 = parseDate(dateStr2);
        String sign = "";
        if (date1.getTime() < date2.getTime()) {
            sign = "-";
        }
        Long totalMilliSeconds = Math.abs(date1.getTime() - date2.getTime());
        long milliSeconds = totalMilliSeconds % 1000;
        Long seconds = totalMilliSeconds / 1000 / 60 % 60;
        Long minutes = totalMilliSeconds / 1000 / 3600 % 60;
        long hours = totalMilliSeconds / 1000 / 3600;
        return sign + hours + ":" + padNumber(minutes) + ":" + padNumber(seconds) + "." + milliSeconds;
    }

    private static String padNumber(Long number) {
        if (number < 10L) return "0" + number;
        return String.valueOf(number);
    }

    private static Map<String, String> mysqlToJavaDateFormat() {
        Map<String, String> convert = new HashMap<>();
        convert.put("%a", "E");
        convert.put("%b", "M");
        convert.put("%c", "M");
        convert.put("%d", "dd");
        convert.put("%e", "d");
        convert.put("%f", "S");
        convert.put("%H", "HH");
        convert.put("%h", "H");
        convert.put("%I", "h");
        convert.put("%i", "mm");
        convert.put("%J", "D");
        convert.put("%k", "h");
        convert.put("%l", "h");
        convert.put("%M", "M");
        convert.put("%m", "MM");
        convert.put("%p", "a");
        convert.put("%r", "hh:mm:ss a");
        convert.put("%s", "ss");
        convert.put("%S", "ss");
        convert.put("%T", "HH:mm:ss");
        convert.put("%U", "w");
        convert.put("%u", "w");
        convert.put("%V", "w");
        convert.put("%v", "w");
        convert.put("%W", "EEE");
        convert.put("%w", "F");
        convert.put("%Y", "yyyy");
        convert.put("%y", "yy");
        return convert;
    }

    public static Date parseDate(String dateStr) throws Exception {
        return DateUtils.parseDate(dateStr, "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.S", "HH:mm:ss", "HH:mm:ss.S");
    }

    public static LocalDate parseLocalDate(String dateStr) throws Exception {
        return parseDate(dateStr).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
