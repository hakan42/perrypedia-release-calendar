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

    DateTime getFirstIssueReleaseDate();

    DateTime getFixedReleaseDate();

    int getFixedReleaseIssue();
}
