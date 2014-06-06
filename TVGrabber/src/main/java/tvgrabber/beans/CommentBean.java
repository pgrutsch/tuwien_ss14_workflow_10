package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tvgrabber.entities.Comment;
import tvgrabber.entities.Series;
import tvgrabber.exceptions.InvalidSoapCommentException;
import tvgrabber.webservice.soap.SOAPComment;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by patrickgrutsch on 18.05.14.
 */
@Component
public class CommentBean {

    private static final Logger logger = Logger.getLogger(CommentBean.class);

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public void route(@Headers Map<String, Object> headers, Exchange exchange) throws InvalidSoapCommentException {
        SOAPComment soapComment = exchange.getIn().getBody(SOAPComment.class);

        Comment comment = new Comment();
        comment.setEmail(soapComment.getEmail());
        comment.setComment(soapComment.getComment());

        Series tvProgram = getSeriesByID(soapComment.getTvprogram());
        comment.setTvprogram(tvProgram);

        exchange.getIn().setBody(comment);

        String db = "jpa:tvgrabber.entities.Comment";
        String twitter = "seda:twitter";

        headers.put("recipients", Arrays.asList(db, twitter));
        headers.put("type", "comment");
    }

    private Series getSeriesByID(int id){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Series series = entityManager.find(Series.class, id);
        entityManager.close();
        return series;
    }
}
