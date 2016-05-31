package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PerryRhodanJupiterSeriesTest
{
    private Series testable;

    @Before
    public void setUp()
    {
        testable = new PerryRhodanJupiterSeries();
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testFirstIssueReleaseDate()
    {
        // PRJUP 1
        // Calculated: 7.7.2016
        // Perrypedia: Freitag, 8. July 2016
        DateTime expected = new DateTime().withDate(2016, 7, 7).withMillisOfDay(0);
        DateTime actual = testable.getFirstIssueReleaseDate();
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue2ReleaseDate()
    {
        // PRJUP 2
        // Calculated: 21.7.2016
        // Perrypedia: Freitag, 22. July 2016
        DateTime expected = new DateTime().withDate(2016, 7, 21).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(2);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue12ReleaseDate()
    {
        // PRJUP 12
        // Calculated: 23. 6. 2016
        // Perrypedia: Unknown yet...
        DateTime expected = new DateTime().withDate(2016, 12, 8).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(12);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue50ReleaseDate()
    {
        // PRJUP 50
        // Calculated: null, too big to release
        // Perrypedia: series is only 12 issues
        DateTime expected = null;
        DateTime actual = testable.getIssueReleaseDate(50);
        assertNull(actual);
        assertEquals(expected, actual);
    }
}
