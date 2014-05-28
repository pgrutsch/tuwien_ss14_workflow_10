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
            "&oAuthAccessToken=CAAFFKBCwdrUBAEuaJfgGrXpNv1S6jgQd5YLVOOhfFruaBAMbWvuTbx8gzrZBqqQD8lLQu15GSiFaNLaqAaZBTX97xRcxZB5Ug5UgZCn6AKU0CpVBd8M3SXklUbpjIUgM8caCzylZC0DLJaWMmKmtb1ZCfD4ZCQUZCBLlweiZAXR33h73RJ3d9ZBdu6";


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
