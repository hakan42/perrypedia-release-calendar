package com.gurkensalat.calendar.perrypedia.releasecalendar;

import biweekly.component.VEvent;
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
import org.apache.http.util.VersionInfo;
import org.joda.time.DateTime;
import org.mediawiki.xml.export_0.MediaWikiType;
import org.mediawiki.xml.export_0.PageType;
import org.mediawiki.xml.export_0.RevisionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private PersistenceContext persistenceContext;

    @Autowired
    private WikiPageRepository wikiPageRepository;

    @Autowired
    private EventUtil eventUtil;

    @Autowired
    private ICalendarUtil iCalendarUtil;

    @Value("${info.build.artifact}")
    private String projectArtifact;

    @Value("${info.build.version}")
    private String projectVersion;

    @Value("${sleep:30}")
    private int sleepBetweenIssues;

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner work() throws Exception
    {
        // first, calculate which issues we need to check
        DateTime start = DateTime.now().minusDays(14).withMillisOfDay(0);
        DateTime end = DateTime.now().plusDays(60).withMillisOfDay(0);

        int year = DateTime.now().getYear();

        List<Issue> issuesToCheck = new ArrayList<Issue>();

        // check Perry Rhodan Classic first
        Series perryRhodanSeries = new PerryRhodanSeries();
        Map<String, VEvent> perryRhodanEvents = new TreeMap<String, VEvent>();
        {
            int perryRhodanYearBeginIssue = 2785 + 53 * (year - 2015);
            int startIssue = perryRhodanYearBeginIssue - 20;
            int endIssue = perryRhodanYearBeginIssue + 70;
            logger.debug("Calculating main series issues for main series from {} to {}", startIssue, endIssue);
            List<Issue> issues = calculateIssues(perryRhodanSeries, startIssue, endIssue, start, end);
            logger.debug("Scanning main series issues from {} to {}", issues.get(0).getNumber(), issues.get(issues.size() - 1).getNumber());
            issuesToCheck.addAll(issues);
        }

        // check Perry Rhodan NEO next
        Series perryRhodanNeoSeries = new PerryRhodanNeoSeries();
        Map<String, VEvent> perryRhodanNeoEvents = new TreeMap<String, VEvent>();
        {
            int perryRhodanNeoYearBeginIssue = 86 + 26 * (year - 2015);
            int startIssue = perryRhodanNeoYearBeginIssue - 10;
            int endIssue = perryRhodanNeoYearBeginIssue + 40;
            logger.debug("Calculating main series issues for main series from {} to {}", startIssue, endIssue);
            List<Issue> issues = calculateIssues(perryRhodanNeoSeries, startIssue, endIssue, start, end);
            logger.debug("Scanning main series issues from {} to {}", issues.get(0).getNumber(), issues.get(issues.size() - 1).getNumber());
            issuesToCheck.addAll(issues);
        }

        // check Perry Rhodan NEO Story next
        Series perryRhodanNeoStorySeries = new PerryRhodanNeoStorySeries();
        Map<String, VEvent> perryRhodanNeoStoryEvents = new TreeMap<String, VEvent>();
        issuesToCheck.addAll(calculateIssues(perryRhodanNeoStorySeries, 1, 12, start, end));

        // check Perry Rhodan Arkon next
        Series perryRhodanArkonSeries = new PerryRhodanArkonSeries();
        Map<String, VEvent> perryRhodanArkonEvents = new TreeMap<String, VEvent>();
        issuesToCheck.addAll(calculateIssues(perryRhodanArkonSeries, 1, 12, start, end));

        // check Perry Rhodan Jupiter next
        Series perryRhodanJupiterSeries = new PerryRhodanJupiterSeries();
        Map<String, VEvent> perryRhodanJupiterEvents = new TreeMap<String, VEvent>();
        issuesToCheck.addAll(calculateIssues(perryRhodanJupiterSeries, 1, 12, start, end));

        // check Perry Rhodan Terminus next
        Series perryRhodanTerminusSeries = new PerryRhodanTerminusSeries();
        Map<String, VEvent> perryRhodanTerminusEvents = new TreeMap<String, VEvent>();
        issuesToCheck.addAll(calculateIssues(perryRhodanTerminusSeries, 1, 12, start, end));

        // check Perry Rhodan Olymp next
        Series perryRhodanOlympSeries = new PerryRhodanOlympSeries();
        Map<String, VEvent> perryRhodanOlympEvents = new TreeMap<String, VEvent>();
        issuesToCheck.addAll(calculateIssues(perryRhodanOlympSeries, 1, 12, start, end));

        // Now, to the Perrypedia checks...
        logger.info("Sleeping {} seconds between checks", sleepBetweenIssues);
        Map<String, VEvent> allEvents = new TreeMap<String, VEvent>();
        for (Issue issue : issuesToCheck)
        {
            WikiPage wikiPage = checkIssueOnPerryPedia(issue);
            if (WikiPage.getVALID().equals(wikiPage.getFullPageValid()))
            {
                VEvent event = eventUtil.convertToIcalEvent(issue, wikiPage);
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

                    if (perryRhodanJupiterSeries.getSourcePrefix().equals(issue.getSeries().getSourcePrefix()))
                    {
                        perryRhodanJupiterEvents.put(key, event);
                    }

                    if (perryRhodanTerminusSeries.getSourcePrefix().equals(issue.getSeries().getSourcePrefix()))
                    {
                        perryRhodanTerminusEvents.put(key, event);
                    }

                    if (perryRhodanOlympSeries.getSourcePrefix().equals(issue.getSeries().getSourcePrefix()))
                    {
                        perryRhodanOlympEvents.put(key, event);
                    }
                }
            }

            Thread.sleep(sleepBetweenIssues * 1000);
        }

        persistenceContext.exportDatabase();

        // Finally, create the iCal file
        iCalendarUtil.saveIcal(allEvents, "All");
        iCalendarUtil.saveIcal(perryRhodanEvents, perryRhodanSeries.getSourcePrefix());
        iCalendarUtil.saveIcal(perryRhodanNeoEvents, perryRhodanNeoSeries.getSourcePrefix());
        iCalendarUtil.saveIcal(perryRhodanNeoStoryEvents, perryRhodanNeoStorySeries.getSourcePrefix());
        iCalendarUtil.saveIcal(perryRhodanArkonEvents, perryRhodanArkonSeries.getSourcePrefix());
        iCalendarUtil.saveIcal(perryRhodanJupiterEvents, perryRhodanJupiterSeries.getSourcePrefix());
        iCalendarUtil.saveIcal(perryRhodanTerminusEvents, perryRhodanTerminusSeries.getSourcePrefix());
        iCalendarUtil.saveIcal(perryRhodanOlympEvents, perryRhodanOlympSeries.getSourcePrefix());

        return null;
    }

    private List<Issue> calculateIssues(Series series, int startIssue, int endIssue, DateTime start, DateTime end)
    {
        List<Issue> result = new ArrayList<Issue>();

        for (int i = startIssue; i <= endIssue; i++)
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


        final String EXPORT_URL = "https://www.perrypedia.proc.org/mediawiki/index.php?title=Spezial:Exportieren&action=submit";

        String userAgent = projectArtifact + "/" + projectVersion;
        userAgent = userAgent + " " + VersionInfo.getUserAgent("Apache-HttpClient", "org.apache.http.client", getClass());

        CloseableHttpClient httpclient = HttpClients.custom().setUserAgent(userAgent).build();

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
}
