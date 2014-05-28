package tvgrabber.routes;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import tvgrabber.entities.Comment;

/**
 * Created by LeBon on 26.05.14.
 */
@Component
public class TwitterRoute extends RouteBuilder {

    private String twitterAccess = "twitter://timeline/user?consumerKey=K8WGu6kIxeipNv1pYPTA" +
            "&consumerSecret=eS6kq93pT8xnMgK4MKnUR5ilkFExALXrSWiuB1wEXv8" +
            "&accessToken=2289840392-NyRv99h6JGrIe5n5sqNOVZtAsy4BM603ET4X69m" +
            "&accessTokenSecret=hHgTntMJKqPyXf0NwVb7qqEyurZQgpeEMNPpeGbuYFy5H";

    @Override
    public void configure() throws Exception {

        Endpoint twitterEnd = endpoint("seda:twitter");
        Predicate isComment = header("type").isEqualTo("comment");

        //TODO add some more stuff to twitter but verify length in both cases


        /* change the time for testing stuff */
        from(twitterEnd).throttle(1).timePeriodMillis(600000L).asyncDelayed()
                .choice().when(isComment).process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Comment comment = exchange.getIn().getBody(Comment.class);
                String tweet = comment.getComment() + " for " + comment.getTvprogram().getTitle();
                exchange.getIn().setBody(tweet);

            }
        }).to(twitterAccess)
                .otherwise().split(body()).setBody(simple("${body.title}"))
                .to(twitterAccess).end();
    }
}
