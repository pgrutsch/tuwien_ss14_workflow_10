package tvgrabber.routes;

import facebook4j.FacebookException;
import org.apache.camel.CamelContext;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by LeBon on 09.06.14.
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {TestConfig.class, FaceBookRouteTest.SpecificTestConfig.class},
        loader = CamelSpringDelegatingTestContextLoader.class
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testing")
public class FaceBookRouteTest extends CamelTestSupport {

    private static final Logger logger = Logger.getLogger(FaceBookRouteTest.class);


    @Autowired
    private FacebookRoute facebookRoute;

    @Autowired
    private CamelContext camelContext;

    @Configuration
    public static class SpecificTestConfig extends SingleRouteCamelConfiguration{

        @Autowired
        private FacebookRoute facebookRoute;

        @Bean
        public RouteBuilder route() {
            return facebookRoute;
        }
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return facebookRoute;
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
                interceptSendToEndpoint("facebook:*")  //use wildcard because of parameters
                        .skipSendToOriginalEndpoint()
                        .to("mock:facebook");
            }
        });

        context.start();

        getMockEndpoint("mock:facebook").expectedMessageCount(1);
        getMockEndpoint("mock:facebook").expectedBodiesReceived("blub1");

        Series series = new Series();
        series.setTitle("blub1");
        series.setStart(new Date());
        series.setStop(new Date());

        template.sendBody("seda:facebook", series);

        assertMockEndpointsSatisfied();

        context.stop();
    }

    @Test
    public void testExceptionHandlingSucc() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("facebook:*")
                        .skipSendToOriginalEndpoint().throwException(new FacebookException("testException"))
                        .throwException(new FacebookException("testException")).throwException(new FacebookException("testException"))
                        .to("mock:facebook");
            }
        });
        context.start();

        getMockEndpoint("mock:facebook").expectedMessageCount(1);
        getMockEndpoint("mock:facebook").expectedBodiesReceived("test");

        Series series = mock(Series.class);
        when(series.getTitle())
                .thenReturn("test");

        template.sendBody("seda:facebook", series);

        assertMockEndpointsSatisfied();

        context.stop();
    }

    @Test
    public void testExceptionHandlingFail() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("facebook:*")
                        .skipSendToOriginalEndpoint().throwException(new FacebookException("testException"))
                        .throwException(new FacebookException("testException")).throwException(new FacebookException("testException"))
                        .throwException(new FacebookException("testException"))
                        .to("mock:facebook");
            }
        });
        context.start();

        getMockEndpoint("mock:facebook").expectedMessageCount(0);

        Series series = mock(Series.class);
        when(series.getTitle())
                .thenReturn("test");

        template.sendBody("seda:facebook", series);

        assertMockEndpointsSatisfied();

        context.stop();
    }


}
