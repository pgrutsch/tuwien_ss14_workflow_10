package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.log4j.Logger;
import tvgrabber.entities.Comment;
import tvgrabber.webservice.soap.SOAPComment;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by patrickgrutsch on 18.05.14.
 */
public class CommentBean {

    private static final Logger logger = Logger.getLogger(CommentBean.class);

    public void route(@Headers Map<String, Object> headers, Exchange exchange) {

        SOAPComment soapComment = exchange.getIn().getBody(SOAPComment.class);

        Comment comment = new Comment();
        comment.setEmail(soapComment.getEmail());
        comment.setComment(soapComment.getComment());
        comment.setTvprogram(soapComment.getTvprogram());

        exchange.getIn().setBody(comment);

        String db = "jpa://tvgrabber.beans.Comment";
        String twitter = ""; //TODO


        //TODO add twitter to list
        headers.put("recipients", Arrays.asList(db));
    }
}
