package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;

public class PerryRhodanNeoSeries extends AbstractSeries
{
    // PRN 112 - 31.12.2015
    private static final DateTime RELEASE_OF_ISSUE_112 = new DateTime().withDate(2015, 12, 31).withMillisOfDay(0);

    /**
     * {@inheritDoc}
     */
    public int getDaysPerIssue()
    {
        return 14;
    }

    public DateTime fixedReleaseDate()
    {
        return RELEASE_OF_ISSUE_112;
    }

    public int fixedReleaseIssue()
    {
        return 112;
    }
}
