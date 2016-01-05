package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;

public class PerryRhodanNeoStorySeries extends AbstractSeries
{
    // PRNS 1 - Oktober 2014 ???
    public static final DateTime RELEASE_OF_ISSUE_1 = null;

    // PRNS 5 - November 2015 ???
    public static final DateTime RELEASE_OF_ISSUE_5 = null;

    /**
     * {@inheritDoc}
     */
    public int getDaysPerIssue()
    {
        return 90;
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxIssue()
    {
        return 7;
    }

    public DateTime getFirstIssueReleaseDate()
    {
        // FIXME mock for now, implement properly...
        return RELEASE_OF_ISSUE_1;
    }

    public DateTime getFixedReleaseDate()
    {
        return RELEASE_OF_ISSUE_5;
    }

    public int getFixedReleaseIssue()
    {
        return 0;
    }

}
