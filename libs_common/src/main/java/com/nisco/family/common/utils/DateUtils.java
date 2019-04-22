package com.nisco.family.common.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cathy on 2016/11/21.
 */
public class DateUtils {

    public static String dateToString(String timeStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = timeStr.substring(timeStr.indexOf("(") + 1, timeStr.indexOf(")"));
        long msec = Long.parseLong(date);
        Date mDate = new Date(msec);
        String time = format.format(mDate);
        return time;
    }

    /**
     * 将毫秒数转化为时间
     *
     * @param askTime
     * @return
     */
    public static String getDate(String askTime) {
        if (askTime != null) {
            String str = askTime.substring(askTime.indexOf("(") + 1, askTime.indexOf(")"));
            long msec = Long.parseLong(str);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String mDate = sdf.format(new Date(msec));
            return mDate;
        }
        return null;
    }

    public static String getCtTime(String date) {
        String newDate = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
        return newDate;
    }

    public static String getDateStr(long mill) {
        //mill为你龙类型的时间戳
        Date date = new Date(mill);
        String strs = "";
        try {
            //yyyy表示年MM表示月dd表示日
            //yyyy-MM-dd是日期的格式，比如2015-12-12如果你要得到2015年12月12日就换成yyyy年MM月dd日
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //进行格式化
            strs = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }

    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd
     *
     * @param str1 the first date
     * @param str2 the second date
     * @return true <br/>false
     */
    public static boolean isDate2Bigger(String str1, String str2) {
        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            isBigger = true;
        } else if (dt1.getTime() <= dt2.getTime()) {
            isBigger = false;
        }
        return isBigger;
    }

    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd HH:mm
     *
     * @param str1 the first date
     * @param str2 the second date
     * @return true <br/>false
     */
    public static boolean isDateBigger(String str1, String str2, String format) {
        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() >= dt2.getTime()) {
            isBigger = true;
        } else if (dt1.getTime() < dt2.getTime()) {
            isBigger = false;
        }
        return isBigger;
    }

    public static StringBuffer toStringBuffer(String fromDate, String fromTime, String toDate, String toTime) {
        StringBuffer dateTime = new StringBuffer();
        dateTime.append(fromDate.equals("") ? "" : fromDate.substring(0, 4));
        dateTime.append("-");
        dateTime.append(fromDate.equals("") ? "" : fromDate.substring(4, 6));
        dateTime.append("-");
        dateTime.append(fromDate.equals("") ? "" : fromDate.substring(6, 8));
        dateTime.append(" ");
        dateTime.append(fromTime.equals("") ? "" : fromTime.substring(0, 2));
        dateTime.append(":");
        dateTime.append(fromTime.equals("") ? "" : fromTime.substring(2, 4));
        dateTime.append("\n至\n");
        dateTime.append(toDate.equals("") ? "" : toDate.substring(0, 4));
        dateTime.append("-");
        dateTime.append(toDate.equals("") ? "" : toDate.substring(4, 6));
        dateTime.append("-");
        dateTime.append(toDate.equals("") ? "" : toDate.substring(6, 8));
        dateTime.append(" ");
        dateTime.append(toTime.equals("") ? "" : toTime.substring(0, 2));
        dateTime.append(":");
        dateTime.append(toTime.equals("") ? "" : toTime.substring(2, 4));

        return dateTime;
    }

    public static StringBuffer toStringBuffer(String date, String time, int flag) {
        StringBuffer dateTime = new StringBuffer();
        dateTime.append(date.equals("") ? "" : date.substring(0, 4));
        dateTime.append("-");
        dateTime.append(date.equals("") ? "" : date.substring(4, 6));
        dateTime.append("-");
        dateTime.append(date.equals("") ? "" : date.substring(6, 8));
        if (flag == 0) {
            dateTime.append(" ");
        } else if (flag == 1) {
            dateTime.append("\n");
        }
        dateTime.append(time.equals("") ? "" : time.substring(0, 2));
        dateTime.append(":");
        dateTime.append(time.equals("") ? "" : time.substring(2, 4));

        return dateTime;
    }

    public static String getCurrentDate(int year, int month) {
        String date = null;
        if (month < 10) {
            date = String.valueOf(year) + "0" + String.valueOf(month);
        } else {
            date = String.valueOf(year) + String.valueOf(month);
        }
        return date;
    }
    /**
     * 把20180630格式的时间转成2018-06-30
     * @return
     */
    public static String changeDateType(String dateString){
        if (TextUtils.isEmpty(dateString)){
            return "";
        }else if (6 == dateString.length()){
            return dateString.subSequence(0,4) + "-" + dateString.substring(4, 6);
        }else if (8 == dateString.length()){
            return dateString.subSequence(0,4) + "-" + dateString.substring(4, 6)+ "-" + dateString.substring(6);
        }else if (12 == dateString.length()){
            return dateString.subSequence(0,4) + "-" + dateString.substring(4, 6)+ "-" + dateString.substring(6, 8) + "  " + dateString.substring(8, 10) + ":" + dateString.substring(10);
        }else if (14 == dateString.length()){
            return dateString.subSequence(0,4) + "-" + dateString.substring(4, 6)+ "-" + dateString.substring(6, 8) + "  " + dateString.substring(8, 10) + ":" + dateString.substring(10, 12) + ":" + dateString.substring(12);
        }else {
            return dateString;
        }
    }
    /**
     * 把20180630格式的时间转成2018/06/30
     * @return
     */
    public static String changeDateType2(String dateString){
        if (TextUtils.isEmpty(dateString)){
            return "";
        }
        return dateString.subSequence(0,4) + "/" + dateString.substring(4, 6)+ "/" + dateString.substring(6);
    }
    /**
     * 把20180630格式的时间转成06/30
     * @return
     */
    public static String changeDateType3(String dateString){
        if (TextUtils.isEmpty(dateString)){
            return "";
        }
        return dateString.substring(4, 6)+ "/" + dateString.substring(6);
    }

    /**
     * 把111111格式的时间转成11:11:11
     * @return
     */
    public static String changeTimeType(String dateString){
        if (TextUtils.isEmpty(dateString)){
            return "";
        }else if (4 == dateString.length()){
            return dateString.subSequence(0,2) + ":" + dateString.substring(2, 4);
        }else if (6 == dateString.length()){
            return dateString.subSequence(0,2) + ":" + dateString.substring(2, 4)+ ":" + dateString.substring(4);
        }else {
            return dateString;
        }
    }

    /***
     * 获取系统当前时间
     */
    public static String getTimeDescription() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "   MM月dd日  HH:mm");
        return dateFormat.format(date).toString();
    }

    /**
     * 比较两个格式如"yyyy-MM-dd"的字符串时间大小
     * @param dateEnd
     * @param dateBegin
     * @return
     */
    public static boolean compareDate(String dateEnd, String dateBegin) {

        SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
        boolean result = false;
        try {
            if(sdf.parse(dateEnd).getTime() - sdf.parse(dateBegin).getTime() >= 0){
                result = true;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 获取前n天日期、后n天日期
     *
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @return
     */
    public static String getOldDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }
}

