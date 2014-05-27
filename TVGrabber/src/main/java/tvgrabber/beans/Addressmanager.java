package tvgrabber.beans;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tvgrabber.TVGrabberMain;
import tvgrabber.entities.TVGrabberUser;
import tvgrabber.exceptions.UnsubscribeException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cle
 * Date: 13.05.2014
 */

@Component
public class Addressmanager {
    private static final Logger logger = Logger.getLogger(Addressmanager.class);
    private static final String existsUserByMail = "SELECT id, email, subscribed, searchTerm FROM TVUser WHERE email = ?";

    @Autowired
    private DataSource dataSource;

    public void unsubscribe(@Headers Map<String, Object> headers, @Body String myBody, Exchange exchange){
        String userMail = String.valueOf(headers.get("from"));
        logger.debug("Started to unsubscribe user: " + userMail);
        TVGrabberUser subscriber = existsUser(userMail);
        if(subscriber!=null) {
            subscriber.setSubscribed(false);
            subscriber.setSearchTerm("");
            exchange.getOut().getHeaders().put("To", userMail);
            exchange.getOut().getHeaders().put("From", "workflow2014ss@gmail.com");
            exchange.getOut().getHeaders().put("Subject", "Successfully unsubscribed");
        }else{
            exchange.getOut().setFault(true);
            logger.debug("User '" + userMail + "' tried to unsubscribe: '" + myBody + "' but isn't in the db.");
            exchange.setException(new UnsubscribeException("User '" + userMail + "' tried to unsubscribe: '" + myBody + "' but isn't in the db."));
        }
        exchange.getOut().setBody(subscriber);
    }
    public void subscribe(@Headers Map<String, Object> headers, @Body String myBody, Exchange exchange){
        String userMail = String.valueOf(headers.get("from"));
        logger.debug("Started to subscribe user " + userMail);

        TVGrabberUser subscriber = existsUser(userMail);
        if(subscriber==null){
           subscriber = new TVGrabberUser();
           subscriber.setEmail(userMail);
        }
        //TODO: discuss about the values of subscribed
        subscriber.setSubscribed(true);
        subscriber.setSearchTerm(myBody);
        exchange.getOut().setBody(subscriber);
        exchange.getOut().getHeaders().put("To", userMail);
        exchange.getOut().getHeaders().put("From", "workflow2014ss@gmail.com");
        exchange.getOut().getHeaders().put("Subject", "Successfully subscribed");
    }

    /** DB lookup, if there is an user with the given email
     * @param email != null
     * @return existing user or new user with the searched email address
     */
    public TVGrabberUser existsUser(String email){
        TVGrabberUser dbUser = null;
        try {
            Connection con = dataSource.getConnection();
            PreparedStatement existsUserPS = con.prepareStatement(existsUserByMail);
            existsUserPS.setString(1, email);
            ResultSet rsUser = existsUserPS.executeQuery();
            while (rsUser.next()){
                dbUser = new TVGrabberUser();
                dbUser.setId(rsUser.getInt("id"));
                dbUser.setEmail(rsUser.getString("email"));
                dbUser.setSubscribed(rsUser.getBoolean("subscribed"));
                dbUser.setSearchTerm(rsUser.getString("searchTerm"));
                return dbUser;
            }
            return dbUser;

        } catch (SQLException e) {
            logger.error("DB error:");
            logger.error(e.toString());
            return null;
        }
    }
}
