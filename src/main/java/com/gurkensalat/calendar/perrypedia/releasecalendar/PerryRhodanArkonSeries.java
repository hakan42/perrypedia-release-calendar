package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;

public class PerryRhodanArkonSeries extends AbstractSeries
{
    // PRAR 1 - 22.1.2016
    private static final DateTime RELEASE_OF_ISSUE_1 = new DateTime().withDate(2016, 1, 22).withMillisOfDay(0);

    // PRAR 1 - Freitag, 22. Januar 2016
    private static DateTime releaseOfFirstIssue;

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
    public int getMaxIssues()
    {
        return 12;
    }

    /**
     * {@inheritDoc}
     */
    public DateTime getFirstIssueReleaseDate()
    {
        if (releaseOfFirstIssue == null)
        {
            releaseOfFirstIssue = getIssueReleaseDate(1);
        }

        return releaseOfFirstIssue;
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
