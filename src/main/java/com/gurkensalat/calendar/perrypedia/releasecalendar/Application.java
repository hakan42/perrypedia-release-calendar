package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.mediawiki.xml.export_0.MediaWikiType;
import org.mediawiki.xml.export_0.PageType;
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
        issuesToCheck.addAll(calculateIssues(new PerryRhodanNeoSeries(), 110, 120, start, end));

        // check Perry Rhodan NEO Story next
        issuesToCheck.addAll(calculateIssues(new PerryRhodanNeoStorySeries(), 1, 12, start, end));

        // check Perry Rhodan Arkon next
        issuesToCheck.addAll(calculateIssues(new PerryRhodanArkonSeries(), 1, 12, start, end));

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

    // @Bean
    public CommandLineRunner downloadAndDecode() throws Exception
    {
        logger.info("downloadAndDecode method called...");

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://www.perrypedia.proc.org/wiki/Spezial:Exportieren/Quelle:PR2837");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);

        try
        {
            logger.info("{}", response1.getStatusLine());

            HttpEntity entity1 = response1.getEntity();
            logger.info("{}", entity1.getContent());

            // do something useful with the response body
            String data = EntityUtils.toString(entity1);
            logger.info("{}", data);

            JAXBContext jaxbContext = JAXBContext.newInstance(MediaWikiType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            StreamSource source = new StreamSource(new StringReader(data));
            JAXBElement<MediaWikiType> userElement = unmarshaller.unmarshal(source, MediaWikiType.class);
            MediaWikiType mwt = userElement.getValue();
            logger.info("Parsed Data: {}", mwt);

            for (PageType page : mwt.getPage())
            {
                logger.info("  page: {}", page);
                logger.info("    id:    {}", page.getId());
                logger.info("    title: {}", page.getTitle());
                logger.info("    redir: {}", page.getRedirect().getTitle());
            }

            // and ensure it is fully consumed
            EntityUtils.consume(entity1);
        }
        finally
        {
            response1.close();
        }

        return null;
    }
}
