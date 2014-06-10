package tvgrabber.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.PropertyInject;
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

    private @PropertyInject("subscribe.unsubscribeQueue") String unsubscribeQueue;
    private @PropertyInject("subscribe.subscribeQueue") String subscribeQueue;
    private @PropertyInject("addressmanager.fromMail") String mailAddress;
    private @PropertyInject("subscribe.criteria") String criteria;
    private @PropertyInject("subscribe.criteriaValue") String criteriaValue;
    private @PropertyInject("subscribe.antiCriteriaValue") String subscribeCriteriaValue;

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

    /** Tests the subscribe routing
     * @throws Exception
     */
    @Test
    public void testSubscribeRouting() throws Exception {

        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:popTest");
                mockEndpoints(subscribeQueue);
                mockEndpoints(unsubscribeQueue);
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
        getMockEndpoint("mock:"+subscribeQueue).expectedMessageCount(2);
        getMockEndpoint("mock:"+unsubscribeQueue).expectedMessageCount(0);
        getMockEndpoint("mock:smtpsEnd").expectedMessageCount(2);

        HashMap<String,Object> headers = new HashMap<String, Object>();
        headers.put(criteria,subscribeCriteriaValue);
        headers.put("to", mailAddress);
        headers.put("from", "bla");
        template.sendBodyAndHeaders("seda:popTest","body",headers);

        headers.put(criteria,subscribeCriteriaValue);
        headers.put("to", mailAddress);
        headers.put("from", "newUser");
        template.sendBodyAndHeaders("seda:popTest","body",headers);

        assertMockEndpointsSatisfied();
    }

    /**
     * Unsubscribes two existing user and one non existing user
     * @throws Exception
     */
    @Test
    public void testUnsubscribeUsers() throws Exception {

        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:popTest");

                mockEndpoints(subscribeQueue);
                mockEndpoints(unsubscribeQueue);
            }
        });
        context.getRouteDefinitions().get(1).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpoints(TVGrabberDeadLetter.DEAD_LETTER_CHANNEL);
                interceptSendToEndpoint("jpa:*")
                        .skipSendToOriginalEndpoint()
                        .to("mock:jpaEnd");
            }
        });
        context.start();
        getMockEndpoint("mock:"+subscribeQueue).expectedMessageCount(0);
        getMockEndpoint("mock:"+unsubscribeQueue).expectedMessageCount(2);
        getMockEndpoint("mock:jpaEnd").expectedMessageCount(2);
        getMockEndpoint("mock:" + TVGrabberDeadLetter.DEAD_LETTER_CHANNEL).expectedMessageCount(1);

        HashMap<String, Object> headers = new HashMap<String, Object>();

        headers.put(criteria, criteriaValue);
        headers.put("to", mailAddress);
        headers.put("from", "usermailbiz");
        template.sendBodyAndHeaders("seda:popTest", "body", headers);

        headers.put(criteria, criteriaValue);
        headers.put("to", mailAddress);
        headers.put("from", "user@mail.biz");
        template.sendBodyAndHeaders("seda:popTest", "body1", headers);

        headers.put("subject", criteriaValue);
        headers.put("to", mailAddress);
        headers.put("from", "bla");
        template.sendBodyAndHeaders("seda:popTest", "body2", headers);

        assertMockEndpointsSatisfied();

    }
}

