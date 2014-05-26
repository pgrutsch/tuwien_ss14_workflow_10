package tvgrabber.routesTest;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import tvgrabber.TVGrabberConfig;
import tvgrabber.routes.TVGrabberComment;
import tvgrabber.webservice.soap.SOAPComment;

/**
 * Created by patrickgrutsch on 24.05.14.
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {TVGrabberConfig.class, TVGrabberCommentTest.TestConfig.class},
        loader = CamelSpringDelegatingTestContextLoader.class
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@MockEndpoints
public class TVGrabberCommentTest {

    @EndpointInject(uri = "mock:jpa://tvgrabber.entities.Comment")
    protected MockEndpoint endEndpoint;

    @EndpointInject(uri = "mock:seda:errors")
    protected MockEndpoint errorEndpoint;

    @Produce(uri = "cxf://http://localhost:8080/spring-soap/PostComment?serviceClass=tvgrabber.webservice.soap.PostComment")
    protected ProducerTemplate testProducer;

    @Configuration
    public static class TestConfig extends SingleRouteCamelConfiguration {

        @Autowired
        private TVGrabberComment tvGrabberComment;

        @Bean
        @Override
        public RouteBuilder route() {
            return tvGrabberComment;
        }
    }

    @Test
    public void shouldNotSaveCommentWithInvalidTVProgram() throws InterruptedException {
        endEndpoint.expectedMessageCount(0);
        errorEndpoint.expectedMessageCount(1);

        SOAPComment comment = new SOAPComment();
        comment.setComment("im the comment");
        comment.setEmail("andi@much.at");
        comment.setTvprogram(-1);

        testProducer.sendBody(comment);

        endEndpoint.assertIsSatisfied();
        errorEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldReceiveSOAPMessage() {

    }

    @Test
    public void shouldTransformSOAPCommentToComment() {

    }

    @Test
    public void shouldSaveCommentToDB() {

    }

    @Test
    public void shouldSendCommentToTwitter() {

    }

}
