package tvgrabber.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tvgrabber.beans.Addressmanager;
import tvgrabber.exceptions.UnsubscribeException;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Component
public class TVGrabberSubscribe extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(TVGrabberSubscribe.class);


    @Override
    public void configure() throws Exception {
        /*Subscribe / Unsubscribe */
        from("pop3s://pop.gmail.com:995?password=workflow2014&username=workflow2014ss@gmail.com&consumer.delay=12000")
            .choice().when(header("subject").contains("Unsubscribe"))
                .to("seda:unsubscribe")
            .otherwise().to("seda:subscribe");


        from("seda:unsubscribe").bean(Addressmanager.class, "unsubscribe")
                .to("jpa://tvgrabber.entities.TVGrabberUser")
                .to("smtps://smtp.gmail.com:465?password=workflow2014&username=workflow2014ss@gmail.com")
                .errorHandler(deadLetterChannel(TVGrabberDeadLetter.DEAD_LETTER_CHANNEL));

        from("seda:subscribe").bean(Addressmanager.class, "subscribe")
                .to("jpa://tvgrabber.entities.TVGrabberUser")
                .to("smtps://smtp.gmail.com:465?password=workflow2014&username=workflow2014ss@gmail.com");
        }


    }