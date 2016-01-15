package com.gurkensalat.calendar.perrypedia.releasecalendar;

import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.text.ICalWriter;
import biweekly.property.ProductId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

@Component
public class ICalendarUtil
{
    private static final Logger logger = LoggerFactory.getLogger(ICalendarUtil.class);

    @Autowired
    private Environment environment;

    @Value("${info.build.artifact}")
    private String projectArtifact;

    @Value("${info.build.version}")
    private String projectVersion;

    public void saveIcal(Map<String, VEvent> events, String calendar)
    {
        ICalendar ical = new ICalendar();

        ical.setProductId(new ProductId("-//Hakan Tandogan//" + projectArtifact + " " + projectVersion + "//EN"));

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
