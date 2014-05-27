package tvgrabber.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import tvgrabber.entities.Series;

/**
 * Created by LeBon on 27.05.14.
 */
@Component
public class FacebookRoute extends RouteBuilder {

    private  String facebookRout = "facebook://postStatusMessage?inBody=message&userId=100007112160276" +
            "&oAuthAppId=357513357719221" +
            "&oAuthAppSecret=4d027c8b2e01fa43afe7321907c22d5f" +
            "&oAuthAccessToken=CAAFFKBCwdrUBAFpE0JGUkzEdpbplPN7VMtJRrZC8yTCEeQK6ArrmUVv80ZCsBvRYukLkZCwHoEEUJSORXwZCTBGSQlqAgxZAeJ4b2HAXGIPc5kFGL1ztOTC89Sr2ID7O5ooiWAJODZC56e4dBZBuRVVLolMGBu13XwjoGO3eZA1C5SnhdNoBOsbjw16BjIUKqvUZD";


    @Override
    public void configure() throws Exception {

        /*the throttle denies the spamming in fb*/
        from("seda:facebook").throttle(1).timePeriodMillis(600000L).asyncDelayed()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {

                        //TODO enrich the message if needed
                        String title = exchange.getIn().getBody(Series.class).getTitle();
                        exchange.getIn().setBody(title);
                    }
                })
                .to(facebookRout);
    }
}
