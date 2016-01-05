package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

public abstract class AbstractSeries implements Series
{
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
    public int getMaxIssue()
    {
        return 9999;
    }

    /**
     * {@inheritDoc}
     */
    public DateTime getIssueReleaseDate(int issue)
    {
        if (issue == -1)
        {
            return null;
        }

        return null;
    }

    public DateTime getFirstIssueReleaseDate()
    {
        return getIssueReleaseDate(1);
    }

    public DateTime getFixedReleaseDate()
    {
        return null;
    }

    public int getFixedReleaseIssue()
    {
        return 0;
    }

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
