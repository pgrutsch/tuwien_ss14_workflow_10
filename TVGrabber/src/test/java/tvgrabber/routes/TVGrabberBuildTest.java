package tvgrabber.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import tvgrabber.TestConfig;
import tvgrabber.entities.Series;

import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 29.05.14
 * Time: 12:37
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {TestConfig.class, TVGrabberBuildTest.SpecificTestConfig.class}, /* define both configs for route tests */
        loader = CamelSpringDelegatingTestContextLoader.class /* IMPORTANT: Use delegation loader */
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testing")
public class TVGrabberBuildTest extends CamelTestSupport {

    private static final Logger logger = Logger.getLogger(TVGrabberBuildTest.class);

    private @PropertyInject("build.enrichmentQueue") String waitingForEnrichment;
    private @PropertyInject("buildtest.file") String guideXMLBuildTest;

    private @PropertyInject("socialMedia.seda") String socialMedia;
    private @PropertyInject("twitter.seda") String twitter;
    private @PropertyInject("facebook.seda") String facebook;


    @Autowired
    private TVGrabberBuild TVGrabberBuild; /* needed for createRouteBuilder() */

    @Autowired
    private CamelContext camelContext; /* SpringCamelContext from TestConfig */

    @Configuration
    public static class SpecificTestConfig extends SingleRouteCamelConfiguration {

        @Autowired
        private TVGrabberBuild TVGrabberBuild; /* needed for route() method */

        @Bean
        public RouteBuilder route() { /* limits the test class environment to use only one routeBuilder */
            return TVGrabberBuild;
        }
    }

    @Override
    public boolean isUseAdviceWith() { /* defines that tests run with adviceWith */
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception { /* returns the route used for the adviceWith tests */
        return TVGrabberBuild;
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        return camelContext;
    }

    /**
     * TESTS
     */

    @Test
    public void testXMLReader() throws Exception {
        logger.debug("testXMLReader - consuming from ROUTE: "+ guideXMLBuildTest);

        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith(guideXMLBuildTest);

                interceptSendToEndpoint(waitingForEnrichment)
                        .skipSendToOriginalEndpoint()
                        .to("mock:result");
            }
        });

        context.start();

        // Expecting 1 message because content filter checks for "isSeries"
        getMockEndpoint("mock:result").expectedMessageCount(1);

        assertMockEndpointsSatisfied();

        Series s = getMockEndpoint("mock:result").getExchanges().get(0).getIn().getBody(Series.class);

        assertEquals("Columbo", s.getTitle());
        assertEquals("ORF 2", s.getChannel());

        context.stop();
    }

    @Test
    public void testIMDBRatingEnrichment() throws Exception {
        logger.debug("Route0="+ context.getRouteDefinitions().get(0).toString());
        logger.debug("Route1="+ context.getRouteDefinitions().get(1).toString());

        // Do not load guide.xml file
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:emptyQueue");
            }
        });

        // Set custom queue for enrichTest
        context.getRouteDefinitions().get(1).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                //replaceFromWith("seda:enrichTest");

                interceptSendToEndpoint("jpa://*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:result");
            }
        });

        context.start();

        getMockEndpoint("mock:result").expectedMessageCount(1);

        Series s = new Series();
        s.setTitle("Um Himmels Willen");
        s.setStart(new Date());
        s.setStop(new Date());
        assertEquals(s.getImdbRating(), 0.0, 0); // Initial value should be zero

        template.sendBody(waitingForEnrichment, s);

        assertMockEndpointsSatisfied();

        logger.debug("RES size: " + getMockEndpoint("mock:result").getExchanges().size());

        Series sres = getMockEndpoint("mock:result").getExchanges().get(0).getIn().getBody(Series.class);
        //log.debug("Series "+ sres.getTitle() + " was enriched with IMDBRating "+ sres.getImdbRating());
        assertTrue(0.0 <= sres.getImdbRating() && sres.getImdbRating() <= 10.0);

        context.stop();
    }

    @Test
    public void filterNonExistingSeriesForSocialMedia() throws Exception {
        context.getRouteDefinitions().get(3).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith(socialMedia);

                interceptSendToEndpoint(twitter)
                        .skipSendToOriginalEndpoint()
                        .to("mock:twitter");

                interceptSendToEndpoint(facebook)
                        .skipSendToOriginalEndpoint()
                        .to("mock:facebook");
            }
        });
        getMockEndpoint("mock:twitter").expectedMessageCount(1);
        getMockEndpoint("mock:facebook").expectedMessageCount(1);

        Series s = new Series();
        s.setTitle("new one");

        template.sendBody(socialMedia, s);
        assertMockEndpointsSatisfied();
        context.stop();
    }

    @Test
    public void filterExistingSeriesForSocialMedia() throws Exception {
        context.getRouteDefinitions().get(3).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith(socialMedia);

                interceptSendToEndpoint(twitter)
                        .skipSendToOriginalEndpoint()
                        .to("mock:twitter");

                interceptSendToEndpoint(facebook)
                        .skipSendToOriginalEndpoint()
                        .to("mock:facebook");
            }
        });
        getMockEndpoint("mock:twitter").expectedMessageCount(0);
        getMockEndpoint("mock:facebook").expectedMessageCount(0);

        Series s = new Series();
        s.setTitle("test1");

        template.sendBody(socialMedia, s);
        assertMockEndpointsSatisfied();
        context.stop();
    }
}