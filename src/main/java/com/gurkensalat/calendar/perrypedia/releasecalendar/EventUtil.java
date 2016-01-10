package com.gurkensalat.calendar.perrypedia.releasecalendar;

import biweekly.component.VEvent;
import biweekly.property.Categories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventUtil
{
    @Value("${info.build.artifact}")
    private String projectArtifact;

    @Value("${info.build.version}")
    private String projectVersion;

    public VEvent convertToIcalEvent(Issue issue, WikiPage wikiPage)
    {
        VEvent result = new VEvent();

        // BEGIN:VEVENT
        // DTSTART:20160120T030000
        // DTEND:20160120T040000
        // SUMMARY:Marvel's Agent Carter 2x2 - A View in the Dark
        // DESCRIPTION:Coming Soon...
        // URL:
        // UID:MARVELSAGENTCARTER_2_2
        // SEQUENCE:0
        // DTSTAMP:20160109T114833Z
        // TRANSP:TRANSPARENT
        // CATEGORIES: Marvel's Agent Carter Episodes, TV Shows
        // END:VEVENT

        // logger.info("  {}", ToStringBuilder.reflectionToString(issue, ToStringStyle.MULTI_LINE_STYLE));

        // logger.info("  {}", ToStringBuilder.reflectionToString(wikiPage, ToStringStyle.MULTI_LINE_STYLE));

        result.setUid(wikiPage.getSeriesPrefix() + wikiPage.getIssueNumber());
        result.setSummary(wikiPage.getSeriesPrefix() + wikiPage.getIssueNumber() + ": " + wikiPage.getFullPageTitle());
        int startOffset = issue.getSeries().getSourcePrefix().length() * 2 * 5;
        result.setDateStart(issue.getReleaseDate().plusMinutes(startOffset).toDate());
        result.setDateEnd(issue.getReleaseDate().plusMinutes(startOffset + 5).toDate());

        String seriesName = issue.getSeries().getClass().getSimpleName();
        seriesName = seriesName.replace("Series", "");
        seriesName = seriesName.replace("PerryRhodan", "Perry Rhodan ");
        seriesName = seriesName.trim();

        if ("Perry Rhodan".equals(seriesName))
        {
            seriesName = seriesName + " EA";
        }

        result.addCategories(new Categories(seriesName));

        result.addCategories(new Categories("Perry Rhodan"));

        // logger.info("  {}", ToStringBuilder.reflectionToString(result, ToStringStyle.MULTI_LINE_STYLE));

        return result;
    }


}
