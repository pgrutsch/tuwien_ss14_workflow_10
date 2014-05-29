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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tvgrabber.TestConfig;
import tvgrabber.entities.Series;

import java.util.Date;

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

    private String twitterAccess = "twitter://timeline/user?consumerKey=K8WGu6kIxeipNv1pYPTA" +
            "&consumerSecret=eS6kq93pT8xnMgK4MKnUR5ilkFExALXrSWiuB1wEXv8" +
            "&accessToken=2289840392-NyRv99h6JGrIe5n5sqNOVZtAsy4BM603ET4X69m" +
            "&accessTokenSecret=hHgTntMJKqPyXf0NwVb7qqEyurZQgpeEMNPpeGbuYFy5H";

    @Autowired
    private TwitterRoute twitterRoute;

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

    @Test
    public void testIsUseAdviceWith() throws Exception {

        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                 interceptSendToEndpoint("twitter:*") /* use wildcard because of parameters */
                        .skipSendToOriginalEndpoint()
                        .to("mock:adviced");
            }
        });

        context.start();

        getMockEndpoint("mock:" + twitterAccess).expectedMessageCount(0);
        getMockEndpoint("mock:adviced").expectedMessageCount(1);
        getMockEndpoint("mock:adviced").expectedBodiesReceived("blub1");

        Series series = new Series();
        series.setTitle("blub1");
        series.setStart(new Date());
        series.setStop(new Date());

        template.sendBody("seda:twitter", series);

        assertMockEndpointsSatisfied();
    }

}
