import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tvgrabber.Producer;
import tvgrabber.TVGrabberConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by stamm_000 on 15.05.14.
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = {TVGrabberConfig.class, StandAloneDerby.class})
@ActiveProfiles("testing")
@MockEndpoints
public class SampleTest extends AbstractJUnit4SpringContextTests{

    // What was is:
    // http://spring.io/blog/2011/06/21/spring-3-1-m2-testing-with-configuration-classes-and-profiles/

    @Autowired
    Producer producer;

    @Autowired
    DataSource dataSource;

    @Test
    public void SimpleTest() {
        Assert.assertEquals(true, true);
        //System.out.println("Hello Test");
    }

    @Test
    public void TestForTest() {
        Assert.assertEquals("Hello Test!!", producer.forTest());
    }

    @Test
    public void TestDB() {

        Connection cs = null;
        Statement stm = null;
        ResultSet rs = null;

        try {
            cs = dataSource.getConnection();
            stm = cs.createStatement();
            rs = stm.executeQuery("Select email from TVUser");

            rs.next();

            Assert.assertEquals("bla", rs.getString("email"));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stm != null) stm.close();
                if (cs != null) cs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void MockitoSampleTest() throws Exception {
        // for further reading:
        // http://docs.mockito.googlecode.com/hg-history/be6d53f62790ac7c9cf07c32485343ce94e1b563/1.9.5/org/mockito/Mockito.html

        String msgToSend = "Hi, I am a Test.";

        ProducerTemplate prodtemp = mock(ProducerTemplate.class);

        producer.setProducer(prodtemp);
        producer.send(msgToSend);

        verify(prodtemp).sendBody(msgToSend);

    }

    @EndpointInject(uri = "mock:direct:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:test")
    protected ProducerTemplate template;

    @DirtiesContext
    @Test
    public void testSendMatchingMessage() throws Exception {
        String expectedBody = "<matched/>";

        resultEndpoint.expectedBodiesReceived(expectedBody);

        template.sendBodyAndHeader(expectedBody, "foo", "bar");

        resultEndpoint.assertIsSatisfied();
    }

    @DirtiesContext
    @Test
    public void testSendNotMatchingMessage() throws Exception {
        resultEndpoint.expectedMessageCount(0);

        template.sendBodyAndHeader("<notMatched/>", "foo", "notMatchedHeaderValue");

        resultEndpoint.assertIsSatisfied();
    }

}


