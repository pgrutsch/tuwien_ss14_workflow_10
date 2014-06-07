package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;
import tvgrabber.entities.Series;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Isabella
 * Date: 05.06.14
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class NewsletterFullAS implements AggregationStrategy {

    private static final Logger logger = Logger.getLogger(NewsletterFullAS.class);

    public Exchange aggregate(Exchange OldExchange, Exchange NewExchange) {
        List<Series> mySeries = new ArrayList<Series>();
        String sFirstTitel="";
        String sSecTitel="";
        String firstMessage="";
        String secMessage="";
        String sNewsletter="";

        if(OldExchange!=null)
        {
            firstMessage=OldExchange.getIn().getBody(String.class);
        }
        secMessage=NewExchange.getIn().getBody(String.class);

        sNewsletter=firstMessage + '\n' + secMessage; // + '\n';

        NewExchange.getIn().setBody(sNewsletter);
        NewExchange.getIn().setHeader("title",NewExchange.getIn().getHeader("title"));

        return NewExchange;
    }
}
