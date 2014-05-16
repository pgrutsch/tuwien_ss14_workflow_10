package tvgrabber.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.spi.DataFormat;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import tvgrabber.beans.Addressmanager;
import tvgrabber.beans.MyBean;
import tvgrabber.entities.Series;

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

        from("seda:unsubscribe").bean(MyBean.class,"echo").bean(Addressmanager.class, "unsubscribe");
        from("seda:subscribe").bean(Addressmanager.class, "subscribe");

    }


}