package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PerryRhodanNeoSeriesTest
{
    private Series testable;

    @Before
    public void setUp()
    {
        testable = new PerryRhodanNeoSeries();
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testFirstIssueReleaseDate()
    {
        // PRN 1
        // Calculated: 29. 9. 2011
        // Perrypedia: Freitag, 30. September 2011
        DateTime expected = new DateTime().withDate(2011, 9, 29).withMillisOfDay(0);
        DateTime actual = testable.getFirstIssueReleaseDate();
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue50ReleaseDate()
    {
        // PRN 50
        // Calculated: 15. 8. 2013
        // Perrypedia: Freitag, 16. August 2013
        DateTime expected = new DateTime().withDate(2013, 8, 15).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(50);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue100ReleaseDate()
    {
        // PRN 100
        // Calculated: 16. 7. 2015
        // Perrypedia: Freitag, 17. Juli 2015
        DateTime expected = new DateTime().withDate(2015, 7, 16).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(100);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue112ReleaseDate()
    {
        // PRN 100
        // Calculated: 31. 12. 2015
        // Perrypedia: Donnerstag, 31. Dezember 2015
        DateTime expected = new DateTime().withDate(2015, 12, 31).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(112);
        assertEquals(expected, actual);
    }
}
