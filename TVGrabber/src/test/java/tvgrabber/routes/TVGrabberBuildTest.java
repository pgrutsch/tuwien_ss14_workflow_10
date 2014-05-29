package tvgrabber.routes;

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
import tvgrabber.webservice.soap.SOAPComment;


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

    /**
     * BASIC TEST SETUP
     */

    private static final Logger logger = Logger.getLogger(TVGrabberBuildTest.class);

    /**
     * WATCH OUT for variable name. if you use eg. tvGrabberBuild, there will be an uniquebeanidentifier exception!!!
     */
    @Autowired
    private TVGrabberBuild TVGrabberBuild; /* needed for createRouteBuilder() */

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

    /**
     * TESTS
     */

    @Test
    public void testIMDBRatingEnrichment() throws Exception {
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

                interceptSendToEndpoint("jpa:*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:result");
            }
        });

        context.start();

        Series s = new Series();
        s.setTitle("Um Himmels Willen");

        assertEquals(s.getImdbRating(), 0.0, 0); // Initial value should be zero

        /* Not yet working: Message should be in mock:result in the end
        template.sendBody("seda:waitingForEnrichment", s);

        System.out.println("WFE: "+ getMockEndpoint("mock:waitingForEnrichment").getExchanges().isEmpty());
        System.out.println("RES: "+ getMockEndpoint("mock:result").getExchanges().isEmpty());

        Double imdbRating = 0.0; //getMockEndpoint("mock:result").getExchanges().get(0).getIn().getBody(Series.class).getImdbRating();
        log.info("Series "+ s.getTitle() + " was enriched with IMDBRating "+ imdbRating);
        assertTrue(0.0 <= imdbRating && imdbRating <= 10.0);

        getMockEndpoint("mock:seda:waitingForEnrichment").expectedMessageCount(0);
        getMockEndpoint("mock:result").expectedMessageCount(0);

        assertMockEndpointsSatisfied();
        */
    }


    /*
     * TODO: Test Invalid XML File
     */
}