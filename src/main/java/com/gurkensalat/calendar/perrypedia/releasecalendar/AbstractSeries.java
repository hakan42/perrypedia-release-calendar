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

    public DateTime getFirstIssueReleaseDate()
    {
        return null;
    }

    public DateTime fixedReleaseDate()
    {
        return null;
    }

    public int fixedReleaseIssue()
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
                .append("fixedReleaseIssue", fixedReleaseIssue())
                .append("fixedReleaseDate", fixedReleaseDate())
                .append("firstIssueReleaseDate", getFirstIssueReleaseDate())
                .toString();
        // @formatter:on
    }
}
