package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
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
        logger.info("spring-boot command line runner main...");
        logger.info(environment.getProperty("foo"));
        logger.info("spring-boot command line runner main end...");
    }
}
