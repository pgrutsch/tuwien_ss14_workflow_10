package tvgrabber.routes;

import org.apache.camel.BeanInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.camel.test.spring.UseAdviceWith;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tvgrabber.StandAloneTestH2;
import tvgrabber.TVGrabberConfig;
import tvgrabber.TestConfig;
import tvgrabber.webservice.soap.SOAPComment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by patrickgrutsch on 24.05.14.
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {StandAloneTestH2.class, TestConfig.class},
        loader = AnnotationConfigContextLoader.class
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testing")
public class TVGrabberCommentTest extends CamelTestSupport {

    private static final Logger logger = Logger.getLogger(TVGrabberCommentTest.class);

    @Produce(uri = "cxf://http://localhost:8080/spring-soap/PostComment?serviceClass=tvgrabber.webservice.soap.PostComment")
    private ProducerTemplate cxfProducer;

    @Autowired
    protected TVGrabberComment tvGrabberComment;



    /*
    @Test
    public void shouldSaveCommentToDB() throws InterruptedException {

        endEndpoint.expectedMessageCount(1);
        errorEndpoint.expectedMessageCount(0);

        SOAPComment comment = new SOAPComment();
        comment.setComment("im the comment");
        comment.setEmail("andi@much.at");
        comment.setTvprogram(1);

        testProducer.sendBody(comment);

        endEndpoint.assertIsSatisfied();
        errorEndpoint.assertIsSatisfied();

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

    } */
    /*

    @Test
    public void shouldSendCommentToTwitter() {

    } */

    @Test
    public void testIsUseAdviceWith() throws Exception {
        /*
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("jpa:tvgrabber.entities.Comment")
                        .skipSendToOriginalEndpoint()
                        .to("mock:adviced");
            }
        });
        context.start();

        getMockEndpoint("mock:jpa:tvgrabber.entities.Comment").expectedMessageCount(0);
        getMockEndpoint("mock:adviced").expectedMessageCount(1);

        SOAPComment comment = new SOAPComment();
        comment.setComment("im the comment");
        comment.setEmail("andi@much.at");
        comment.setTvprogram(1);

        cxfProducer.sendBody(comment);

        assertMockEndpointsSatisfied();
        */
    }

    /*
    @Test
    public void testSendMultipleCommentsToDB() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("jpa:tvgrabber.entities.Comment")
                        .skipSendToOriginalEndpoint()
                        .to("mock:adviced");
            }
        });
        context.start();

        getMockEndpoint("mock:jpa:tvgrabber.entities.Comment").expectedMessageCount(0);
        getMockEndpoint("mock:adviced").expectedMessageCount(3);

        SOAPComment comment1 = new SOAPComment();
        comment1.setComment("im the comment1");
        comment1.setEmail("andi@much.at");
        comment1.setTvprogram(1);
        cxfProducer.sendBody(comment1);

        SOAPComment comment2 = new SOAPComment();
        comment2.setComment("im the comment2");
        comment2.setEmail("andi@much.at");
        comment2.setTvprogram(2);
        cxfProducer.sendBody(comment2);

        SOAPComment comment3 = new SOAPComment();
        comment3.setComment("im the comment3");
        comment3.setEmail("andi@much.at");
        comment3.setTvprogram(3);
        cxfProducer.sendBody(comment3);

        assertMockEndpointsSatisfied();
        context.stop();
    }
*/
    /*
    @Test
    public void testSendCommentWithInvalidTVProgramIDToDeadLetter() throws Exception {

    } */

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return tvGrabberComment;
    }

}