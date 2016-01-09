package com.gurkensalat.calendar.perrypedia.releasecalendar;

import biweekly.Biweekly;
import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.text.ICalWriter;
import biweekly.property.Categories;
import biweekly.property.ProductId;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.mediawiki.xml.export_0.MediaWikiType;
import org.mediawiki.xml.export_0.PageType;
import org.mediawiki.xml.export_0.RevisionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SpringBootApplication
@EntityScan
@EnableJpaRepositories
public class Application
{
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static final String MACRO_PREFIX_ROMAN = "{{Roman";
    private static final String MACRO_PREFIX_HANDLUNGSZUSAMMENFASSUNG = "{{Handlungszusammenfassung";
    private static final String MACRO_POSTFIX = "}}";

    @Autowired
    private Environment environment;

    @Autowired
    private WikiPageRepository wikiPageRepository;

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner work() throws Exception
    {
        // first, calculate which issues we need to check

        DateTime start = DateTime.now().minusDays(7).withMillisOfDay(0);
        DateTime end = DateTime.now().plusDays(60).withMillisOfDay(0);

        List<Issue> issuesToCheck = new ArrayList<Issue>();

        // check Perry Rhodan Classic first
        Series perryRhodanSeries = new PerryRhodanSeries();
        Map<String, VEvent> perryRhodanEvents = new TreeMap<String, VEvent>();
        issuesToCheck.addAll(calculateIssues(perryRhodanSeries, 2838, 2860, start, end));

        // check Perry Rhodan NEO next
        Series perryRhodanNeoSeries = new PerryRhodanNeoSeries();
        Map<String, VEvent> perryRhodanNeoEvents = new TreeMap<String, VEvent>();
        issuesToCheck.addAll(calculateIssues(perryRhodanNeoSeries, 110, 120, start, end));

        // check Perry Rhodan NEO Story next
        Series perryRhodanNeoStorySeries = new PerryRhodanNeoStorySeries();
        Map<String, VEvent> perryRhodanNeoStoryEvents = new TreeMap<String, VEvent>();
        issuesToCheck.addAll(calculateIssues(perryRhodanNeoStorySeries, 1, 12, start, end));

        // check Perry Rhodan Arkon next
        Series perryRhodanArkonSeries = new PerryRhodanArkonSeries();
        Map<String, VEvent> perryRhodanArkonEvents = new TreeMap<String, VEvent>();
        issuesToCheck.addAll(calculateIssues(perryRhodanArkonSeries, 1, 12, start, end));

        // Now, to the Perrypedia checks...
        Map<String, VEvent> allEvents = new TreeMap<String, VEvent>();
        for (Issue issue : issuesToCheck)
        {
            WikiPage wikiPage = checkIssueOnPerryPedia(issue);
            if (WikiPage.getVALID().equals(wikiPage.getFullPageValid()))
            {
                VEvent event = convertToIcalEvent(issue, wikiPage);
                if (event != null)
                {
                    String key = issue.getReleaseDate().toString();
                    key = issue.getSeries().getSourcePrefix() + issue.getNumber();

                    allEvents.put(key, event);

                    if (perryRhodanSeries.getSourcePrefix().equals(issue.getSeries().getSourcePrefix()))
                    {
                        perryRhodanEvents.put(key, event);
                    }

                    if (perryRhodanNeoSeries.getSourcePrefix().equals(issue.getSeries().getSourcePrefix()))
                    {
                        perryRhodanNeoEvents.put(key, event);
                    }

                    if (perryRhodanNeoStorySeries.getSourcePrefix().equals(issue.getSeries().getSourcePrefix()))
                    {
                        perryRhodanNeoStoryEvents.put(key, event);
                    }

                    if (perryRhodanArkonSeries.getSourcePrefix().equals(issue.getSeries().getSourcePrefix()))
                    {
                        perryRhodanArkonEvents.put(key, event);
                    }
                }
            }
        }

        // Finally, create the iCal file
        saveIcal(allEvents, "All");
        saveIcal(perryRhodanEvents, perryRhodanSeries.getSourcePrefix());
        saveIcal(perryRhodanNeoEvents, perryRhodanNeoSeries.getSourcePrefix());
        saveIcal(perryRhodanNeoStoryEvents, perryRhodanNeoStorySeries.getSourcePrefix());
        saveIcal(perryRhodanArkonEvents, perryRhodanArkonSeries.getSourcePrefix());

        return null;
    }

    private List<Issue> calculateIssues(Series series, int startIssue, int endIssue, DateTime start, DateTime end)
    {
        List<Issue> result = new ArrayList<Issue>();

        for (int i = startIssue; i < endIssue; i++)
        {
            DateTime issueDate = series.getIssueReleaseDate(i);

            if (issueDate != null)
            {
                if (start.isBefore(issueDate))
                {
                    if (end.isAfter(issueDate))
                    {
                        Issue issue = new Issue(series, i);
                        result.add(issue);
                    }
                }
            }
        }

        return result;
    }

