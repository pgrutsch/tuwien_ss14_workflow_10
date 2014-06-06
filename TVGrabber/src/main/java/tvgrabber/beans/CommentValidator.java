package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tvgrabber.entities.Series;
import tvgrabber.exceptions.InvalidSoapCommentException;
import tvgrabber.webservice.soap.SOAPComment;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.regex.Pattern;

/**
 * Created by patrickgrutsch on 06.06.14.
 */
@Component
public class CommentValidator {

    private static final Logger logger = Logger.getLogger(CommentValidator.class);

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public void validateComment(Exchange exchange) throws InvalidSoapCommentException {
        SOAPComment comment = exchange.getIn().getBody(SOAPComment.class);

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
