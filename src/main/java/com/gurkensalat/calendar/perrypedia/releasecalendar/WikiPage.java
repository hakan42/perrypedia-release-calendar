package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class WikiPage
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String seriesPrefix;

    private int issueNumber;

    private String valid;

    public WikiPage()
    {
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getSeriesPrefix()
    {
        return seriesPrefix;
    }

    public void setSeriesPrefix(String seriesPrefix)
    {
        this.seriesPrefix = seriesPrefix;
    }

    public int getIssueNumber()
    {
        return issueNumber;
    }

    public void setIssueNumber(int issueNumber)
    {
        this.issueNumber = issueNumber;
    }

    public String getValid()
    {
        return valid;
    }

    public void setValid(String valid)
    {
        this.valid = valid;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        // @formatter:off
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", getId())
                .append("seriesPrefix", getSeriesPrefix())
                .append("number", getIssueNumber())
                .append("valid", getValid())
                .toString();
        // @formatter:on
    }
}
