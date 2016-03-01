package com.mjrichardson.teamCity.buildTriggers;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class NullOctopusDateTest {
    public void null_octopus_date_sets_date_to_1970() {
        OctopusDate sut = new NullOctopusDate();
        Assert.assertEquals(sut.toString(), "1970-01-01T00:00:00.000+00:00");
    }

    public void equals_returns_true_for_equal_null_dates() {
        OctopusDate dateA = new NullOctopusDate();
        OctopusDate dateB = new NullOctopusDate();
        Assert.assertTrue(dateA.equals(dateB));
    }
}
