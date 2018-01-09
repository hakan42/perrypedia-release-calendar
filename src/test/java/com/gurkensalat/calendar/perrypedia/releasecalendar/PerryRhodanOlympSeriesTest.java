package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PerryRhodanOlympSeriesTest
{
    private Series testable;

    @Before
    public void setUp()
    {
        testable = new PerryRhodanOlympSeries();
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testFirstIssueReleaseDate()
    {
        // PROL 1
        // Calculated: 12.1.2018
        // Perrypedia: Freitag, 12. Januar 2018
        DateTime expected = new DateTime().withDate(2018, 1, 11).withMillisOfDay(0);
        DateTime actual = testable.getFirstIssueReleaseDate();
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue2ReleaseDate()
    {
        // PROL 2
        // Calculated: 26.1.2018
        // Perrypedia: Freitag, 26. Januar 2018
        DateTime expected = new DateTime().withDate(2018, 1, 25).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(2);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue12ReleaseDate()
    {
        // PROL 12
        // Calculated: 15. 8. 2018
        // Perrypedia: Unknown yet...
        DateTime expected = new DateTime().withDate(2018, 6, 14).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(12);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue50ReleaseDate()
    {
        // PROL 50
        // Calculated: null, too big to release
        // Perrypedia: series is only 12 issues
        DateTime expected = null;
        DateTime actual = testable.getIssueReleaseDate(50);
        assertNull(actual);
        assertEquals(expected, actual);
    }
}
