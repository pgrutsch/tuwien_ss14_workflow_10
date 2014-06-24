package tvgrabber.routes;

import facebook4j.FacebookException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import tvgrabber.entities.Series;

/**
 * Created by LeBon on 27.05.14.
 */
@Component
public class FacebookRoute extends RouteBuilder {

    @Autowired
    private Environment prop;

    private String facebookRout = "facebook://postStatusMessage?inBody=message";

    @Override
    public void configure() throws Exception {

        loadCredentials();
        onException(FacebookException.class).redeliveryDelay(1000).maximumRedeliveries(3).continued(true);

        /*the throttle denies the spamming in fb*/
        from("{{facebook.seda}}").throttle(1).timePeriodMillis(600000L).asyncDelayed()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String title = exchange.getIn().getBody(Series.class).getTitle();
                        exchange.getIn().setBody(title);
                    }
                })
                .to(facebookRout);
    }

    private void loadCredentials() {
        facebookRout += "&userId=" + prop.getProperty("userID");
        facebookRout += "&oAuthAppId=" + prop.getProperty("oAuthAppId");
        facebookRout += "&oAuthAppSecret=" + prop.getProperty("oAuthAppSecret");
        facebookRout += "&oAuthAccessToken=" + prop.getProperty("oAuthAccessToken");

    }


}
