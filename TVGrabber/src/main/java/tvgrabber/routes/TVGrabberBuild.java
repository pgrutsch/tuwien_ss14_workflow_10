package tvgrabber.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpOperationFailedException;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.spi.DataFormat;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import tvgrabber.beans.IMDBRatingAggregationStrategy;
import tvgrabber.beans.NewSeries;
import tvgrabber.entities.Series;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Component
public class TVGrabberBuild extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(TVGrabberBuild.class);

    @Override
    public void configure() throws Exception {
        onException(HttpOperationFailedException.class)
                .maximumRedeliveries(0)
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        log.error("ERROR while connecting to omdbapi.com API to retrieve IMDB rating");
                        log.error(exchange.getException().getMessage());
                    }
                }).handled(true);

       errorHandler(deadLetterChannel(TVGrabberDeadLetter.DEAD_LETTER_CHANNEL));


        DataFormat jaxbDataFormat = new JaxbDataFormat("tvgrabber.entities");

        from("{{build.file}}")
                .log(LoggingLevel.INFO, "Loading guide.xml")
                .wireTap("{{build.wireTapArchive}}")
                .split().tokenizeXML("programme")
                .unmarshal(jaxbDataFormat)
                .filter().method(Series.class, "isSeries") // Content filter
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Series msg = exchange.getIn().getBody(Series.class);

                        String encodedTitle = msg.getTitle().replaceAll("[^ a-zA-Z0-9-_]", "");
                        exchange.getIn().setHeader("seriesTitle", encodedTitle);

                        logger.debug("Series title: " + msg.getTitle());
                        logger.debug("Series desc: " + msg.getDesc());
                        logger.debug("Series imdbRating: " + msg.getImdbRating());
                        logger.debug("Series channel: " +msg.getChannel());
                        logger.debug("Series start: " + msg.getStart());
                        logger.debug("Series stop: " + msg.getStop());
                    }
                })
                .multicast()
                .to("{{socialMedia.seda}}")
                .to("seda:waitingForEnrichment");


        IMDBRatingAggregationStrategy aggregationStrategy = new IMDBRatingAggregationStrategy();
        from("seda:waitingForEnrichment")
                .setHeader(Exchange.HTTP_QUERY, simple("t=${header.seriesTitle}&r=xml"))
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                //.throttle(1).asyncDelayed() // Throttler EIP to avoid overloading omdbapi (1 request per second allowed)
                //.enrich("http://omdbapi.com/", aggregationStrategy)
                .to("{{build.jpaSeries}}");


        /* Fetch 5 entries every 5 seconds to check if there is really data in the database */

        from("{{build.consumeSeries}}")
                .log(LoggingLevel.INFO, "Reading series from TVProgram table")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        logger.info("Series: " + exchange.getIn().getBody(Series.class).getId() + " - "
                                + exchange.getIn().getBody(Series.class).getTitle() + " - "
                                + exchange.getIn().getBody(Series.class).getStart() + " (imdbRating: "
                                + exchange.getIn().getBody(Series.class).getImdbRating() + ")");
                    }
                });

       from("{{socialMedia.seda}}").filter().method(NewSeries.class, "filterExistingSeries")
//                .multicast()
//                .to("{{facebook.seda}}")
                .to("{{twitter.seda}}");

    }


}