package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tvgrabber.TestConfig;
import tvgrabber.entities.Series;

import java.util.Date;

/**
 * Created by Isabella on 20.06.2014.
 */


@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = {TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testing")
public class NewsletterTitleASTest extends CamelTestSupport {

    @Autowired
    private NewsletterTitleAS aggre;

    private Exchange original, resource;

    @Before
    public void setUp() {
        this.resource = new DefaultExchange(context);
        this.original = new DefaultExchange(context);
    }

    @Test
    public void aggregate_shouldAggregateOneSeries() {
        Series SerieFirst=new Series();
        SerieFirst.setTitle("Scrubs");
        SerieFirst.setStart(new Date(1412892000000L));
        SerieFirst.setStop(new Date(1412892000000L));
        SerieFirst.setImdbRating(2.0);
        SerieFirst.setChannel("o1");

        resource.getIn().setBody(SerieFirst);
        resource.getIn().setHeader("title", SerieFirst.getTitle());

        Exchange result = aggre.aggregate(null, resource);

        assertEquals("Scrubs", result.getIn().getHeader("title"));
        String body = "$Scrubs$\n" +
                "Fri Oct 10 00:00:00 CEST 2014 - Fri Oct 10 00:00:00 CEST 2014 ## Rating - 2.0 ## ** Channel - o1 **\n";
        assertEquals(body, result.getIn().getBody(String.class));
    }

    @Test
    public void aggregate_shouldAggregateSeriesToExistingSeries() {
        Series SerieSec=new Series();
        SerieSec.setTitle("Scrubs");
        SerieSec.setStart(new Date(1412892000000L));
        SerieSec.setStop(new Date(1412892000000L));
        SerieSec.setImdbRating(2.0);
        SerieSec.setChannel("o2");

        resource.getIn().setBody(SerieSec);
        resource.getIn().setHeader("title", SerieSec.getTitle());

        String exBody = "$Scrubs$\n" +
                "Fri Oct 10 00:00:00 CEST 2014 - Fri Oct 10 00:00:00 CEST 2014 ## Rating - 2.0 ## ** Channel - o1 **\n";

        original.getIn().setBody(exBody);
        original.getIn().setHeader("title", "Scrubs");

        Exchange result = aggre.aggregate(original, resource);

        assertEquals("Scrubs", result.getIn().getHeader("title"));

        String body = "$Scrubs$\n" +
                "Fri Oct 10 00:00:00 CEST 2014 - Fri Oct 10 00:00:00 CEST 2014 ## Rating - 2.0 ## ** Channel - o1 **\n" +
                "Fri Oct 10 00:00:00 CEST 2014 - Fri Oct 10 00:00:00 CEST 2014 ## Rating - 2.0 ## ** Channel - o2 **\n";
        assertEquals(body, result.getIn().getBody(String.class));
    }

}
