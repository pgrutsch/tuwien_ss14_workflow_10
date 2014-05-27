package tvgrabber.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ExpressionNode;
import org.apache.camel.model.config.BatchResequencerConfig;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;
import org.apache.openjpa.lib.log.Log;
import org.springframework.stereotype.Component;
import tvgrabber.TVGrabberMain;
import tvgrabber.entities.Series;
import tvgrabber.entities.TVGrabberUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Component
public class TVGrabberNewsletter extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(TVGrabberNewsletter.class);

    @Override
    public void configure() throws Exception {

     /*    from("jpa://tvgrabber.entities.Series?consumer.delay=10000&consumer.query=select s from tvgrabber.entities.Series s") //where s.title like '%Fußball%' or s.title like '%Sturm%'")
                 .log(LoggingLevel.INFO, "********************** Lese Serien fuer Newsletter ein **************************")
                 .process(new Processor() {
                     @Override
                     public void process(Exchange exchange) throws Exception {
                         logger.info("Series: " + exchange.getIn().getBody(Series.class).getTitle());
                     }
                 }).convertBodyTo(Series.class)
                 .to("seda:resequencer");


        from("seda:resequencer").resequence(body()).batch(new BatchResequencerConfig(5,10000L))
                .log(LoggingLevel.INFO, "********************** NEWSLETTER2 **************************")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        logger.info("Series: " + exchange.getIn().getBody(Series.class).getTitle());
                    }
                })
                .to("seda:aggregator");

        from("seda:aggregator").aggregate(header("id"),new AggregationStrategy() {
            @Override
            public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                Message newIn = newExchange.getIn();
                String oldBody = oldExchange.getIn().getBody(String.class);
                String newBody = newIn.getBody(String.class);
                newIn.setBody(oldBody + newBody);
                return newExchange;
            }
        }).completionSize(1)
        .to("seda:publisher");
        /*.log(LoggingLevel.INFO, "********************** NEWSLETTER3 **************************")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        logger.info("Series: " + exchange.getIn().getBody(Series.class).getTitle());
                    }
                });*/
        // .to("jpm:publisher");

/*
        from("seda:publisher").process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                logger.info("Finito: " + exchange.getIn().getBody(Series.class).getTitle());
            }
        });
      //  from("jpm:publisher")

*/

    }


}