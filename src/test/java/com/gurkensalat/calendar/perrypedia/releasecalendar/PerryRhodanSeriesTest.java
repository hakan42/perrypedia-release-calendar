package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PerryRhodanSeriesTest
{
    private Series testable;

    @Before
    public void setUp()
    {
        testable = new PerryRhodanSeries();
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testFirstIssueReleaseDate()
    {
        // PR 1
        // Calculated: 24. 8. 1961
        // Perrypedia: Freitag, 8. September 1961
        DateTime expected = new DateTime().withDate(1961, 8, 24).withMillisOfDay(0);
        DateTime actual = testable.getFirstIssueReleaseDate();
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue1000ReleaseDate()
    {
        // PR 1000
        // Calculated: 16. 10. 1980
        // Perrypedia: Dienstag, 21. Oktober 1980
        DateTime expected = new DateTime().withDate(1980, 10, 16).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(1000);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue2500ReleaseDate()
    {
        // PR 2500
        // Calculated: 16. 7. 2009
        // Perrypedia: 	Freitag, 17. Juli 2009
        DateTime expected = new DateTime().withDate(2009, 7, 16).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(2500);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue2800ReleaseDate()
    {
        // PR 2800
        // Calculated: 16. 4. 2015
        // Perrypedia: Freitag, 17. April 2015
        DateTime expected = new DateTime().withDate(2015, 4, 16).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(2800);
        assertEquals(expected, actual);
    }

    @Test
    public void testIssue2837ReleaseDate()
    {
        // PR 2837
        // Calculated: 31. 21. 2015
        // Perrypedia: Donnerstag, 31. Dezember 2015
        DateTime expected = new DateTime().withDate(2015, 12, 31).withMillisOfDay(0);
        DateTime actual = testable.getIssueReleaseDate(2837);
        assertEquals(expected, actual);
    }
}
