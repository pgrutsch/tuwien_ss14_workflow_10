package tvgrabber.routes;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import tvgrabber.entities.Comment;
import twitter4j.TwitterException;

/**
 * Created by LeBon on 26.05.14.
 */
@Component
public class TwitterRoute extends RouteBuilder {

    @Autowired
    Environment prop;

    private String twitterAccess = "twitter://timeline/user";

    @Override
    public void configure() throws Exception {

        Predicate isComment = header("type").isEqualTo("comment");

        loadCredentials();

        onException(TwitterException.class).redeliveryDelay(1000).maximumRedeliveries(3).continued(true);

        /* change the time for testing stuff */
        from("{{twitter.seda}}").throttle(1).timePeriodMillis(600000L).asyncDelayed()
                .choice().when(isComment).process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Comment comment = exchange.getIn().getBody(Comment.class);
                String tweet = comment.getComment() + " for " + comment.getTvprogram().getTitle();

                tweet = verifyLength(tweet);

                exchange.getIn().setBody(tweet);

            }
        }).to(twitterAccess)
                .otherwise().setBody(simple("${body.title}"))
                .to(twitterAccess).end();
    }

    private String verifyLength(String tweet) {
        if (tweet.length() >= 140) {
            return tweet.substring(0, 140);
        } else {
            return tweet;
        }
    }

    private void loadCredentials() {
        twitterAccess += "?consumerKey=" + prop.getProperty("oauth.consumerKey");
        twitterAccess += "&consumerSecret=" + prop.getProperty("oauth.consumerSecret");
        twitterAccess += "&accessToken=" + prop.getProperty("oauth.accessToken");
        twitterAccess += "&accessTokenSecret=" + prop.getProperty("oauth.accessTokenSecret");
    }
}
