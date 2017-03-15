package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;

public class PerryRhodanTerminusSeries extends AbstractSeries
{
    // PRTER 1 - 21.4.2017 - one day less to put it on thursday
    private static final DateTime RELEASE_OF_ISSUE_1 = new DateTime().withDate(2017, 4, 21).minusDays(1).withMillisOfDay(0);

    // PRTER 1 - Freitag, 21. April 2017
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourcePrefix()
    {
        return "PRTER";
    }
}
