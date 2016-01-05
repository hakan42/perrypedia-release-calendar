package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;

public class PerryRhodanSeries extends AbstractSeries
{
    // PR 2837 - 31.12.2015
    private static final DateTime RELEASE_OF_ISSUE_2837 = new DateTime().withDate(2015, 12, 31).withMillisOfDay(0);

    // PR 1 - Freitag, 8. September 1961
    private static DateTime releaseOfFirstIssue;

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
        return RELEASE_OF_ISSUE_2837;
    }

    public int getFixedReleaseIssue()
    {
        return 2837;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourcePrefix()
    {
        return "PR";
    }
}
