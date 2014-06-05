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
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by patrickgrutsch on 18.05.14.
 */
@Component
public class CommentBean {

    private static final Logger logger = Logger.getLogger(CommentBean.class);

    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public void route(@Headers Map<String, Object> headers, Exchange exchange) throws InvalidSoapCommentException {

        SOAPComment soapComment = exchange.getIn().getBody(SOAPComment.class);

        validateComment(soapComment);

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

    private void validateComment(SOAPComment comment) throws InvalidSoapCommentException {
        if(comment.getComment() != null && comment.getEmail() != null) {
            if(getSeriesByID(comment.getTvprogram()) != null) {
                if(!comment.getComment().trim().isEmpty() &&
                        !comment.getEmail().trim().isEmpty()) {
                    if(pattern.matcher(comment.getEmail()).matches()) {
                        // comment is valid
                    } else {
                        logger.debug("Email '" + comment.getEmail() + "' is invalid");
                        throw new InvalidSoapCommentException("Email is invalid");
                    }
                } else {
                    logger.debug("Comment or Email is empty");
                    throw new InvalidSoapCommentException("Comment or Email is empty");
                }
            } else {
                logger.debug("Invalid TVProgram ID: " + comment.getTvprogram() + ". Not found in DB");
                throw new InvalidSoapCommentException("Invalid TVProgram ID: " + comment.getTvprogram() + ". Not found in DB");
            }
        } else {
            logger.debug("Comment or Email is null");
            throw new InvalidSoapCommentException("Comment or Email is null");
        }
    }

    private Series getSeriesByID(int id){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Series series = entityManager.find(Series.class, id);
        entityManager.close();
        return series;
    }
}
