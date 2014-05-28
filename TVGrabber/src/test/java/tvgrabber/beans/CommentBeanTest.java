package tvgrabber.beans;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tvgrabber.StandAloneTestH2;
import tvgrabber.TVGrabberConfig;
import tvgrabber.entities.Comment;
import tvgrabber.entities.Series;
import tvgrabber.webservice.soap.SOAPComment;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by patrickgrutsch on 24.05.14.
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = {TVGrabberConfig.class, StandAloneTestH2.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testing")
public class CommentBeanTest {

    private Exchange exchange;
    private CamelContext context;
    private Map<String, Object> headers;

    private static final Logger logger = Logger.getLogger(CommentBeanTest.class);

    @Autowired
    private CommentBean commentBean;

    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() {
        context = new DefaultCamelContext();
        exchange = new DefaultExchange(context);
        headers = new HashMap<String, Object>();

        Message in = exchange.getIn();
        in.setBody(createSOAPComment());
        in.setHeaders(headers);
    }

    private SOAPComment createSOAPComment() {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("i'm the best comment in the world");
        soapComment.setEmail("hans@mueller.at");
        soapComment.setTvprogram(1);

        return soapComment;
    }

    private Comment createComment() {
        Comment comment = new Comment();
        comment.setComment("i'm the best comment in the world");
        comment.setEmail("hans@mueller.at");

        Series series = new Series();
        series.setId(1);

        comment.setTvprogram(series);

        return comment;
    }

    @After
    public void tearDown() {
        exchange = null;
        context = null;
        headers = null;
    }

    @Test
    public void route_shouldReplaceSOAPCommentWithComment() {
        Connection c = null;
        try {
            c = dataSource.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM TVProgram");

            logger.debug("1Reading TVPrograms ...");
            while(rs.next()) {
                logger.debug("id: " + rs.getInt("id") + " title: " + rs.getString("title"));
            }
        } catch (SQLException e) {
            logger.error("ERROR INSERT AND READ");
            e.printStackTrace();
        }


        commentBean.route(headers, exchange);
        Comment comment = createComment();

        assertEquals(exchange.getIn().getBody(Comment.class).getComment(), comment.getComment());
        assertEquals(exchange.getIn().getBody(Comment.class).getEmail(), comment.getEmail());
        assertEquals(exchange.getIn().getBody(Comment.class).getTvprogram().getId(), comment.getTvprogram().getId());
        assertEquals(exchange.getIn().getBody(Comment.class).getId(), comment.getId());
    }

    @Test
    public void route_shouldSetRecipientlistHeader() throws Exception {
        Connection c = null;
        try {
            c = dataSource.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM TVProgram");

            logger.debug("2Reading TVPrograms ...");
            while(rs.next()) {
                logger.debug("id: " + rs.getInt("id") + " title: " + rs.getString("title"));
            }
        } catch (SQLException e) {
            logger.error("ERROR INSERT AND READ");
            e.printStackTrace();
        }

        commentBean.route(headers, exchange);

        List<String> recipients = new ArrayList<String>();
        recipients.add("jpa://tvgrabber.entities.Comment");
        recipients.add("seda:twitter");

        assertEquals(recipients, headers.get("recipients"));
    }

    //TODO: test empty comments etc.
    @Test
    public void route_shouldThrowNullPointerCommentEmpty() {

    }

}