package tvgrabber.beans;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
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
import tvgrabber.TestConfig;
import tvgrabber.exceptions.InvalidSoapCommentException;
import tvgrabber.webservice.soap.SOAPComment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by patrickgrutsch on 06.06.14.
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = {TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testing")
public class CommentValidatorTest {

    private Exchange exchange;
    private CamelContext context;

    private static final Logger logger = Logger.getLogger(CommentValidatorTest.class);

    @Autowired
    private CommentValidator commentValidator;

    @Before
    public void setUp() {
        context = new DefaultCamelContext();
        exchange = new DefaultExchange(context);
    }

    @After
    public void tearDown() {
        exchange = null;
        context = null;
    }

    @Test
    public void validateComment_shouldValidateSuccessfully() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("mycomment");
        soapComment.setTvprogram(1);
        soapComment.setEmail("anton@mueller.at");

        exchange.getIn().setBody(soapComment);

        commentValidator.validateComment(exchange);
    }

    @Test(expected = InvalidSoapCommentException.class)
    public void validateComment_shouldThrowInvalidSoapCommentExCommentEmpty() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("");
        soapComment.setTvprogram(1);
        soapComment.setEmail("anton@mueller.at");

        exchange.getIn().setBody(soapComment);

        commentValidator.validateComment(exchange);
    }

    @Test(expected = InvalidSoapCommentException.class)
    public void validateComment_shouldThrowInvalidSoapCommentExCommentNull() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment(null);
        soapComment.setTvprogram(1);
        soapComment.setEmail("anton@mueller.at");

        exchange.getIn().setBody(soapComment);

        commentValidator.validateComment(exchange);
    }

    @Test(expected = InvalidSoapCommentException.class)
    public void validateComment_shouldThrowInvalidSoapCommentExEmailEmpty() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("mycomment");
        soapComment.setTvprogram(1);
        soapComment.setEmail("");

        exchange.getIn().setBody(soapComment);

        commentValidator.validateComment(exchange);
    }

    @Test(expected = InvalidSoapCommentException.class)
    public void validateComment_shouldThrowInvalidSoapCommentExEmailNull() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("mycomment");
        soapComment.setTvprogram(1);
        soapComment.setEmail(null);

        exchange.getIn().setBody(soapComment);

        commentValidator.validateComment(exchange);
    }

    @Test(expected = InvalidSoapCommentException.class)
    public void validateComment_shouldThrowInvalidSoapCommentExEmailInvalid() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("mycomment");
        soapComment.setTvprogram(1);
        soapComment.setEmail("asdfasdf");

        exchange.getIn().setBody(soapComment);

        commentValidator.validateComment(exchange);
    }

    @Test(expected = InvalidSoapCommentException.class)
    public void validateComment_shouldThrowInvalidSoapCommentExInvalidTVProgramID() throws InvalidSoapCommentException {
        SOAPComment soapComment = new SOAPComment();
        soapComment.setComment("mycomment");
        soapComment.setTvprogram(-1);
        soapComment.setEmail("anton@mueller.at");

        exchange.getIn().setBody(soapComment);

        commentValidator.validateComment(exchange);
    }
}
