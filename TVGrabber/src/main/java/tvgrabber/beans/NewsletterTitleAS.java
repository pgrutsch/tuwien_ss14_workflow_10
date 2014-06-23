package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import tvgrabber.entities.Series;

/**
 * Created with IntelliJ IDEA.
 * User: Isabella
 * Date: 05.06.14
 * Time: 15:08
 * Aggregate the Messages with the same title in one Message (same serie)
 */
@Component
public class NewsletterTitleAS implements AggregationStrategy {

    private static final Logger logger = Logger.getLogger(NewsletterTitleAS.class);

    public Exchange aggregate(Exchange OldExchange, Exchange NewExchange) {
        String sTitle="";
        String sNewsletter="";

        if(OldExchange!=null)
        {
            sNewsletter=OldExchange.getIn().getBody(String.class);

        }else
        {
            sTitle=NewExchange.getIn().getBody(Series.class).getTitle();
            sNewsletter= "$" + sTitle + "$" + '\n';
        }
        sNewsletter=sNewsletter+NewExchange.getIn().getBody(Series.class).getStart().toString() + " - " +
                NewExchange.getIn().getBody(Series.class).getStop().toString()+
                " ## Rating - " + NewExchange.getIn().getBody(Series.class).getImdbRating().toString()
                + " ##"  + " ** Channel - " + NewExchange.getIn().getBody(Series.class).getChannel() + " **"
                + '\n';

        NewExchange.getIn().setBody(sNewsletter);
        NewExchange.getIn().setHeader("title",NewExchange.getIn().getHeader("title"));
        return NewExchange;
    }
}
