package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

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
        logger.info("Series {}", classic);

        Series neo = new PerryRhodanNeoSeries();
        logger.info("Series {}", neo);

        Series neoStory = new PerryRhodanNeoStorySeries();
        logger.info("Series {}", neoStory);

        Series arkon = new PerryRhodanArkonSeries();
        logger.info("Series {}", arkon);
    }
}
