package tvgrabber.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import tvgrabber.beans.Addressmanager;
import tvgrabber.beans.MyBean;
import tvgrabber.entities.TVGrabberUser;

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
        .bean(MyBean.class,"echo")
                .choice()
                .when(header("subject").contains("Unsubscribe")).to("seda:unsubscribe")
                .otherwise().to("seda:subscribe");
        //TODO: perhaps a deadletter queue or something like this :)

        from("seda:unsubscribe").bean(Addressmanager.class, "unsubscribe")
                .to("jpa://tvgrabber.entities.TVGrabberUser")
                .setHeader("to", header("from")).to("smtp://smtp.gmail.com:587?password=workflow2014&username=workflow2014ss@gmail.com");

        from("seda:subscribe").bean(Addressmanager.class, "subscribe").to("jpa://tvgrabber.entities.TVGrabberUser");

        }


    }