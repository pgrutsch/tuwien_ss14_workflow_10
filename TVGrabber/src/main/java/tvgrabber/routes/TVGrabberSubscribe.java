package tvgrabber.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tvgrabber.beans.Addressmanager;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Component
public class TVGrabberSubscribe extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(TVGrabberSubscribe.class);

    @Autowired
    private Addressmanager addressmanager;

    @Override
    public void configure() throws Exception {

        /*Subscribe / Unsubscribe */
        from("pop3s://pop.gmail.com:995?password=workflow2014&username=workflow2014ss@gmail.com&consumer.delay=12000")
            .choice().when(header("subject").contains("Unsubscribe"))
                .to("seda:unsubscribe")
            .otherwise().to("seda:subscribe");

        from("seda:unsubscribe").bean(addressmanager, "unsubscribe")
                .choice().when(body().isNotNull())
                    .to("jpa://tvgrabber.entities.TVGrabberUser")
                    .to("smtps://smtp.gmail.com:465?password=workflow2014&username=workflow2014ss@gmail.com")
                .otherwise().to(TVGrabberDeadLetter.DEAD_LETTER_CHANNEL);

        from("seda:subscribe").bean(addressmanager, "subscribe")
                .to("jpa://tvgrabber.entities.TVGrabberUser")
                .to("smtps://smtp.gmail.com:465?password=workflow2014&username=workflow2014ss@gmail.com");
        }


    }