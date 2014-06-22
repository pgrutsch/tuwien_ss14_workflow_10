package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
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
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 22.06.2014
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = {TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("testing")
public class IMDBRatingAggregationStrategyTest extends CamelTestSupport {

    private String myBody;
    private Map<String, Object> header;
    private Exchange original;
    private Exchange resource;

    @Autowired
    IMDBRatingAggregationStrategy aggregationStrat;

    @Before
    public void setUp() {
        context = new DefaultCamelContext();
        original = new DefaultExchange(context);
        resource = new DefaultExchange(context);

    }

    @Test
    public void aggregateTestOK() {
        Series s = new Series();
        s.setTitle("Californication");
        s.setStart(new Date());
        s.setStop(new Date());

        original.getIn().setBody(s);
        resource.getIn().setBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root response=\"True\"><movie title=\"Californication\" year=\"2007–\" rated=\"TV-MA\" released=\"13 Aug 2007\" runtime=\"28 min\" genre=\"Comedy, Drama\" director=\"N/A\" writer=\"Tom Kapinos\" actors=\"David Duchovny, Natascha McElhone, Evan Handler, Pamela Adlon\" plot=\"A self-loathing, alcoholic writer attempts to repair his damaged relationships with his daughter and her mother while combating sex addiction, a budding drug problem, and the seeming inability to avoid making bad decisions.\" language=\"English\" country=\"USA\" awards=\"Won 1 Golden Globe. Another 5 wins &amp; 25 nominations.\" poster=\"http://ia.media-imdb.com/images/M/MV5BMjAyMDM2ODExNF5BMl5BanBnXkFtZTgwNTI2MjkzMTE@._V1_SX300.jpg\" metascore=\"N/A\" imdbRating=\"8.5\" imdbVotes=\"112,625\" imdbID=\"tt0904208\" type=\"series\"/></root>");

        Exchange result = aggregationStrat.aggregate(original, resource);
        Series sres = result.getIn().getBody(Series.class);

        log.info("Series "+ sres.getTitle() + " was enriched with IMDBRating "+ sres.getImdbRating());
        assertEquals(8.5, sres.getImdbRating(), 0);
    }


    @Test
    public void aggregateTestInvalidXML(){
        Series s = new Series();
        s.setTitle("Californication");
        s.setStart(new Date());
        s.setStop(new Date());

        original.getIn().setBody(s);
        resource.getIn().setBody("");

        Exchange result = aggregationStrat.aggregate(original, resource);
        Series sres = result.getIn().getBody(Series.class);

        assertEquals(0.0, sres.getImdbRating(), 0); // Invalid (empty) XML -> IMDB Rating still not set
    }

    public void aggregateTestSeriesNotFound() {
        Series s = new Series();
        s.setTitle("Californication");
        s.setStart(new Date());
        s.setStop(new Date());

        original.getIn().setBody(s);
        resource.getIn().setBody("<root response=\"False\"><error>Movie not found!</error></root>");

        Exchange result = aggregationStrat.aggregate(original, resource);
        Series sres = result.getIn().getBody(Series.class);

        assertEquals(0.0, sres.getImdbRating(), 0); // IMDB Rating still not set
    }
}
