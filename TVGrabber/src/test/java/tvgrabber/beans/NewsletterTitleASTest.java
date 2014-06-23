package tvgrabber.beans;

import com.sun.org.apache.xml.internal.serializer.utils.SerializerMessages_en;
import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("testing")


public class NewsletterTitleASTest extends CamelTestSupport {
    private Exchange original;
    private Exchange resource;

    @Autowired
    NewsletterTitleAS aggre;

    @Test
    public void aggregateOK()
    {
        Series SerieFirst=new Series();
        SerieFirst.setTitle("Scrubs");
        SerieFirst.setStart(new Date());
        SerieFirst.setStop(new Date());

        String sNewsletter= "$" + SerieFirst.getTitle() + "$" + '\n';
        sNewsletter+=SerieFirst.getStart() + " - " + SerieFirst.getStop() + " ## Rating ## " + '\n';
        Series SerieNew = new Series();
        SerieNew.setTitle("Scrubs");
        SerieNew.setStart(new Date());
        SerieNew.setStart(new Date());

        original.getIn().setBody(sNewsletter);
        resource.getIn().setBody(SerieNew);

        Exchange result = aggre.aggregate(original, resource);

        System.out.println("Testausgabe: " + result.getIn().getBody(String.class));
    }
}
