package tvgrabber.routes;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import tvgrabber.StandAloneTestH2;
import tvgrabber.TVGrabberConfig;
import tvgrabber.webservice.soap.SOAPComment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by patrickgrutsch on 24.05.14.
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {TVGrabberConfig.class, StandAloneTestH2.class, TVGrabberCommentTest.TestConfig.class},
        loader = CamelSpringDelegatingTestContextLoader.class
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints
@ActiveProfiles("testing")
public class TVGrabberCommentTest {

    private static final Logger logger = Logger.getLogger(TVGrabberCommentTest.class);

    @EndpointInject(uri = "mock:jpa://tvgrabber.entities.Comment")
    protected MockEndpoint endEndpoint;

    @EndpointInject(uri = "mock:seda:errors")
    protected MockEndpoint errorEndpoint;

    @Produce(uri = "cxf://http://localhost:8080/spring-soap/PostComment?serviceClass=tvgrabber.webservice.soap.PostComment")
    protected ProducerTemplate testProducer;

    @Autowired
    private DataSource dataSource;

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
        /*
        Connection c = null;
        try {
            c = dataSource.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM TVProgram");

            logger.debug("1aReading TVPrograms ...");
            while(rs.next()) {
                logger.debug("id: " + rs.getInt("id") + " title: " + rs.getString("title"));
            }
        } catch (SQLException e) {
            logger.error("ERROR INSERT AND READ");
            e.printStackTrace();
        } */


        /*
            Route testing not working yet
         */

        /*
        endEndpoint.expectedMessageCount(1);
        errorEndpoint.expectedMessageCount(0);

        SOAPComment comment = new SOAPComment();
        comment.setComment("im the comment");
        comment.setEmail("andi@much.at");
        comment.setTvprogram(1);

        testProducer.sendBody(comment);

        endEndpoint.assertIsSatisfied();
        errorEndpoint.assertIsSatisfied();
        */

    }

    @Test
    public void shouldSaveCommentToDB() throws InterruptedException {
        /* route testing not working yet */

        /*
        endEndpoint.expectedMessageCount(0);
        errorEndpoint.expectedMessageCount(1);

        SOAPComment comment = new SOAPComment();
        comment.setComment("im the comment");
        comment.setEmail("andi@much.at");
        comment.setTvprogram(-11);

        testProducer.sendBody(comment);

        endEndpoint.assertIsSatisfied();
        errorEndpoint.assertIsSatisfied();
        */
    }

    @Test
    public void shouldSendCommentToTwitter() {

    }

}
