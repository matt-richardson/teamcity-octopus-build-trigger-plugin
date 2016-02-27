package com.mjrichardson.teamCity.buildTriggers;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class OctopusDateTest {
    public void can_parse_date_with_t_separator_between_date_and_time() {
        OctopusDate sut = new OctopusDate("2016-12-15T16:10:11.256+00:00");
        Assert.assertEquals(sut.toString(), "2016-12-15T16:10:11.256+00:00");
    }

    public void can_parse_date_with_no_t_separator_between_date_and_time() {
        OctopusDate sut = new OctopusDate("2016-12-1516:10:11.256+00:00");
        Assert.assertEquals(sut.toString(), "2016-12-15T16:10:11.256+00:00");
    }

    public void can_parse_date_with_z_timezone_specification() {
        OctopusDate sut = new OctopusDate("2016-12-15T16:10:11.256Z");
        Assert.assertEquals(sut.toString(), "2016-12-15T16:10:11.256+00:00");
    }

    public void can_parse_date_with_utc_timezone_specification() {
        OctopusDate sut = new OctopusDate("2016-12-1516:10:11.256+00:00");
        Assert.assertEquals(sut.toString(), "2016-12-15T16:10:11.256+00:00");
    }

    public void can_parse_date_with_non_timezone_specification() {
        OctopusDate sut = new OctopusDate("2016-12-1516:10:11.256+03:00");
        Assert.assertEquals(sut.toString(), "2016-12-15T13:10:11.256+00:00");
    }

    public void creating_date_with_day_month_year_sets_time_to_midnight_and_timezone_to_utc() {
        OctopusDate sut = new OctopusDate(2016, 12, 15);
        Assert.assertEquals(sut.toString(), "2016-12-15T00:00:00.000+00:00");
    }

    public void creating_date_with_day_month_year_hour_minute_second_sets_milliseconds_to_zero_and_timezone_to_utc() {
        OctopusDate sut = new OctopusDate(2016, 12, 15, 7, 15, 35);
        Assert.assertEquals(sut.toString(), "2016-12-15T07:15:35.000+00:00");
    }

    public void creating_date_with_day_month_year_hour_minute_second_millisecond_sets_timezone_to_midnight() {
        OctopusDate sut = new OctopusDate(2016, 12, 15, 7, 15, 35, 123);
        Assert.assertEquals(sut.toString(), "2016-12-15T07:15:35.123+00:00");
    }

    public void compare_returns_0_for_equal_dates() {
        OctopusDate dateA = new OctopusDate(2016, 12, 15, 7, 15, 35, 123);
        OctopusDate dateB = new OctopusDate(2016, 12, 15, 7, 15, 35, 123);
        Assert.assertEquals(dateA.compareTo(dateB), 0);
    }

    public void compare_returns_one_when_passed_an_older_date() {
        OctopusDate dateA = new OctopusDate(2016, 12, 15, 7, 15, 35, 123);
        OctopusDate dateB = new OctopusDate(2015, 12, 15, 7, 15, 35, 123);
        Assert.assertEquals(dateA.compareTo(dateB), 1);
    }

    public void compare_returns_minus_one_when_passed_an_newer_date() {
        OctopusDate dateA = new OctopusDate(2016, 12, 15, 7, 15, 35, 123);
        OctopusDate dateB = new OctopusDate(2017, 12, 15, 7, 15, 35, 123);
        Assert.assertEquals(dateA.compareTo(dateB), -1);
    }

    public void equals_returns_true_for_equal_dates() {
        OctopusDate dateA = new OctopusDate(2016, 12, 15, 7, 15, 35, 123);
        OctopusDate dateB = new OctopusDate(2016, 12, 15, 7, 15, 35, 123);
        Assert.assertTrue(dateA.equals(dateB));
    }

    public void equals_returns_false_when_passed_an_older_date() {
        OctopusDate dateA = new OctopusDate(2016, 12, 15, 7, 15, 35, 123);
        OctopusDate dateB = new OctopusDate(2015, 12, 15, 7, 15, 35, 123);
        Assert.assertFalse(dateA.equals(dateB));
    }

    public void equals_returns_false_when_passed_an_newer_date() {
        OctopusDate dateA = new OctopusDate(2016, 12, 15, 7, 15, 35, 123);
        OctopusDate dateB = new OctopusDate(2017, 12, 15, 7, 15, 35, 123);
        Assert.assertFalse(dateA.equals(dateB));
    }

    public void equals_returns_false_when_passed_a_different_object() {
        OctopusDate dateA = new OctopusDate(2016, 12, 15, 7, 15, 35, 123);
        String notADate = "a random string";
        Assert.assertFalse(dateA.equals(notADate));
    }
}
