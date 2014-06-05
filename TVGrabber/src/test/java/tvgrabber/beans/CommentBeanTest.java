package tvgrabber.beans;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
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
import tvgrabber.TestConfig;
import tvgrabber.entities.Comment;
import tvgrabber.entities.Series;
import tvgrabber.exceptions.InvalidSoapCommentException;
import tvgrabber.webservice.soap.SOAPComment;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        classes = {TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testing")
public class CommentBeanTest {

    private Exchange exchange;
    private CamelContext context;
    private Map<String, Object> headers;

    private static final Logger logger = Logger.getLogger(CommentBeanTest.class);

    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private CommentBean commentBean;

    @Before
    public void setUp() {
        context = new DefaultCamelContext();
        exchange = new DefaultExchange(context);
        headers = new HashMap<String, Object>();
        entityManager = entityManagerFactory.createEntityManager();
    }

    @After
    public void tearDown() {
        exchange = null;
        context = null;
        headers = null;
        entityManager.close();
    }

    @Test
    public void route_shouldReplaceSOAPCommentWithComment() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("mycomment");
        soapComment.setTvprogram(1);
        soapComment.setEmail("anton@mueller.at");

        exchange.getIn().setBody(soapComment);

        commentBean.route(headers, exchange);

        assertEquals(exchange.getIn().getBody(Comment.class).getComment(), "mycomment");
        assertEquals(exchange.getIn().getBody(Comment.class).getEmail(), "anton@mueller.at");
        assertEquals(exchange.getIn().getBody(Comment.class).getTvprogram().getId(), 1, 0);
    }

    @Test
    public void route_shouldSetRecipientlistHeader() throws Exception {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("mycomment");
        soapComment.setTvprogram(1);
        soapComment.setEmail("anton@mueller.at");

        exchange.getIn().setBody(soapComment);

        commentBean.route(headers, exchange);

        List<String> recipients = new ArrayList<String>();
        recipients.add("jpa:tvgrabber.entities.Comment");
        recipients.add("seda:twitter");

        assertEquals(recipients, headers.get("recipients"));
    }

    @Test
    public void route_shouldSetTypeHeader() throws Exception {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("mycomment");
        soapComment.setTvprogram(1);
        soapComment.setEmail("anton@mueller.at");

        exchange.getIn().setBody(soapComment);

        commentBean.route(headers, exchange);

        assertEquals("comment", headers.get("type"));
    }

    @Test(expected = InvalidSoapCommentException.class)
    public void route_shouldThrowInvalidSoapCommentExCommentEmpty() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("");
        soapComment.setTvprogram(1);
        soapComment.setEmail("anton@mueller.at");

        exchange.getIn().setBody(soapComment);

        commentBean.route(headers, exchange);
    }

    @Test(expected = InvalidSoapCommentException.class)
    public void route_shouldThrowInvalidSoapCommentExCommentNull() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment(null);
        soapComment.setTvprogram(1);
        soapComment.setEmail("anton@mueller.at");

        exchange.getIn().setBody(soapComment);

        commentBean.route(headers, exchange);
    }

    @Test(expected = InvalidSoapCommentException.class)
    public void route_shouldThrowInvalidSoapCommentExEmailEmpty() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("mycomment");
        soapComment.setTvprogram(1);
        soapComment.setEmail("");

        exchange.getIn().setBody(soapComment);

        commentBean.route(headers, exchange);
    }

    @Test(expected = InvalidSoapCommentException.class)
    public void route_shouldThrowInvalidSoapCommentExEmailNull() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("mycomment");
        soapComment.setTvprogram(1);
        soapComment.setEmail(null);

        exchange.getIn().setBody(soapComment);

        commentBean.route(headers, exchange);
    }

    @Test(expected = InvalidSoapCommentException.class)
    public void route_shouldThrowInvalidSoapCommentExEmailInvalid() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("mycomment");
        soapComment.setTvprogram(1);
        soapComment.setEmail("asdfasdf");

        exchange.getIn().setBody(soapComment);

        commentBean.route(headers, exchange);
    }

    @Test(expected = InvalidSoapCommentException.class)
    public void route_shouldThrowInvalidSoapCommentExInvalidTVProgramID() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("mycomment");
        soapComment.setTvprogram(-1);
        soapComment.setEmail("anton@mueller.at");

        exchange.getIn().setBody(soapComment);

        commentBean.route(headers, exchange);
    }

}