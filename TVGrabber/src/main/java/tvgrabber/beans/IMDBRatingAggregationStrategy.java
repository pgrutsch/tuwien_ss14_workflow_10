package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import tvgrabber.entities.Series;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 24.05.14
 * Time: 13:48
 *
 */

public class IMDBRatingAggregationStrategy implements AggregationStrategy {
    private static final Logger logger = Logger.getLogger(IMDBRatingAggregationStrategy.class);

    @Override
    public Exchange aggregate(Exchange original, Exchange resource) {
        Series originalBody = original.getIn().getBody(Series.class);

        String omdbReply = resource.getIn().getBody(String.class);
        InputSource replyIS = new InputSource(new StringReader(omdbReply));

        if(omdbReply.equals("<root response=\"False\"><error>Movie not found!</error></root>")) {
            logger.debug("Error enriching IMDB rating via omdbapi.com: Series not found");
            return original;
        }

        String imdbRating = XPathBuilder.xpath("string(/root/movie[1]/@imdbRating)").evaluate(resource.getContext(), replyIS);
        if(!imdbRating.isEmpty()) {
            originalBody.setImdbRating(Double.parseDouble(imdbRating));
            logger.debug("IMDB Rating for "+ originalBody.getTitle() + " was set to "+ imdbRating);

            if (original.getPattern().isOutCapable()) {
                original.getOut().setBody(originalBody);
            } else {
                original.getIn().setBody(originalBody);
            }
        }


        return original;
    }
}