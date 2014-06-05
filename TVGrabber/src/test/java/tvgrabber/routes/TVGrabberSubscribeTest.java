package tvgrabber.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
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
import tvgrabber.beans.Addressmanager;
import tvgrabber.entities.ObjectFactory;
import tvgrabber.entities.Series;
import tvgrabber.routes.TVGrabberSubscribe;

import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Cle
 * Date: 26.05.2014
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {TestConfig.class, TVGrabberSubscribeTest.SpecificTestConfig.class},
        loader = CamelSpringDelegatingTestContextLoader.class
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testing")
public class TVGrabberSubscribeTest extends CamelTestSupport {

    @Autowired
    private TVGrabberSubscribe TVGrabberSubscribe;

    @Autowired
    private CamelContext camelContext;

    @Configuration
    public static class SpecificTestConfig extends SingleRouteCamelConfiguration {
        @Autowired
        private TVGrabberSubscribe TVGrabberSubscribe;

        @Bean
        public RouteBuilder route() {
            return TVGrabberSubscribe;
        }
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return TVGrabberSubscribe;
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        return camelContext;
    }

    /** Tests the routing
     * @throws Exception
     */
    @Test
    public void testRouting() throws Exception {

        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:popTest");
                mockEndpoints("seda:subscribe");
                mockEndpoints("seda:unsubscribe");
            }
        });
        context.getRouteDefinitions().get(2).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("smtps:*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:smtpsEnd");
                interceptSendToEndpoint("jpa:*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:jpaEnd");
            }
        });

        context.start();

        getMockEndpoint("mock:jpaEnd").expectedMessageCount(2);
        getMockEndpoint("mock:seda:subscribe").expectedMessageCount(2);
        getMockEndpoint("mock:seda:unsubscribe").expectedMessageCount(0);
        getMockEndpoint("mock:smtpsEnd").expectedMessageCount(2);

        HashMap<String,Object> headers = new HashMap<String, Object>();
        headers.put("subject","Subscribe");
        headers.put("to", "workflowSS2014");
        headers.put("from", "bla");
        template.sendBodyAndHeaders("seda:popTest","body",headers);

        headers.put("subject","Subscribe");
        headers.put("to", "workflowSS2014");
        headers.put("from", "newUser");
        template.sendBodyAndHeaders("seda:popTest","body",headers);

        assertMockEndpointsSatisfied();
    }


}

