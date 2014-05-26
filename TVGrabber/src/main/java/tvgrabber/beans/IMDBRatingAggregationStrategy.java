package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import tvgrabber.entities.Series;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
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
    private XPath xPath = XPathFactory.newInstance().newXPath();

    @Override
    public Exchange aggregate(Exchange original, Exchange resource) {
        Series originalBody = original.getIn().getBody(Series.class);

        String omdbReply = resource.getIn().getBody(String.class);

        if(omdbReply.equals("<root response=\"False\"><error>Movie not found!</error></root>")) {
            logger.debug("Error enriching IMDB rating via omdbapi.com: Series not found");
            return original;
        }

        try {
            String imdbRating = xPath.evaluate("string(/root/movie[1]/@imdbRating)", new InputSource(new StringReader(omdbReply)));
            if(!imdbRating.isEmpty()) {
                originalBody.setImdbRating(Double.parseDouble(imdbRating));
                logger.debug("IMDB Rating for "+ originalBody.getTitle() + " was set to "+ imdbRating);

                if (original.getPattern().isOutCapable()) {
                    original.getOut().setBody(originalBody);
                } else {
                    original.getIn().setBody(originalBody);
                }
            }
        } catch (XPathExpressionException e) {
            logger.error("Error parsing response from omdbapi.com while enriching IMDB rating");
            logger.error(e.getStackTrace());
        };

        return original;
    }
}
