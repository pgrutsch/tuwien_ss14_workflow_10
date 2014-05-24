import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tvgrabber.TVGrabberConfig;
import tvgrabber.beans.CommentBean;
import tvgrabber.entities.Comment;
import tvgrabber.webservice.soap.SOAPComment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by patrickgrutsch on 24.05.14.
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = {TVGrabberConfig.class, StandAloneDerby.class})
@ActiveProfiles("testing")
public class CommentBeanTest {

    private Exchange exchange;
    private CamelContext context;
    private Map<String, Object> headers;

    @Autowired
    private CommentBean commentBean;

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
        comment.setTvprogram(1);

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
        commentBean.route(headers, exchange);
        Comment comment = createComment();

        assertEquals(exchange.getIn().getBody(Comment.class).getComment(), comment.getComment());
        assertEquals(exchange.getIn().getBody(Comment.class).getEmail(), comment.getEmail());
        assertEquals(exchange.getIn().getBody(Comment.class).getTvprogram(), comment.getTvprogram());
        assertEquals(exchange.getIn().getBody(Comment.class).getId(), comment.getId());
    }

    @Test
    public void route_shouldSetRecipientlistHeader() {
        commentBean.route(headers, exchange);

        List<String> recipients = new ArrayList<String>();
        recipients.add("jpa://tvgrabber.beans.Comment");

        //TODO add twitter
        assertEquals(headers.get("recipients"), recipients);
    }

}