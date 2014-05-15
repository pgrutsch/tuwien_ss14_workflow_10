package tvgrabber.beans;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cle
 * Date: 13.05.2014
 */

@Component
public class Addressmanager {
    private static final Logger logger = Logger.getLogger(Addressmanager.class);

    public void unsubscribe(@Headers Map<String, Object> headers, @Body String myBody, Exchange exchange){
        logger.debug("Started to unsubscribe user");
        //TODO: unsubscribe this user

    }
    public void subscribe(@Headers Map<String, Object> headers, @Body String myBody, Exchange exchange){
        logger.debug("Started to subscribe user");
        //TODO: subscribe this user

    }

    public String forTest() {
        return "Hello Test!!";
    }

}
