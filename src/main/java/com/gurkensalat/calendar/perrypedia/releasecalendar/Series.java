package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.joda.time.DateTime;

public interface Series
{
    /**
     * Number of days between issues.
     *
     * @return number days between issues
     */
    int getDaysPerIssue();

    /**
     * The maximum issue number available.
     *
     * @return number of maximum issue
     */
    int getMaxIssues();

    /**
     * The release date of a given issue.
     *
     * @param issue
     * @return release date
     */
    DateTime getIssueReleaseDate(int issue);

    /**
     * Convenience wrapper to fetch the start date of a series.
     * @return release date
     */
    DateTime getFirstIssueReleaseDate();

    DateTime getFixedReleaseDate();

    int getFixedReleaseIssue();

    /**
     * Prefix on perrypedia wiki articles.
     *
     * @return prefix
     */
    String getSourcePrefix();
}
