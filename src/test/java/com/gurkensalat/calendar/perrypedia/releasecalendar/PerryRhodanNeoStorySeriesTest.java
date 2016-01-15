package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PerryRhodanNeoStorySeriesTest
{
    private Series testable;

    @Before
    public void setUp()
    {
        testable = new PerryRhodanNeoStorySeries();
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testFirstIssueReleaseDate()
    {
        // PRNS 1
        // Calculated: 1. 10. 2014
        // Perrypedia: Oktober 2014
        DateTime expected = new DateTime().withDate(2014, 10, 1).withMillisOfDay(0);
        DateTime actual = testable.getFirstIssueReleaseDate();
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue50ReleaseDate()
    {
        // PRNS 50
        // Calculated: null, too big to release
        // Perrypedia: too far in the future...
        DateTime expected = null;
        DateTime actual = testable.getIssueReleaseDate(50);
        assertNull(actual);
        assertEquals(expected, actual);
    }
}
