package com.gurkensalat.calendar.perrypedia.releasecalendar;

import biweekly.component.VEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class EventUtilTest
{
    private EventUtil testable;

    // TODO write up proper unit tests...

    @Before
    public void setUp()
    {
        // TODO actually need Spring-Boot component here...
        testable = new EventUtil();
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testConvertToIcalEvent()
    {
        Series series = new PerryRhodanSeries();
        Issue issue = new Issue(series, 42);
        issue.setNumber(42);
        issue.setSeries(series);
        WikiPage wikiPage = new WikiPage();

        VEvent expected = null;
        VEvent actual = testable.convertToIcalEvent(issue, wikiPage);

        assertNotNull(actual);
        // assertEquals(expected, actual);

        assertFalse("No alarm added to event", actual.getAlarms().size() == 0);
    }
}
