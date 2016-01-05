package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;

public class PerryRhodanSeries extends AbstractSeries
{
    // PR 2837 - 31.12.2015
    private static final DateTime RELEASE_OF_ISSUE_2837 = new DateTime().withDate(2015, 12, 31).withMillisOfDay(0);

    public DateTime fixedReleaseDate()
    {
        return RELEASE_OF_ISSUE_2837;
    }

    public int fixedReleaseIssue()
    {
        return 2837;
    }
}
