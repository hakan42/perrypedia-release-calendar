package com.gurkensalat.calendar.perrypedia.releasecalendar;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.h2.tools.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class PersistenceContext
{
    private static final Logger logger = LoggerFactory.getLogger(PersistenceContext.class);

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.connectionTestQuery}")
    private String connectionTestQuery;

    @Bean(destroyMethod = "close")
    public DataSource dataSource()
    {
        Properties dsProps = new Properties();
        dsProps.setProperty("url", dataSourceUrl);
        dsProps.setProperty("user", user);
        dsProps.setProperty("password", password);

        Properties configProps = new Properties();
        configProps.setProperty("connectionTestQuery", connectionTestQuery);
        configProps.setProperty("driverClassName", driverClassName);
        configProps.setProperty("jdbcUrl", dataSourceUrl);

        HikariConfig hc = new HikariConfig(configProps);
        hc.setDataSourceProperties(dsProps);
        return new HikariDataSource(hc);
    }

    public void exportDatabase()
    {
        if ("org.h2.Driver".equals(driverClassName))
        {
            try
            {
                Connection connection = dataSource().getConnection();

                String filename = dataSourceUrl.substring("jdbc:h2:".length()) + ".sql";
                String option1 = "SIMPLE";
                String option2 = "";
                Script.process(connection, filename, option1, option2);
            }
            catch (Exception e)
            {
                logger.error("While exporting database", e);
            }
        }
        else
        {
            logger.info("No H2 database, do not know how to export database");
        }
    }
}
