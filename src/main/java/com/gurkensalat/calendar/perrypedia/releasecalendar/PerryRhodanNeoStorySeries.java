package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerryRhodanNeoStorySeries extends AbstractSeries
{
    private static final Logger logger = LoggerFactory.getLogger(AbstractSeries.class);

    // PRNS 1 - Oktober 2014 ???
    public static final DateTime RELEASE_OF_ISSUE_1 = new DateTime().withDate(2014, 10, 1).withMillisOfDay(0);

    // PRNS 2 - 22. Dezember 2014
    public static final DateTime RELEASE_OF_ISSUE_2 = new DateTime().withDate(2014, 10, 1).withMillisOfDay(0);

    // PRNS 3 - 23. März 2015
    public static final DateTime RELEASE_OF_ISSUE_3 = new DateTime().withDate(2014, 10, 1).withMillisOfDay(0);

    // PRNS 4 - Mai 2015
    public static final DateTime RELEASE_OF_ISSUE_4 = new DateTime().withDate(2014, 10, 1).withMillisOfDay(0);

    // PRNS 5 - Oktober 2015
    public static final DateTime RELEASE_OF_ISSUE_5 = new DateTime().withDate(2014, 10, 1).withMillisOfDay(0);

    // PRNS 6 - November 2015
    public static final DateTime RELEASE_OF_ISSUE_6 = new DateTime().withDate(2014, 10, 1).withMillisOfDay(0);

    // PRNS 7 - März 2015
    public static final DateTime RELEASE_OF_ISSUE_7 = new DateTime().withDate(2014, 10, 1).withMillisOfDay(0);

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
    public int getMaxIssues()
    {
        return 7;
    }

    /**
     * {@inheritDoc}
     */
    public DateTime getIssueReleaseDate(int issue)
    {
        logger.debug("Asked for release date of {}", issue);
        DateTime result = null;

        if (issue == 1)
        {
            result = RELEASE_OF_ISSUE_1;
        }
        else if (issue == 2)
        {
            result = RELEASE_OF_ISSUE_2;
        }
        else if (issue == 3)
        {
            result = RELEASE_OF_ISSUE_3;
        }
        else if (issue == 4)
        {
            result = RELEASE_OF_ISSUE_4;
        }
        else if (issue == 5)
        {
            result = RELEASE_OF_ISSUE_5;
        }
        else if (issue == 6)
        {
            result = RELEASE_OF_ISSUE_6;
        }
        else if (issue == 7)
        {
            result = RELEASE_OF_ISSUE_7;
        }

        return result;
    }

    public DateTime getFirstIssueReleaseDate()
    {
        return getIssueReleaseDate(1);
    }

    public DateTime getFixedReleaseDate()
    {
        return getIssueReleaseDate(5);
    }

    public int getFixedReleaseIssue()
    {
        return 0;
    }

}