    private WikiPage checkIssueOnPerryPedia(Issue issue)
    {
        logger.info("Have to check issue {}", issue);

        WikiPage wikiPage = findFirstWikiPage(issue);
        // logger.debug("  Wiki Page is {}", wikiPage);

        if (wikiPage == null)
        {
            wikiPage = new WikiPage();
            wikiPage.setSeriesPrefix(issue.getSeries().getSourcePrefix());
            wikiPage.setIssueNumber(issue.getNumber());
        }

        // logger.debug("    before save {}", wikiPage);
        wikiPage = wikiPageRepository.save(wikiPage);
        // logger.debug("    after save {}", wikiPage);

        if (!(WikiPage.getVALID().equals(wikiPage.getSourcePageValid())))
        {
            try
            {
                wikiPage.setSourcePageTitle("Quelle:" + issue.getSeries().getSourcePrefix() + issue.getNumber());
                wikiPage = wikiPageRepository.save(wikiPage);

                MediaWikiType mwt = downloadAndDecode(wikiPage.getSourcePageTitle());
                if ((mwt.getPage() != null) && (mwt.getPage().size() > 0))
                {
                    PageType page = mwt.getPage().get(0);
                    logger.info("  page: {}", page);
                    logger.info("    id:    {}", page.getId());
                    logger.info("    title: {}", page.getTitle());
                    logger.info("    redir: {}", page.getRedirect().getTitle());

                    wikiPage.setSourcePageId(page.getId().toString());
                    wikiPage.setFullPageTitle(page.getRedirect().getTitle());

                    if (StringUtils.isNotEmpty(wikiPage.getSourcePageId()) && StringUtils.isNotEmpty(wikiPage.getSourcePageTitle()))
                    {
                        if (StringUtils.isNotEmpty(wikiPage.getFullPageTitle()))
                        {
                            wikiPage.setSourcePageValid(WikiPage.getVALID());
                        }
                    }

                    wikiPage = wikiPageRepository.save(wikiPage);
                }
            }
            catch (Exception e)
            {
                logger.error("While loading 'Quelle' page", e);
            }
        }

        if (WikiPage.getVALID().equals((wikiPage.getSourcePageValid())))
        {
            if (!(WikiPage.getVALID().equals(wikiPage.getFullPageValid())))
            {
                try
                {
                    MediaWikiType mwt = downloadAndDecode(wikiPage.getFullPageTitle());
                    if ((mwt.getPage() != null) && (mwt.getPage().size() > 0))
                    {
                        PageType page = mwt.getPage().get(0);
                        logger.info("  page: {}", page);
                        logger.info("    id:    {}", page.getId());

                        wikiPage.setFullPageId(page.getId().toString());
                        wikiPage.setFullPageTitle(page.getTitle());
                        wikiPage.setFullPageText(null);

                        if (StringUtils.isNotEmpty(wikiPage.getFullPageId()) && StringUtils.isNotEmpty(wikiPage.getFullPageId()))
                        {
                            if ((page.getRevisionOrUpload() != null) && (page.getRevisionOrUpload().size() > 0))
                            {
                                RevisionType revision = (RevisionType) page.getRevisionOrUpload().get(0);

                                String text = revision.getText().getValue();
                                int startMacroPrefixRoman = text.indexOf(MACRO_PREFIX_ROMAN);
                                if (startMacroPrefixRoman > -1)
                                {
                                    text = text.substring(startMacroPrefixRoman);

                                    int startMacroPostfix = text.indexOf(MACRO_POSTFIX);
                                    if (startMacroPostfix > -1)
                                    {
                                        text = text.substring(0, startMacroPostfix);

                                        wikiPage.setFullPageValid(WikiPage.getVALID());
                                        wikiPage.setFullPageText(text);
                                    }
                                }

                                int startMacroPrefixNeo = text.indexOf(MACRO_PREFIX_HANDLUNGSZUSAMMENFASSUNG);
                                if (startMacroPrefixNeo > -1)
                                {
                                    text = text.substring(startMacroPrefixNeo);

                                    int startMacroPostfix = text.indexOf(MACRO_POSTFIX);
                                    if (startMacroPostfix > -1)
                                    {
                                        text = text.substring(0, startMacroPostfix);

                                        wikiPage.setFullPageValid(WikiPage.getVALID());
                                        wikiPage.setFullPageText(text);
                                    }
                                }

                            }
                        }

                        // if (StringUtils.isNotEmpty(wikiPage.getFullPageText()))
                        // {
                        // wikiPage.setFullPageValid(wikiPage.getVALID());
                        // }

                        wikiPage = wikiPageRepository.save(wikiPage);
                    }
                }
                catch (Exception e)
                {
                    logger.error("While loading full page", e);
                }
            }
        }

        String wikiPageAsString = ToStringBuilder.reflectionToString(wikiPage, ToStringStyle.MULTI_LINE_STYLE);
        logger.info("wikiPage is {}", wikiPageAsString);

        return wikiPage;
    }

