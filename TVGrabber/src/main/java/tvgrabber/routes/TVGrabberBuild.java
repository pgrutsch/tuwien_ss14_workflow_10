package tvgrabber.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.spi.DataFormat;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import tvgrabber.entities.Series;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Component
public class TVGrabberBuild extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(TVGrabberBuild.class);

    @Override
    public void configure() throws Exception {

        /* Fetch and parse guide.xml */
        DataFormat jaxbDataFormat = new JaxbDataFormat("tvgrabber.entities");

        from("file://src/tvdata?noop=true&initialDelay=2000&delay=4000&fileName=guide.xml")
                .log(LoggingLevel.INFO, "Loading guide.xml")
                .wireTap("file://archive?fileName=${date:now:yyyyMMdd}_${file:onlyname}")
                .split().tokenizeXML("programme")
                .unmarshal(jaxbDataFormat)
                .filter().method(Series.class, "isSeries") // Content filter
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        logger.debug("Series title: " + exchange.getIn().getBody(Series.class).getTitle());
                        logger.debug("Series desc: " + exchange.getIn().getBody(Series.class).getDesc());
                        logger.debug("Series channel: " + exchange.getIn().getBody(Series.class).getChannel());
                        logger.debug("Series start: " + exchange.getIn().getBody(Series.class).getStart());
                        logger.debug("Series stop: " + exchange.getIn().getBody(Series.class).getStop());
                    }
                })
                .to("jpa://tvgrabber.entities.Series");


        /* Fetch 5 entries every 5 seconds to check if there is really data in the database */
        from("jpa://tvgrabber.entities.Series?consumeDelete=false&maximumResults=5&consumer.delay=5000")
                .log(LoggingLevel.INFO, "Reading from series from TVProgram table")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        logger.info("Series: " + exchange.getIn().getBody(Series.class).getId() + " - "
                                + exchange.getIn().getBody(Series.class).getTitle() + " - "
                                + exchange.getIn().getBody(Series.class).getStart());
                    }
                });

    }


}