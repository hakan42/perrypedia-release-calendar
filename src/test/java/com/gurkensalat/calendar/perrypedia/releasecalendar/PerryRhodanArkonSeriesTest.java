package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PerryRhodanArkonSeriesTest
{
    private Series testable;

    @Before
    public void setUp()
    {
        testable = new PerryRhodanArkonSeries();
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testFirstIssueReleaseDate()
    {
        // PRAR 1
        // Calculated: 21.1.2016
        // Perrypedia: Freitag, 22. Januar 2016
        DateTime expected = new DateTime().withDate(2016, 1, 21).withMillisOfDay(0);
        DateTime actual = testable.getFirstIssueReleaseDate();
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue2ReleaseDate()
    {
        // PRAR 2
        // Calculated: 4. 2.2016
        // Perrypedia: Freitag, 5. Februar 2016
        DateTime expected = new DateTime().withDate(2016, 2, 4).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(2);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue12ReleaseDate()
    {
        // PRAR 12
        // Calculated: 23. 6. 2016
        // Perrypedia: Unknown yet...
        DateTime expected = new DateTime().withDate(2016, 6, 23).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(12);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue50ReleaseDate()
    {
        // PRAR 50
        // Calculated: null, too big to release
        // Perrypedia: series is only 12 issues
        DateTime expected = null;
        DateTime actual = testable.getIssueReleaseDate(50);
        assertNull(actual);
        assertEquals(expected, actual);
    }
}
