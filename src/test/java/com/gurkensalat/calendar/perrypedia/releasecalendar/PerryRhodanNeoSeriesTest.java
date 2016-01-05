package com.gurkensalat.calendar.perrypedia.releasecalendar;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class PerryRhodanNeoSeriesTest
{
    private PerryRhodanNeoSeries testable;

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
        // PRN1 - Freitag, 30. September 2011
        DateTime expected = new DateTime().withDate(2011, 9, 30).withMillisOfDay(0);
        DateTime actual = testable.firstIssueReleaseDate();
        assertEquals(expected, actual);
    }
}
