package tvgrabber.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.commons.lang.StringUtils;
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
import tvgrabber.entities.Comment;
import tvgrabber.entities.Series;
import twitter4j.TwitterException;

import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by patrickgrutsch on 28.05.14.
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {TestConfig.class, TwitterRouteTest.SpecificTestConfig.class},
        loader = CamelSpringDelegatingTestContextLoader.class
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testing")
public class TwitterRouteTest extends CamelTestSupport {

    private static final Logger logger = Logger.getLogger(TwitterRouteTest.class);


    private @PropertyInject("twitter.seda") String twitter;

    @Autowired
    private TwitterRoute twitterRoute;

    @Autowired
    private CamelContext camelContext; /* SpringCamelContext from TestConfig */

    @Configuration
    public static class SpecificTestConfig extends SingleRouteCamelConfiguration {
        @Autowired
        private TwitterRoute twitterRoute;

        @Bean
        public RouteBuilder route() {
            return twitterRoute;
        }
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return twitterRoute;
    }

    /* reuse TestConfig's SpringCamelContext to avoid creation of a DefaultCamelContext() by CamelTestSupport */
    @Override
    protected CamelContext createCamelContext() throws Exception {
        return camelContext;
    }


    /**
     * TESTS
     */

    @Test
    public void testSendingSeries() throws Exception {

        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("twitter:*")  //use wildcard because of parameters
                        .skipSendToOriginalEndpoint()
                        .to("mock:twitter");
            }
        });

        context.start();

//        getMockEndpoint("mock:" + twitterAccess).expectedMessageCount(0);
        getMockEndpoint("mock:twitter").expectedMessageCount(1);
        getMockEndpoint("mock:twitter").expectedBodiesReceived("blub1");

        Series series = new Series();
        series.setTitle("blub1");
        series.setStart(new Date());
        series.setStop(new Date());

        template.sendBody(twitter, series);

        assertMockEndpointsSatisfied();

        context.stop();
    }

    @Test
    public void testSendingComment() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("twitter:*")
                        .skipSendToOriginalEndpoint().to("mock:twitter");
            }
        });
        context.start();

        getMockEndpoint("mock:twitter").expectedMessageCount(1);
        getMockEndpoint("mock:twitter").expectedBodiesReceived("blub for blab");

        Comment comment = new Comment();
        comment.setComment("blub");
        Series series = new Series();
        series.setTitle("blab");
        comment.setTvprogram(series);

        template.sendBodyAndHeader(twitter, comment, "type", "comment");

        assertMockEndpointsSatisfied();

        context.stop();
    }

    @Test
    public void testSendingOversizedComment() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("twitter:*")
                        .skipSendToOriginalEndpoint().to("mock:twitter");
            }
        });
        context.start();

        String filled = StringUtils.repeat("*", 138);

        getMockEndpoint("mock:twitter").expectedMessageCount(1);
        getMockEndpoint("mock:twitter").expectedBodiesReceived(filled + " f");

        Comment comment = new Comment();
        comment.setComment(filled);
        Series series = new Series();
        series.setTitle("blab");
        comment.setTvprogram(series);

        template.sendBodyAndHeader(twitter, comment, "type", "comment");

        assertMockEndpointsSatisfied();

        context.stop();
    }

    @Test
    public void testExceptionHandlingSucc() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("twitter:*")
                        .skipSendToOriginalEndpoint().throwException(new TwitterException("testException"))
                        .throwException(new TwitterException("testException")).throwException(new TwitterException("testException"))
                        .to("mock:twitter");
            }
        });
        context.start();

        getMockEndpoint("mock:twitter").expectedMessageCount(1);
        getMockEndpoint("mock:twitter").expectedBodiesReceived("test");

        Series series = mock(Series.class);
        when(series.getTitle())
                .thenReturn("test");

        template.sendBody(twitter, series);

        assertMockEndpointsSatisfied();

        context.stop();
    }

    @Test
    public void testExceptionHandlingFail() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("twitter:*")
                        .skipSendToOriginalEndpoint().throwException(new TwitterException("testException"))
                        .throwException(new TwitterException("testException")).throwException(new TwitterException("testException"))
                        .throwException(new TwitterException("testException"))
                        .to("mock:twitter");
            }
        });
        context.start();

        getMockEndpoint("mock:twitter").expectedMessageCount(0);

        Series series = mock(Series.class);
        when(series.getTitle())
                .thenReturn("test");

        template.sendBody(twitter, series);

        assertMockEndpointsSatisfied();

        context.stop();
    }


}
