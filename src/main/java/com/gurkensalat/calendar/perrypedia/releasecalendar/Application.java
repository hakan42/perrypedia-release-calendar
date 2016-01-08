package com.gurkensalat.calendar.perrypedia.releasecalendar;

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
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EntityScan
@EnableJpaRepositories
public class Application
{
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

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
        issuesToCheck.addAll(calculateIssues(new PerryRhodanSeries(), 2838, 2840, start, end));

        // check Perry Rhodan NEO next
        // issuesToCheck.addAll(calculateIssues(new PerryRhodanNeoSeries(), 110, 120, start, end));

        // check Perry Rhodan NEO Story next
        // issuesToCheck.addAll(calculateIssues(new PerryRhodanNeoStorySeries(), 1, 12, start, end));

        // check Perry Rhodan Arkon next
        // issuesToCheck.addAll(calculateIssues(new PerryRhodanArkonSeries(), 1, 12, start, end));

        // Now, to the Perrypedia checks...
        for (Issue issue : issuesToCheck)
        {
            checkIssueOnPerryPedia(issue);
        }

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

    private void checkIssueOnPerryPedia(Issue issue)
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

                        if (StringUtils.isNotEmpty(wikiPage.getFullPageId()) && StringUtils.isNotEmpty(wikiPage.getFullPageId()))
                        {
                            if ((page.getRevisionOrUpload() != null) && (page.getRevisionOrUpload().size() > 0))
                            {
                                RevisionType revision = (RevisionType) page.getRevisionOrUpload().get(0);
                                wikiPage.setFullPageText(revision.getText().getValue());
                            }
                        }

                        if (StringUtils.isNotEmpty(wikiPage.getFullPageText()))
                        {
                            wikiPage.setFullPageValid(wikiPage.getVALID());
                        }

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

    // @Bean
    public CommandLineRunner seriesCalculator() throws Exception
    {
        logger.info("seriesCalculator method called...");

        Series classic = new PerryRhodanSeries();
        logger.info("Series {}", classic);

        Series neo = new PerryRhodanNeoSeries();
        logger.info("Series {}", neo);

        Series neoStory = new PerryRhodanNeoStorySeries();
        logger.info("Series {}", neoStory);

        Series arkon = new PerryRhodanArkonSeries();
        logger.info("Series {}", arkon);

        return null;
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
            logger.debug("{}", data);

            JAXBContext jaxbContext = JAXBContext.newInstance(MediaWikiType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            StreamSource source = new StreamSource(new StringReader(data));
            JAXBElement<MediaWikiType> userElement = unmarshaller.unmarshal(source, MediaWikiType.class);
            mwt = userElement.getValue();
            logger.debug("Parsed Data: {}", mwt);

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
