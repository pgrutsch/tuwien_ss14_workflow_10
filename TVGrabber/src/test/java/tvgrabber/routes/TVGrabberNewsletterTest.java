package tvgrabber.routes;


import org.apache.camel.CamelContext;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
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
 * Created by Isabella on 21.06.2014.
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {TestConfig.class, TVGrabberNewsletterTest.SpecificTestConfig.class}, /* define both configs for route tests */
        loader = CamelSpringDelegatingTestContextLoader.class /* IMPORTANT: Use delegation loader */
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testing")
public class TVGrabberNewsletterTest extends CamelTestSupport{

    @Autowired
    private TVGrabberNewsletter TVGrabberNewsletter;

    @Autowired
    private CamelContext camelContext;

    @Configuration
    public static class SpecificTestConfig extends SingleRouteCamelConfiguration {

        @Autowired
        private TVGrabberNewsletter TVGrabberNewsletter ;

        @Bean
        public RouteBuilder route() { /* limits the test class environment to use only one routeBuilder */
            return TVGrabberNewsletter;
        }
    }

    @Override
    public boolean isUseAdviceWith() { /* defines that tests run with adviceWith */
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception { /* returns the route used for the adviceWith tests */
        return TVGrabberNewsletter;
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        return camelContext;
    }

    /**
      Tests
     **/

    @Test
    public void test_shouldChangeHeader() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:mockJpa");

                interceptSendToEndpoint("seda:aggregator")
                        .skipSendToOriginalEndpoint()
                        .to("mock:aggregatorAdvice");
            }
        });

        context.getRouteDefinitions().get(1).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:empty1");
            }
        });

        context.getRouteDefinitions().get(2).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:empty2");
            }
        });

        context.getRouteDefinitions().get(3).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:empty3");
            }
        });

        context.start();

        Series series1 = new Series();
        series1.setTitle("A");
        series1.setStart(new Date());
        series1.setStop(new Date());

        getMockEndpoint("mock:seda:aggregator").expectedMessageCount(0);
        getMockEndpoint("mock:aggregatorAdvice").expectedMessageCount(1);
        getMockEndpoint("mock:aggregatorAdvice").expectedBodiesReceived(series1);
        getMockEndpoint("mock:aggregatorAdvice").expectedHeaderReceived("title", series1.getTitle());

        template.sendBody("seda:mockJpa", series1);

        assertMockEndpointsSatisfied();

        context.stop();
    }

    @Test
    public void test_shouldResequenceSeries() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:empty1");
            }
        });

        context.getRouteDefinitions().get(1).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:empty2");
            }
        });

        context.getRouteDefinitions().get(2).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("seda:aggregatorAll")
                        .skipSendToOriginalEndpoint()
                        .to("mock:aggregatorAllAdvice");
            }
        });

        context.getRouteDefinitions().get(3).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:empty3");
            }
        });

        context.start();

        Series series1 = new Series();
        series1.setTitle("A");
        series1.setStart(new Date());
        series1.setStop(new Date());

        Series series2 = new Series();
        series2.setTitle("B");
        series2.setStart(new Date());
        series2.setStop(new Date());

        Series series3 = new Series();
        series3.setTitle("C");
        series3.setStart(new Date());
        series3.setStop(new Date());

        getMockEndpoint("mock:seda:aggregatorAll").expectedMessageCount(0);
        getMockEndpoint("mock:aggregatorAllAdvice").expectedMessageCount(3);
        getMockEndpoint("mock:aggregatorAllAdvice").expectedBodiesReceived(series3, series2, series1);

        template.sendBodyAndHeader("seda:resequencer", series1, "title", series1.getTitle());
        template.sendBodyAndHeader("seda:resequencer", series2, "title", series2.getTitle());
        template.sendBodyAndHeader("seda:resequencer", series3, "title", series3.getTitle());

        assertMockEndpointsSatisfied();

        context.stop();
    }

}
