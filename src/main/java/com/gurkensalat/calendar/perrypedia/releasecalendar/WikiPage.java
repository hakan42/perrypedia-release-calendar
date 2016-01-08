package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "WIKI_PAGE")
public class WikiPage
{
    @Transient
    private static final String VALID = "Y";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String seriesPrefix;

    private int issueNumber;

    private String sourcePageId;

    private String sourcePageTitle;

    private String sourcePageValid;

    private String fullPageId;

    private String fullPageTitle;

    private String fullPageValid;

    @Column(name = "FULL_PAGE_TEXT", length = 8192)
    private String fullPageText;

    private String valid;

    public WikiPage()
    {
    }

    public static String getVALID()
    {
        return VALID;
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

    public String getSourcePageId()
    {
        return sourcePageId;
    }

    public void setSourcePageId(String sourcePageId)
    {
        this.sourcePageId = sourcePageId;
    }

    public String getSourcePageTitle()
    {
        return sourcePageTitle;
    }

    public void setSourcePageTitle(String sourcePageTitle)
    {
        this.sourcePageTitle = sourcePageTitle;
    }

    public String getSourcePageValid()
    {
        return sourcePageValid;
    }

    public void setSourcePageValid(String sourcePageValid)
    {
        this.sourcePageValid = sourcePageValid;
    }

    public String getFullPageId()
    {
        return fullPageId;
    }

    public void setFullPageId(String fullPageId)
    {
        this.fullPageId = fullPageId;
    }

    public String getFullPageTitle()
    {
        return fullPageTitle;
    }

    public void setFullPageTitle(String fullPageTitle)
    {
        this.fullPageTitle = fullPageTitle;
    }

    public String getFullPageValid()
    {
        return fullPageValid;
    }

    public void setFullPageValid(String fullPageValid)
    {
        this.fullPageValid = fullPageValid;
    }

    public String getFullPageText()
    {
        return fullPageText;
    }

    public void setFullPageText(String fullPageText)
    {
        this.fullPageText = fullPageText;
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