    private WikiPage findFirstWikiPage(Issue issue)
    {
        WikiPage wikiPage = null;
        List<WikiPage> wikiPages = wikiPageRepository.findBySeriesPrefixAndIssueNumber(issue.getSeries().getSourcePrefix(), issue.getNumber());

        if ((wikiPages != null) && (wikiPages.size() > 0))
        {
            wikiPage = wikiPages.get(0);
        }

        return wikiPage;
    }

    private MediaWikiType downloadAndDecode(String pageName) throws Exception
    {
        logger.debug("downloadAndDecode '{}'", pageName);

        // String url = "http://www.perrypedia.proc.org/mediawiki/index.php?title=Spezial:Exportieren&action=submit";

        // curl 'http://www.perrypedia.proc.org/mediawiki/index.php?title=Spezial:Exportieren&action=submit' \
        // --data 'catname=&pages=Quelle:PRN111&curonly=1&wpDownload=1'

        // curl 'http://www.perrypedia.proc.org/mediawiki/index.php?title=Spezial:Exportieren&action=submit' \
        // --data 'catname=&pages=Seid+ihr+wahres+Leben%3F&curonly=1&wpDownload=1'

        // curl 'http://www.perrypedia.proc.org/mediawiki/index.php?title=Spezial:Exportieren&action=submit' \
        // --data 'catname=&pages=Leticrons+S%C3%A4ule&curonly=1&wpDownload=1'

        // GEHT NICHT...
        // curl 'http://www.perrypedia.proc.org/mediawiki/index.php?title=Spezial:Exportieren&action=submit' \
        // --data 'catname=&pages=Leticrons+S%E4ule&curonly=1&wpDownload=1'


        final String EXPORT_URL = "http://www.perrypedia.proc.org/mediawiki/index.php?title=Spezial:Exportieren&action=submit";

        CloseableHttpClient httpclient = HttpClients.createDefault();
        // HttpGet httpGet = new HttpGet(url);
        HttpPost httpPost = new HttpPost(EXPORT_URL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("catname", ""));
        params.add(new BasicNameValuePair("pages", pageName));
        params.add(new BasicNameValuePair("curonly", "1"));
        params.add(new BasicNameValuePair("wpDownload", "1"));

        httpPost.setEntity(new UrlEncodedFormEntity(params, Charset.forName("UTF-8")));

        CloseableHttpResponse response1 = httpclient.execute(httpPost);

        MediaWikiType mwt = null;

        try
        {
            logger.debug("{}", response1.getStatusLine());

            HttpEntity entity1 = response1.getEntity();
            // logger.debug("{}", entity1.getContent());

            // do something useful with the response body
            String data = EntityUtils.toString(entity1);
            // logger.debug("{}", data);

            JAXBContext jaxbContext = JAXBContext.newInstance(MediaWikiType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            StreamSource source = new StreamSource(new StringReader(data));
            JAXBElement<MediaWikiType> userElement = unmarshaller.unmarshal(source, MediaWikiType.class);
            mwt = userElement.getValue();
            // logger.debug("Parsed Data: {}", mwt);

            // for (PageType page : mwt.getPage())
            // {
            // logger.debug("  page: {}", page);
            // logger.debug("    id:    {}", page.getId());
            // logger.debug("    title: {}", page.getTitle());
            // logger.debug("    redir: {}", page.getRedirect().getTitle());
            // }

            // and ensure it is fully consumed
            EntityUtils.consume(entity1);
        }
        finally
        {
            response1.close();
        }

        return mwt;
    }

    private VEvent convertToIcalEvent(Issue issue, WikiPage wikiPage)
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

    private void saveIcal(Map<String, VEvent> events, String calendar)
    {
        ICalendar ical = new ICalendar();
        // TODO obtain pom version somehow
        String projectVersion = "0.0.1-SNAPSHOT";
        // TODO obtain project artifact Id somehow
        String projectArtifactId = "perrypedia-release-calendar";

        ical.setProductId(new ProductId("-//Hakan Tandogan//" + projectArtifactId + " " + projectVersion + "//EN"));

        for (Map.Entry<String, VEvent> entry : events.entrySet())
        {
            // logger.info("Have to persist {}", entry.getKey());
            // logger.info("  Event is {}", ToStringBuilder.reflectionToString(entry.getValue(), ToStringStyle.MULTI_LINE_STYLE));
            ical.addEvent(entry.getValue());
        }

        try
        {
            File file = new File("PerryRhodan-" + calendar + ".ical");
            ICalWriter writer = null;
            try
            {
                writer = new ICalWriter(file, ICalVersion.V2_0);
                writer.write(ical);
            }
            finally
            {
                if (writer != null)
                {
                    writer.close();
                }
            }
        }
        catch (Exception e)
        {
            logger.error("While saving calendar", e);
        }
    }
}
