/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iga.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;


import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class HelperCalander {

    public static boolean isPersianUnicode = false;
    public static boolean isLanguagePersian = false;
    public static boolean isLanguageArabic = false;
    @ApplicationContext
    Context context;


    public static String getPersianCalander(int year, int mounth, int day) {

        Calendar c = Calendar.getInstance();
        c.set(year, mounth, day);

        CalendarShamsi shamsi = new CalendarShamsi(c.getTime());

        String time = shamsi.year + "/" + shamsi.month + "/" + shamsi.date;

        return isLanguagePersian ? convertToUnicodeFarsiNumber(time) : time;
    }

    public static String getPersianYearMonth(int year, int mounth, int day) {

        Calendar c = Calendar.getInstance();
        c.set(year, mounth, day);

        CalendarShamsi shamsi = new CalendarShamsi(c.getTime());

        String time = shamsi.year + "/" + shamsi.month;

        return isLanguagePersian ? convertToUnicodeFarsiNumber(time) : time;
    }

    public static String getArabicCalender(int year, int mounth, int day) {

        GregorianCalendar gCal = new GregorianCalendar(year, mounth, day);
        Locale ar = new Locale("ar");
        Calendar uCal = new UmmalquraCalendar(ar);
        uCal.setTime(gCal.getTime());         // Used to properly format 'yy' pattern

        uCal.get(Calendar.YEAR);                                      // 1435
        uCal.getDisplayName(Calendar.MONTH, Calendar.LONG, ar);       // رجب
        uCal.get(Calendar.DAY_OF_MONTH);

        String time = uCal.get(Calendar.YEAR) + "/" + uCal.getDisplayName(Calendar.MONTH, Calendar.LONG, ar) + "/" + uCal.get(Calendar.DAY_OF_MONTH);
        return isLanguageArabic ? convertToUnicodeFarsiNumber(time) : time;
    }

    public static String getPersianCalander(long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return getPersianCalander(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String getArabicCalander(long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return getArabicCalender(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }


    public static int isTimeHijri() {

//        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
        return 1;
    }

    public static String checkHijriAndReturnTime(long time) {

        String result = "";

        if (isTimeHijri() == 1) {
            result = getPersianCalander(time * DateUtils.SECOND_IN_MILLIS);

        } else if (isTimeHijri() == 2) {
            result = getArabicCalander(time * DateUtils.SECOND_IN_MILLIS);
        } else {

            if (HelperCalander.isLanguageArabic) {
                result = TimeUtils.toLocal(time * DateUtils.SECOND_IN_MILLIS, "dd MM yyyy");
                String[] _date = result.split(" ");
                if (_date.length > 2) {
                    result = _date[2] + " " + convertEnglishMonthNameToArabic(Integer.parseInt(_date[1])) + " " + _date[0];
                }
            } else if (HelperCalander.isLanguagePersian) {
                result = TimeUtils.toLocal(time * DateUtils.SECOND_IN_MILLIS, "dd MM yyyy");
                String[] _date = result.split(" ");
                if (_date.length > 2) {
                    result = _date[2] + " " + convertEnglishMonthNameToPersian(Integer.parseInt(_date[1])) + " " + _date[0];
                }
            } else {
                result = TimeUtils.toLocal(time * DateUtils.SECOND_IN_MILLIS, "dd MMM yyyy");
            }
        }

        return result;
    }

    public static String milladyDate(long time) {
        return TimeUtils.toLocal(time, "dd_MM_yyyy");
    }


    public static String convertEnglishMonthNameToArabic(int month) {

        Calendar cal = new UmmalquraCalendar();
        return cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, new Locale("ar"));
    }

    public static String convertEnglishMonthNameToPersian(int month) {
        String result = "";

        switch (month) {
            case 1:
                result = "ژانویه";
                break;
            case 2:
                result = "فوریه";
                break;
            case 3:
                result = "مارس";
                break;
            case 4:
                result = "آوریل";
                break;
            case 5:
                result = "مه";
                break;
            case 6:
                result = "ژوئن";
                break;
            case 7:
                result = "ژوئیه";
                break;
            case 8:
                result = "اوت";
                break;
            case 9:
                result = "سپتامبر";
                break;
            case 10:
                result = "اکتبر";
                break;
            case 11:
                result = "نوامبر";
                break;
            case 12:
                result = "دسامبر";
                break;
        }

        return result;
    }

    public static String getPersianMonthName(int month) {

        String result = "";

        switch (month) {

            case 1:
                result = "فروردین";
                break;
            case 2:
                result = "اردیبهشت";
                break;
            case 3:
                result = "خرداد";
                break;
            case 4:
                result = "تیر";
                break;
            case 5:
                result = "مرداد";
                break;
            case 6:
                result = "شهریور";
                break;
            case 7:
                result = "مهر";
                break;
            case 8:
                result = "آبان";
                break;
            case 9:
                result = "آذر";
                break;
            case 10:
                result = "دی";
                break;
            case 11:
                result = "بهمن";
                break;
            case 12:
                result = "اسفند";
                break;
        }

        return result;
    }

    public static String getArabicMonthName(int month) {

        String result = "";

        switch (month) {

            case 0:
                result = "محرم";
                break;
            case 1:
                result = "صفر";
                break;
            case 2:
                result = "ربیع الاول";
                break;
            case 3:
                result = "ربیع الثانی";
                break;
            case 4:
                result = "جمادی الاول";
                break;
            case 5:
                result = "جمادی الثانی";
                break;
            case 6:
                result = "رجب";
                break;
            case 7:
                result = "شابان";
                break;
            case 8:
                result = "رمضان";
                break;
            case 9:
                result = "شاوال";
                break;
            case 10:
                result = "ذی القعده";
                break;
            case 11:
                result = "ذی الحجه";
                break;
        }

        return result;
    }


    public static String convertToUnicodeEnglishNumber(String text) {
        String[] englishNumber = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        if (text == null) return "";

        if (text.length() == 0) {
            return "";
        }

        String out = "";

        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if ('۰' <= c && c <= '۹') {
                String d = String.valueOf(c);
                d = d.replace("۰", "0");
                d = d.replace("۱", "1");
                d = d.replace("۲", "2");
                d = d.replace("٣", "3");
                d = d.replace("٤", "4");
                d = d.replace("۵", "5");
                d = d.replace("٦", "6");
                d = d.replace("٧", "7");
                d = d.replace("۸", "8");
                d = d.replace("۹", "9");
                int number = Integer.parseInt(d);
                out += englishNumber[number];
            } else if (c == '،') {
                out += '٫';
            } else {
                out += c;
            }
        }
        return out;

    }

    public static String convertToUnicodeFarsiNumber(String text) {

        String[] persianNumbers = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};

        if (text == null) return "";

        if (text.length() == 0) {
            return "";
        }

        String out = "";

        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if ('0' <= c && c <= '9') {
                int number = Integer.parseInt(String.valueOf(c));
                out += persianNumbers[number];
            } else if (c == '٫' || c == ',') {
                out += '،';
            } else {
                out += c;
            }
        }
        return out;
    }

    public static String unicodeManage(String text) {
        if (HelperCalander.isPersianUnicode) {
            return convertToUnicodeFarsiNumber(text);
        }
        return text;
    }

    public static String getTimeForMainRoom(long time) {

        Calendar current = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));

        String output = "";

        if (current.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) && current.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {

            if (HelperCalander.isPersianUnicode) {
                output = "\u200F" + HelperCalander.getClocktime(time, true);
            } else {
                output = HelperCalander.getClocktime(time, true);
            }

        } else if (((current.getTimeInMillis() - date.getTimeInMillis()) / (24 * 60 * 60 * 1000L)) < 7L) {// just week name

            if (HelperCalander.isTimeHijri() == 1) {
                output = getPersianStringDay(date.get(Calendar.DAY_OF_WEEK));
            } else if (HelperCalander.isTimeHijri() == 2) {
                output = getArabicStringDay(date.get(Calendar.DAY_OF_WEEK));
            } else {
                output = TimeUtils.toLocal(date.getTimeInMillis(), "EEE");
            }
        } else {

            if (HelperCalander.isTimeHijri() == 1) {

                CalendarShamsi shamsi = new CalendarShamsi(date.getTime());

                if (HelperCalander.isPersianUnicode) {
                    output = shamsi.date + " " + HelperCalander.getPersianMonthName(shamsi.month);
                } else {
                    output = HelperCalander.getPersianMonthName(shamsi.month) + " " + shamsi.date;
                }

            } else if (HelperCalander.isTimeHijri() == 2) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);

                GregorianCalendar gCal = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                Locale ar = new Locale("ar");
                Calendar uCal = new UmmalquraCalendar(ar);
                uCal.setTime(gCal.getTime());// Used to properly format 'yy' pattern

                if (HelperCalander.isPersianUnicode) {
                    output = uCal.get(Calendar.DAY_OF_MONTH) + " " + getArabicMonthName(uCal.get(Calendar.MONTH));
                } else {

                    output = getArabicMonthName(uCal.get(Calendar.MONTH)) + " " + uCal.get(Calendar.DAY_OF_MONTH);
                }
            } else {

                if (HelperCalander.isLanguageArabic) {
                    output = TimeUtils.toLocal(date.getTimeInMillis(), "MM dd");
                    String[] _date = output.split(" ");
                    if (_date.length > 1) {
                        output = _date[1] + " " + convertEnglishMonthNameToArabic(Integer.parseInt(_date[0]));
                    }
                } else if (HelperCalander.isLanguagePersian) {
                    output = TimeUtils.toLocal(date.getTimeInMillis(), "MM dd");
                    String[] _date = output.split(" ");
                    if (_date.length > 1) {
                        output = _date[1] + " " + convertEnglishMonthNameToPersian(Integer.parseInt(_date[0]));
                    }
                } else {
                    output = TimeUtils.toLocal(date.getTimeInMillis(), "dd MMM");
                }
            }
        }

        return isPersianUnicode ? convertToUnicodeFarsiNumber(output) : output;
    }

    public static String getClocktime(Long timeinMili, boolean ltr) {

        String result;

        if (KeyStore.isTimeWhole) {
            result = TimeUtils.toLocal(timeinMili, "HH:mm");
        } else if (HelperCalander.isPersianUnicode) {
            result = TimeUtils.toLocal(timeinMili, "h:mm a");
            String[] _date = result.split(" ");
            if (_date.length > 1) {
                if (ltr) {
                    result = "\u200F" + _date[0] + " " + (_date[1].toLowerCase().equals("pm") ? "PM" : "AM");
                } else {
                    result = "\u200F" + _date[0] + " " + (_date[1].toLowerCase().equals("pm") ? "PM" : "AM");
                }
            }
        } else {
            result = TimeUtils.toLocal(timeinMili, "h:mm a");
        }

        return result;
    }

    private static String getPersianStringDay(int dayOfWeek) {

        String result = "";

        switch (dayOfWeek) {

            case 1:
                result = "sunday";
                break;
            case 2:
                result = "monday";
                break;
            case 3:
                result = "tuesday";
                break;
            case 4:
                result = "wednesday";
                break;
            case 5:
                result = "thursday";
                break;
            case 6:
                result = "friday";
                break;
            case 7:
                result = "saturday";
                break;
        }

        return result;
    }

    private static String getArabicStringDay(int dayOfWeek) {

        Calendar cal = new UmmalquraCalendar();
        return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("ar"));
    }
}
