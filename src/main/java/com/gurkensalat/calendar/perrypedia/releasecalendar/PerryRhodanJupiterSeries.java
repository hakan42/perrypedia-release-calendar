package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;

public class PerryRhodanJupiterSeries extends AbstractSeries
{
    // PRJUP 1 - 7.7.2016 - one day less to put it on thursday
    private static final DateTime RELEASE_OF_ISSUE_1 = new DateTime().withDate(2016, 7, 8).minusDays(1).withMillisOfDay(0);

    // PRJUP 1 - Freitag, 8. July 2016
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
        return "PRJUP";
    }
}
