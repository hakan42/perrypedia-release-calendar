package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PerryRhodanMissionSolSeriesTest
{
    private Series testable;

    @Before
    public void setUp()
    {
        testable = new PerryRhodanMissionSolSeries();
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testFirstIssueReleaseDate()
    {
        // PRMS 1
        // Calculated: 13. 6. 2019
        // Perrypedia: Freitag, 14. Juni 2019
        DateTime expected = new DateTime().withDate(2019, 6, 13).withMillisOfDay(0);
        DateTime actual = testable.getFirstIssueReleaseDate();
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue2ReleaseDate()
    {
        // PRMS 2
        // Calculated: 27. 6. 2019
        // Perrypedia: Freitag, 28. Juni 2019
        DateTime expected = new DateTime().withDate(2019, 6, 27).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(2);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue12ReleaseDate()
    {
        // PRMS 12
        // Calculated: 14. 11. 2019
        // Perrypedia: Unknown yet...
        DateTime expected = new DateTime().withDate(2019, 11, 14).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(12);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue50ReleaseDate()
    {
        // PRMS 50
        // Calculated: null, too big to release
        // Perrypedia: series is only 12 issues
        DateTime expected = null;
        DateTime actual = testable.getIssueReleaseDate(50);
        assertNull(actual);
        assertEquals(expected, actual);
    }
}
