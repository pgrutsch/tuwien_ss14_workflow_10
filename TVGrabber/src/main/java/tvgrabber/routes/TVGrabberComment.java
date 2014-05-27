package tvgrabber.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tvgrabber.beans.CommentBean;
import tvgrabber.entities.Comment;
import tvgrabber.webservice.soap.PostComment;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Component
public class TVGrabberComment extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(TVGrabberComment.class);

    @Autowired
    private CommentBean commentBean;

    @Override
    public void configure() throws Exception {

        String url = "cxf://http://localhost:8080/spring-soap/PostComment?serviceClass=" + PostComment.class.getName();

        from(url)
                .log(LoggingLevel.INFO, "Receiving SOAP msg from http://localhost:8080/spring-soap/PostComment")
                .errorHandler(deadLetterChannel(TVGrabberDeadLetter.DEAD_LETTER_CHANNEL))
                .bean(commentBean)
                .recipientList(header("recipients"))
                .parallelProcessing();



        from("jpa://tvgrabber.entities.Comment?consumeDelete=false&maximumResults=5&consumer.delay=7000")
                .errorHandler(deadLetterChannel(TVGrabberDeadLetter.DEAD_LETTER_CHANNEL))
                .log(LoggingLevel.INFO, "Reading comments from DB")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {

                        logger.debug("Comment: " + exchange.getIn().getBody(Comment.class).getComment());
                    }
                });

    }

}