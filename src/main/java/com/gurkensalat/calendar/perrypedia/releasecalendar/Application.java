package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.mediawiki.xml.export_0.MediaWikiType;
import org.mediawiki.xml.export_0.PageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

@SpringBootApplication
public class Application implements CommandLineRunner
{
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private Environment environment;

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

    public void run(String... strings) throws Exception
    {
        Series classic = new PerryRhodanSeries();
        // logger.info("Series {}", classic);

        Series neo = new PerryRhodanNeoSeries();
        // logger.info("Series {}", neo);

        Series neoStory = new PerryRhodanNeoStorySeries();
        // logger.info("Series {}", neoStory);

        Series arkon = new PerryRhodanArkonSeries();
        // logger.info("Series {}", arkon);

        // http://www.perrypedia.proc.org/mediawiki/index.php?title=Quelle:PR2837&redirect=no
        // MediaWikiBot wikiBot = new MediaWikiBot("http://www.perrypedia.proc.org/w/");
        // Article article = wikiBot.getArticle("Quelle:PR2837");
        // logger.info(article.getText().substring(5, 42));

        // MediaWikiBot wikiBot = new MediaWikiBot("https://en.wikipedia.org/w/");
        // Article article = wikiBot.getArticle("42");
        // logger.info(article.getText().substring(5, 42));
        // HITCHHIKER'S GUIDE TO THE GALAXY FANS
        // applyChangesTo(article);
        // wikiBot.login("user", "***");
        // article.save();

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

            // MediaWikiType mwt = (MediaWikiType) unmarshaller.unmarshal(new StringReader(data));

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
    }
}
