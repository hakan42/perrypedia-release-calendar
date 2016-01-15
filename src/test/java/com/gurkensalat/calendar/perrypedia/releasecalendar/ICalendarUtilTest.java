package com.gurkensalat.calendar.perrypedia.releasecalendar;

import biweekly.ICalendar;
import biweekly.component.VEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertNull;

public class ICalendarUtilTest
{
    private ICalendarUtil testable;

    // TODO write up proper unit tests...

    @Before
    public void setUp()
    {
        // TODO actually need Spring-Boot component here...
        testable = new ICalendarUtil();
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testSaveIcal()
    {
        ICalendar expected = null;
        ICalendar actual = null;
        Map<String, VEvent> data = new TreeMap<String, VEvent>();
        testable.saveIcal(data, "Foo");

        assertNull(actual);
        // assertEquals(expected, actual);
    }
}
