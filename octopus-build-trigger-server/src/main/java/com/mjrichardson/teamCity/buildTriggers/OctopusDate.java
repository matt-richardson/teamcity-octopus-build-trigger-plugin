package com.mjrichardson.teamCity.buildTriggers;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class OctopusDate {
    private static final String OCTOPUS_DATE_FORMAT = "yyyy-MM-ddHH:mm:ss.SSSZ";//2015-12-08T08:09:39.624+00:00
    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern(OCTOPUS_DATE_FORMAT);
    private DateTime dateTime;

    public OctopusDate(String s) {
        dateTime = DateTime.parse(s.replace("Z", "+00:00").replace("T", ""), dateFormat);
    }

    public OctopusDate(int year, int month, int dayOfMonth)
    {
        this(year, month, dayOfMonth, 0, 0, 0, 0);
    }

    public OctopusDate(int year, int month, int dayOfMonth, int hour, int minute, int seconds)
    {
        this(year, month, dayOfMonth, hour, minute, seconds, 0);
    }

    public OctopusDate(int year, int month, int dayOfMonth, int hour, int minute, int seconds, int milliseconds)
    {
        dateTime = new DateTime(year, month, dayOfMonth, hour, minute, seconds, milliseconds, DateTimeZone.UTC);
    }

    public int compareTo(OctopusDate compareDate) {
        return dateTime.compareTo(compareDate.dateTime);
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").withZoneUTC();
        return dateFormat.print(dateTime);
    }
}
