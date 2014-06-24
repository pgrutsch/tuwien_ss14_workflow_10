package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import tvgrabber.entities.Series;

/**
 * Created by Isabella 02.06.2014
 */
@Component
public class NewsletterBean {
    private static final Logger logger = Logger.getLogger(NewsletterBean.class);

    public void changeHeader(Exchange exchange) {
        Series serie = exchange.getIn().getBody(Series.class);

        exchange.getIn().setHeader("title", exchange.getIn().getBody(Series.class).getTitle());
        exchange.getIn().setBody(serie);
    }
}
