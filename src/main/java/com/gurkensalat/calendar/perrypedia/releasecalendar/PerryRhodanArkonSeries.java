package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;

public class PerryRhodanArkonSeries extends AbstractSeries
{
    // PRAR 1 - 22.1.2016
    private static final DateTime RELEASE_OF_ISSUE_1 = new DateTime().withDate(2016, 1, 22).withMillisOfDay(0);

    /**
     * {@inheritDoc}
     */
    public int getDaysPerIssue()
    {
        return 14;
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxIssue()
    {
        return 12;
    }

    public DateTime getFixedReleaseDate()
    {
        return RELEASE_OF_ISSUE_1;
    }

    public int getFixedReleaseIssue()
    {
        return 1;
    }
}
