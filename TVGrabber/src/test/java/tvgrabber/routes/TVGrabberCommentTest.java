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
import tvgrabber.webservice.soap.SOAPComment;

/**
 * Created by patrickgrutsch on 24.05.14.
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {TestConfig.class, TVGrabberCommentTest.SpecificTestConfig.class}, /* define both configs for route tests */
        loader = CamelSpringDelegatingTestContextLoader.class /* IMPORTANT: Use delegation loader */
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testing")
public class TVGrabberCommentTest extends CamelTestSupport {

    /**
     * BASIC TEST SETUP
     */

    private @PropertyInject("twitter.seda") String twitter;
    private @PropertyInject("comment.jpaComment") String jpaComment;

    private static final Logger logger = Logger.getLogger(TVGrabberCommentTest.class);

    /**
     * WATCH OUT for variable name. if you use eg. tvGrabberComment, there will be an uniquebeanidentifier exception!!!
     */
    @Autowired
    private TVGrabberComment TVGrabberComment; /* needed for createRouteBuilder() */

    @Autowired
    private CamelContext camelContext; /* SpringCamelContext from TestConfig */

    @Configuration
    public static class SpecificTestConfig extends SingleRouteCamelConfiguration {

        @Autowired
        private TVGrabberComment TVGrabberComment; /* needed for route() method */

        @Bean
        public RouteBuilder route() { /* limits the test class environment to use only one routeBuilder */
            return TVGrabberComment;
        }
    }

    @Override
    public boolean isUseAdviceWith() { /* defines that tests run with adviceWith */
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception { /* returns the route used for the adviceWith tests */
        return TVGrabberComment;
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
    public void testShouldSendCommentToDBandTwitter() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:cxftest");

                interceptSendToEndpoint(jpaComment)
                        .skipSendToOriginalEndpoint()
                        .to("mock:advice");

                interceptSendToEndpoint(twitter)
                        .skipSendToOriginalEndpoint()
                        .to("mock:advice");
            }
        });

        context.start();

        getMockEndpoint("mock:jpa:tvgrabber.entities.Comment").expectedMessageCount(0);
        getMockEndpoint("mock:seda:twitter").expectedMessageCount(0);
        getMockEndpoint("mock:advice").expectedMessageCount(2);

        SOAPComment comment = new SOAPComment();
        comment.setComment("im the comment");
        comment.setEmail("andi@much.at");
        comment.setTvprogram(1);

        template.sendBody("seda:cxftest", comment);

        assertMockEndpointsSatisfied();

        context.stop();
    }


    @Test
    public void testSendMultipleCommentsToDBandTwitter() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:cxftest");

                interceptSendToEndpoint(jpaComment)
                        .skipSendToOriginalEndpoint()
                        .to("mock:advice");

                interceptSendToEndpoint(twitter)
                        .skipSendToOriginalEndpoint()
                        .to("mock:advice");
            }
        });

        context.start();

        getMockEndpoint("mock:jpa:tvgrabber.entities.Comment").expectedMessageCount(0);
        getMockEndpoint("mock:seda:twitter").expectedMessageCount(0);
        getMockEndpoint("mock:advice").expectedMessageCount(6);

        SOAPComment comment1 = new SOAPComment();
        comment1.setComment("im the comment1");
        comment1.setEmail("andi@much.at");
        comment1.setTvprogram(1);
        template.sendBody("seda:cxftest", comment1);

        SOAPComment comment2 = new SOAPComment();
        comment2.setComment("im the comment2");
        comment2.setEmail("andi@much.at");
        comment2.setTvprogram(2);
        template.sendBody("seda:cxftest", comment2);

        SOAPComment comment3 = new SOAPComment();
        comment3.setComment("im the comment3");
        comment3.setEmail("andi@much.at");
        comment3.setTvprogram(3);
        template.sendBody("seda:cxftest", comment3);

        assertMockEndpointsSatisfied();

        context.stop();
    }

    @Test
    public void testSendCommentWithInvalidTVProgramIDToDeadLetter() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:cxftest");

                interceptSendToEndpoint("seda:errors")
                        .skipSendToOriginalEndpoint()
                        .to("mock:advice");
            }
        });

        context.start();

        getMockEndpoint("mock:jpa:tvgrabber.entities.Comment").expectedMessageCount(0);
        getMockEndpoint("mock:seda:twitter").expectedMessageCount(0);
        getMockEndpoint("mock:advice").expectedMessageCount(1);

        SOAPComment comment = new SOAPComment();
        comment.setComment("im the comment");
        comment.setEmail("andi@much.at");
        comment.setTvprogram(-1);

        template.sendBody("seda:cxftest", comment);

        assertMockEndpointsSatisfied();

        context.stop();
    }

    @Test
    public void testSendEmptyCommentToDeadLetter() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("seda:cxftest");

                interceptSendToEndpoint("seda:errors")
                        .skipSendToOriginalEndpoint()
                        .to("mock:advice");
            }
        });

        context.start();

        getMockEndpoint("mock:jpa:tvgrabber.entities.Comment").expectedMessageCount(0);
        getMockEndpoint("mock:seda:twitter").expectedMessageCount(0);
        getMockEndpoint("mock:advice").expectedMessageCount(1);

        SOAPComment comment = new SOAPComment();
        comment.setComment("");
        comment.setEmail("andi@much.at");
        comment.setTvprogram(1);

        template.sendBody("seda:cxftest", comment);

        assertMockEndpointsSatisfied();

        context.stop();
    }

}