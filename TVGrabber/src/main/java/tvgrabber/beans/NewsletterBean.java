package tvgrabber.beans;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tvgrabber.entities.Series;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by Isabella 02.06.2014
 */
@Component
public class NewsletterBean {
    private static final Logger logger = Logger.getLogger(NewsletterBean.class);

    @Autowired
    private DataSource dataSource;

    public void changeHeader(@Headers Map<String, Object> headers, @Body String myBody, Exchange exchange) {
        Series serie= new Series();
        serie = exchange.getIn().getBody(Series.class);

        exchange.getIn().setHeader("title",exchange.getIn().getBody(Series.class).getTitle());
        exchange.getIn().setBody(serie);
    }
}
