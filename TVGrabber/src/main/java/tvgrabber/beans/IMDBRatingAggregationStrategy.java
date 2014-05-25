package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
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
    private XPath xPath = XPathFactory.newInstance().newXPath();

    @Override
    public Exchange aggregate(Exchange original, Exchange resource) {
        //System.out.println(resource.getIn().getBody(String.class));

        Series originalBody = original.getIn().getBody(Series.class);

        String omdbReply = resource.getIn().getBody(String.class);

        try {
            String imdbRating = xPath.evaluate("string(/root/movie[1]/@imdbRating)", new InputSource(new StringReader(omdbReply)));
            if(!imdbRating.isEmpty()) {
                originalBody.setImdbRating(Double.parseDouble(imdbRating));
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        };

        if (original.getPattern().isOutCapable()) {
            original.getOut().setBody(originalBody);
        } else {
            original.getIn().setBody(originalBody);
        }

        return original;
    }
}
