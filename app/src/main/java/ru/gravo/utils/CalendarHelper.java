package ru.gravo.utils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static ru.gravo.utils.BitHelper.addZero;

/**
 *
 */

//FIXME: append functions with Calendar for using common instance
public class CalendarHelper {

    private static String minDecline[] = {"минута", "минуты", "минут"};
    private static String hourDecline[] = {"час", "часа", "часов"};
    private static String lessMinute = "менее минуты";
    private static String zeroDuration = "00:00";

    public final static String SEPARATOR = ":";

    public static final long INTERVAL_MINUTE = 60 * 1000;
    public static final long INTERVAL_HOUR = 60 * INTERVAL_MINUTE;
    public static final long INTERVAL_DAY = 24 * INTERVAL_HOUR;

    public static long getTimeFromDateUTC(int year, int month, int day, int hour, int minute) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static Calendar getCalendarUTC(long time) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.setTimeInMillis(time);
        return c;
    }

    public static long getNowWithOffset() {
        Calendar c = Calendar.getInstance();
        long offset = (c.getTimeZone().useDaylightTime() ? c.get(Calendar.DST_OFFSET) : c.get(Calendar.ZONE_OFFSET));
        return (c.getTimeInMillis() + offset);
    }

    public static int getMonthUTC(long time) {
        Calendar calendar = getCalendarUTC(time);
        return calendar.get(Calendar.MONTH);
    }

    public static int getYearUTC(long time) {
        Calendar calendar = getCalendarUTC(time);
        return calendar.get(Calendar.YEAR);
    }

    public static int getEcmaDayOfWeekUTC(long time) {
        Calendar calendar = getCalendarUTC(time);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static int getDayUTC(long time) {
        Calendar calendar = getCalendarUTC(time);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getHoursUTC(long time) {
        Calendar calendar = getCalendarUTC(time);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinutesUTC(long time) {
        Calendar calendar = getCalendarUTC(time);
        return calendar.get(Calendar.MINUTE);
    }

    public static int getSecondsUTC(long time) {
        Calendar calendar = getCalendarUTC(time);
        return calendar.get(Calendar.SECOND);
    }

    public static int getDaysInMonthUTC(long time) {
        Calendar calendar = getCalendarUTC(time);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int getYmUTC(long time) {
        return getYearUTC(time) * 100 + getMonthUTC(time);
    }


    /**
     * Используется для сравнения двух дат
     *
     * @param time1 что сравнивать
     * @param time2 с чем сравнивать
     * @return true если time1 равен time2
     */
    public static boolean datesEqual(long time1, long time2) {
        if (getYearUTC(time1) != getYearUTC(time2)) {
            return false;
        }
        //else{ nop }
        if (getMonthUTC(time1) != getMonthUTC(time2)) {
            return false;
        }
        //else{ nop }
        if (getDayUTC(time1) != getDayUTC(time2)) {
            return false;
        }
        //else{ nop }
        return true;
    }

    public static boolean monthsEqual(long time1, long time2) {
        if (getYearUTC(time1) != getYearUTC(time2)) {
            return false;
        }
        //else{ nop }

        if (getMonthUTC(time1) != getMonthUTC(time2)) {
            return false;
        }
        //else{ nop }

        return true;
    }

    public static int compareDates(long time1, long time2) {
        if (!datesEqual(time1, time2) && time1 < time2) {
            return -1;
        }
        //else{ nop }

        if (!datesEqual(time1, time2) && time1 > time2) {
            return 1;
        }
        //else{ nop }

        return 0;
    }

    public static int compareMonths(long time1, long time2) {
        if (monthsEqual(time1, time2)) {
            return 0;
        }
        else {
            return (time1 < time2 ? -1 : 1);
        }
    }

    public static long getNextDayTime(long time) {
        Calendar c = getCalendarUTC(time);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTimeInMillis();
    }

    public static long getPreviousDayTime(long time) {
        Calendar c = getCalendarUTC(time);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c.getTimeInMillis();
    }

    public static long getNextMonthTime(long time) {
        Calendar c = getCalendarUTC(time);
        c.add(Calendar.MONTH, 1);
        return c.getTimeInMillis();
    }

    public static long getPreviousMonthTime(long time) {
        Calendar c = getCalendarUTC(time);
        c.add(Calendar.MONTH, -1);
        return c.getTimeInMillis();
    }


    public static String getTimeString(long time, String separator) {
        return addZero(getHoursUTC(time)) + separator + addZero(getMinutesUTC(time));
    }

    public static String getTimeString(long time) {
        return addZero(getHoursUTC(time)) + SEPARATOR + addZero(getMinutesUTC(time));
    }

    public static String getTimeStringUTC(long time, String separator) {
        return addZero(getHoursUTC(time)) + separator + addZero(getMinutesUTC(time));
    }

    public static String getTimeStringUTC(long time) {
        return addZero(getHoursUTC(time)) + SEPARATOR + addZero(getMinutesUTC(time));
    }

    //FIXME: split functions by value type - time and duration?
    public static String getDurationString(long duration) {
        if(duration < INTERVAL_DAY) {
            int hour = getHoursUTC(duration);
            int min = getMinutesUTC(duration);
            if (hour <= 0 && min <= 0) {
                int sec = getSecondsUTC(duration);
                if (sec > 0) {
                    return lessMinute;
                }
                else {
                    return zeroDuration;
                }
            }
            else {
                return addZero(getHoursUTC(duration)) + SEPARATOR + addZero(getMinutesUTC(duration));
            }
        }
        else{
            return "";
        }
    }

    public static String getMonthYearStringUTC(long time) {
        Calendar c = getCalendarUTC(time);
        DateFormatSymbols symbols = DateFormatSymbols.getInstance(Locale.getDefault());
        return getNominativeCaseMonth(time) + " " + Integer.toString(c.get(Calendar.YEAR));
    }

    public static String getNominativeCaseMonth(long time) {
        return new SimpleDateFormat("LLLL", Locale.getDefault()).format(new Date(time));
    }

    public static String getGenitiveCaseMonth(long time) {
        Calendar c = getCalendarUTC(time);
        return c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }

    public static String getTimeStringCase(long time) {
        int hour = getHoursUTC(time);
        int min = getMinutesUTC(time);
        String str = "";

        if (hour > 0) {
            str += " " + hourCase(hour);
        }
        //else{ nop }
        if (min > 0) {
            str += " " + minutesCase(min);
        }
        else if(hour <= 0){
            int sec = getSecondsUTC(time);
            if(sec > 0){
                str += " " + lessMinute;
            }
            //else{ nop }
        }
        //else{ nop }
        return str;
    }

    public static String minutesCase(int min) {
        String str = " " + min + " ";
        int n = Math.abs(min) % 100;
        int n1 = n % 10;
        if (n > 10 && n < 20) {
            return str + minDecline[2];
        }
        if (n1 > 1 && n1 < 5) {
            return str + minDecline[1];
        }
        if (n1 == 1) {
            return str + minDecline[0];
        }
        return str + minDecline[2];
    }

    public static String hourCase(int hour) {
        String str = hour + " ";
        if (hour > 4 && hour < 21) {
            return str + hourDecline[2];
        }
        if (hour == 1 || hour == 21) {
            return str + hourDecline[0];
        }
        return str + hourDecline[1];
    }
}
