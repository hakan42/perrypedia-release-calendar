package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSeries implements Series
{
    private static final Logger logger = LoggerFactory.getLogger(AbstractSeries.class);

    /**
     * {@inheritDoc}
     */
    public int getDaysPerIssue()
    {
        return 7;
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxIssues()
    {
        return 9999;
    }

    /**
     * {@inheritDoc}
     */
    public DateTime getIssueReleaseDate(int issue)
    {
        logger.debug("Asked for release date of {}", issue);
        DateTime result = null;

        if (issue > -1)
        {
            if (issue <= getMaxIssues())
            {
                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0); //Set to Epoch time

                Days daysFixedIssue = Days.daysBetween(epoch, getFixedReleaseDate());
                logger.debug("  Calculated days for issue {}: {}", getFixedReleaseIssue(), daysFixedIssue.getDays());

                // Days daysFirstIssue = Days.daysBetween(epoch, new DateTime().withDate(2011, 9, 30).withMillisOfDay(0));
                // logger.debug("  Calculated days for issue {}: {}", 1, daysFirstIssue.getDays());

                int issueDifference = getFixedReleaseIssue() - issue;
                logger.debug("  Calculated issues difference: {} / {}", issueDifference, issueDifference * getDaysPerIssue());

                DateTime expected = getFixedReleaseDate().minusDays(issueDifference * getDaysPerIssue()); // .plusDays(1);
                logger.debug("  Expected datetime for issue {}: {}", issue, expected);

                result = expected;
            }
        }

        return result;
    }

    public DateTime getFirstIssueReleaseDate()
    {
        return getIssueReleaseDate(1);
    }

    public abstract DateTime getFixedReleaseDate();

    public abstract int getFixedReleaseIssue();


    /**
     * {@inheritDoc}
     */
    public abstract String getSourcePrefix();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        // @formatter:off
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("daysPerIssue", getDaysPerIssue())
                .append("fixedReleaseIssue", getFixedReleaseIssue())
                .append("fixedReleaseDate", getFixedReleaseDate())
                .append("firstIssueReleaseDate", getFirstIssueReleaseDate())
                .toString();
        // @formatter:on
    }
}
