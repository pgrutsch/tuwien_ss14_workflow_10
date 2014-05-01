package tvgrabber.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.seda.SedaEndpoint;
import org.springframework.stereotype.Component;
import tvgrabber.MyBean;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Component
public class TVGrabberRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        //TODO implement .from .to etc here



        /* check size of seda queue*/

        /*
        final SedaEndpoint seda = (SedaEndpoint)getContext().getEndpoint("seda:ichBinDasEnde");
        new Thread(new Runnable() {
            @Override
            public void run() {

                do {
                    log.info("SEDA SIZE: " + seda.getExchanges().size());

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                while(true);

            }
        }).start();
        */


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
                .bean(MyBean.class, "echo");


    }




}
