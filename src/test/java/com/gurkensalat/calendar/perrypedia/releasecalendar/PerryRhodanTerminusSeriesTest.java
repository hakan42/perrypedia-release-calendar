package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PerryRhodanTerminusSeriesTest
{
    private Series testable;

    @Before
    public void setUp()
    {
        testable = new PerryRhodanTerminusSeries();
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testFirstIssueReleaseDate()
    {
        // PRTER 1
        // Calculated: 21.4.2017
        // Perrypedia: Freitag, 21. April 2017
        DateTime expected = new DateTime().withDate(2017, 4, 20).withMillisOfDay(0);
        DateTime actual = testable.getFirstIssueReleaseDate();
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue2ReleaseDate()
    {
        // PRTER 2
        // Calculated: 4.5.2016
        // Perrypedia: Freitag, 4. May 2017
        DateTime expected = new DateTime().withDate(2017, 5, 4).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(2);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue12ReleaseDate()
    {
        // PRTER 12
        // Calculated: 21. 9. 2017
        // Perrypedia: Unknown yet...
        DateTime expected = new DateTime().withDate(2017, 9, 21).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(12);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue50ReleaseDate()
    {
        // PRTER 50
        // Calculated: null, too big to release
        // Perrypedia: series is only 12 issues
        DateTime expected = null;
        DateTime actual = testable.getIssueReleaseDate(50);
        assertNull(actual);
        assertEquals(expected, actual);
    }
}
