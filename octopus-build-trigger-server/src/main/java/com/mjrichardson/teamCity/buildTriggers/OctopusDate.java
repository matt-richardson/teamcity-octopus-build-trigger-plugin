package com.mjrichardson.teamCity.buildTriggers;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class OctopusDate {
    private static final String OCTOPUS_DATE_FORMAT = "yyyy-MM-ddHH:mm:ss.SSSZ";//2015-12-08T08:09:39.624+00:00
    private DateTime dateTime;

    private OctopusDate(DateTime dateTime) {
        this.dateTime = dateTime;
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

    public static OctopusDate Parse(String input) {
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern(OCTOPUS_DATE_FORMAT);
        DateTime dateTime = DateTime.parse(input.replace("Z", "+00:00").replace("T", ""), dateFormat);
        if (dateTime.compareTo(new DateTime(1970, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC)) == 0)
            return new NullOctopusDate();
        return new OctopusDate(dateTime);
    }

    public int compareTo(OctopusDate compareDate) {
        return dateTime.compareTo(compareDate.dateTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != OctopusDate.class && obj.getClass() != NullOctopusDate.class )
            return false;
        return obj.toString().equals(toString());
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").withZoneUTC();
        return dateFormat.print(dateTime);
    }
}
