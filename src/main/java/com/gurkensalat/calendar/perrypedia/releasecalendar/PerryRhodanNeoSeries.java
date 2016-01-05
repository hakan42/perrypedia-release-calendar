package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;

public class PerryRhodanNeoSeries extends AbstractSeries
{
    // PRN 112 - 31.12.2015
    private static final DateTime RELEASE_OF_ISSUE_112 = new DateTime().withDate(2015, 12, 31).withMillisOfDay(0);

    // PRN1 - Freitag, 30. September 2011
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
        return RELEASE_OF_ISSUE_112;
    }

    public int getFixedReleaseIssue()
    {
        return 112;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourcePrefix()
    {
        return "PRN";
    }
}
