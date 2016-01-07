package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

public class Issue
{
    private Series series;

    private int number;

    private DateTime releaseDate;

    public Issue(Series series, int number)
    {
        this.series = series;
        this.number = number;

        DateTime releaseDate = series.getIssueReleaseDate(number);
        this.releaseDate = releaseDate;
    }

    public Series getSeries()
    {
        return series;
    }

    public void setSeries(Series series)
    {
        this.series = series;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public DateTime getReleaseDate()
    {
        return releaseDate;
    }

    public void setReleaseDate(DateTime releaseDate)
    {
        this.releaseDate = releaseDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        // @formatter:off
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("series", getSeries().getSourcePrefix())
                .append("number", getNumber())
                .append("releaseDate", getReleaseDate())
                .toString();
        // @formatter:on
    }
}
