package tvgrabber.routes;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import tvgrabber.beans.MyBean;
import tvgrabber.beans.Addressmanager;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Component
public class TVGrabberRouteBuilder extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(TVGrabberRouteBuilder.class);

    @Override
    public void configure() throws Exception {

        //TODO implement .from .to etc here


        /* synchronous alternative to SEDA would be "direct" component */
        from("seda:ichBinDerStart")
                .log(LoggingLevel.INFO, "ROUTING STARTED")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        log.info("process1");
                    }
                })
                .to("seda:ichBinDasEnde");


        from("seda:ichBinDasEnde")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        log.info("process2");
                    }
                })
                .bean(MyBean.class, "echo")
                .to("seda:ichBinDasEnde2");


        /* just an example to consume from the seda queue. check if messages are really in the queue */
        try {
            Endpoint endpoint = getContext().getEndpoint("seda:ichBinDasEnde2");
            final PollingConsumer consumer = endpoint.createPollingConsumer();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        Exchange exchange = consumer.receive();

                        if(exchange != null) {
                            System.out.println(exchange);
                        }
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*Subscribe/ Unsubscribe */
        //TODO: create mail account
        // see: http://camel.apache.org/mail.html
        from("pop3s://host:995?password=pw&username=name&consumer.delay=12000")
        .to("seda:smtp");

        from("seda:smtp").choice()
                .when(header("subject").isEqualTo("Unsubscribe")).to("seda:unsubscribe")
                .otherwise().to("seda:subscribe");

        from("seda:unsubscribe").bean(Addressmanager.class, "unsubscribe");
        from("seda:subscribe").bean(Addressmanager.class, "subscribe");
    }


}