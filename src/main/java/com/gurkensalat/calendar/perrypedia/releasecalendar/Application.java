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
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

@SpringBootApplication
@EntityScan
@EnableJpaRepositories
public class Application
{
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private Environment environment;

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner demo(WikiPageRepository wikiPageRepository) throws Exception
    {
        logger.info("demo method called...");

        Series classic = new PerryRhodanSeries();
        // logger.info("Series {}", classic);

        Series neo = new PerryRhodanNeoSeries();
        // logger.info("Series {}", neo);

        Series neoStory = new PerryRhodanNeoStorySeries();
        // logger.info("Series {}", neoStory);

        Series arkon = new PerryRhodanArkonSeries();
        // logger.info("Series {}", arkon);

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
