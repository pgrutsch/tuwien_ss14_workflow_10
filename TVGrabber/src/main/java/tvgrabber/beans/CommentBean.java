package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tvgrabber.entities.Comment;
import tvgrabber.entities.Series;
import tvgrabber.webservice.soap.SOAPComment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by patrickgrutsch on 18.05.14.
 */
@Component
public class CommentBean {

    private static final Logger logger = Logger.getLogger(CommentBean.class);

    @Autowired
    private DataSource dataSource;

    public void route(@Headers Map<String, Object> headers, Exchange exchange) throws NullPointerException {

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

    private void validateComment(SOAPComment comment) throws NullPointerException {
        if(comment.getComment() != null && comment.getEmail() != null) {
            if(getSeriesByID(comment.getTvprogram()) != null) {
                if(!comment.getComment().trim().isEmpty() &&
                        !comment.getEmail().trim().isEmpty()) {
                    // comment is valid
                } else {
                    logger.debug("Comment or Email are empty");
                    throw new NullPointerException();
                }
            } else {
                logger.debug("Invalid TVProgram ID: " + comment.getTvprogram() + ". Not found in DB");
                throw new NullPointerException();
            }
        } else {
            logger.debug("Comment or Email are null");
            throw new NullPointerException();
        }
    }

    private Series getSeriesByID(int id){
        Series tvProgram = null;

        Connection con = null;
        PreparedStatement getSeriesByIDPS = null;
        ResultSet rsSeries = null;
        try {
            con = dataSource.getConnection();
            getSeriesByIDPS = con.prepareStatement("SELECT * FROM TVProgram WHERE ID = ?");
            getSeriesByIDPS.setInt(1, id);
            rsSeries = getSeriesByIDPS.executeQuery();

            while (rsSeries.next()){
                tvProgram = new Series();
                tvProgram.setId(rsSeries.getInt("id"));
                tvProgram.setTitle(rsSeries.getString("title"));
                tvProgram.setDesc(rsSeries.getString("description"));
                tvProgram.setStart(rsSeries.getDate("startTime"));
                tvProgram.setStop(rsSeries.getDate("endTime"));
                tvProgram.setChannel(rsSeries.getString("channel"));
                tvProgram.setImdbRating(rsSeries.getDouble("imdbRating"));
                return tvProgram;
            }

            return tvProgram;

        } catch (SQLException e) {
            logger.error("DB error:");
            logger.error(e.toString());
            return null;
        } finally {
            try {
                if(con != null) con.close();
                if(getSeriesByIDPS != null) getSeriesByIDPS.close();
                if(rsSeries != null) rsSeries.close();
            } catch (SQLException e) {
                logger.error(e.toString());
            }

        }
    }
}
