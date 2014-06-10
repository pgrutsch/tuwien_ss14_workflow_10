package tvgrabber.beans;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import tvgrabber.entities.TVGrabberUser;
import tvgrabber.exceptions.UnsubscribeException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cle
 * Date: 13.05.2014
 */

@Component
public class Addressmanager {
    private static final Logger logger = Logger.getLogger(Addressmanager.class);

    private @Autowired DataSource dataSource;
    private @Autowired Environment props;

    public void unsubscribe(@Headers Map<String, Object> headers, @Body String myBody, Exchange exchange) throws UnsubscribeException {
        String userMail = String.valueOf(headers.get("from"));
        logger.debug("Started to unsubscribe user: " + userMail);
        TVGrabberUser subscriber = existsUser(userMail);
        if(subscriber!=null && subscriber.isSubscribed()) {
            subscriber.setSubscribed(false);
            subscriber.setSearchTerm("");
            exchange.getOut().getHeaders().put("To", userMail);
            exchange.getOut().getHeaders().put("From", props.getProperty("addressmanager.fromMail"));
            exchange.getOut().getHeaders().put("Subject", props.getProperty("addressmanger.successfullyUnsubscribed"));
        }else{
            String message = props.getProperty("addressmanager.user")+" '" + userMail + "' "+props.getProperty("addressmanager.triedTo") +" '"
                    + myBody + "' "+ props.getProperty("addressmanager.notInDB");
            logger.debug(message);
            throw new UnsubscribeException(message);
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
        subscriber.setSubscribed(true);
        subscriber.setSearchTerm(myBody);
        exchange.getOut().setBody(subscriber);
        exchange.getOut().getHeaders().put("To", userMail);
        exchange.getOut().getHeaders().put("From", props.getProperty("addressmanager.fromMail"));
        exchange.getOut().getHeaders().put("Subject", props.getProperty("addressmanager.successfullySubscribed"));
    }

    /** DB lookup, if there is an user with the given email
     * @param email != null
     * @return existing user or new user with the searched email address
     */
    public TVGrabberUser existsUser(String email){
        TVGrabberUser dbUser = null;
        try {
            Connection con = dataSource.getConnection();
            PreparedStatement existsUserPS = con.prepareStatement(props.getProperty("addressmanager.existsUserByMail"));
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
