package tvgrabber.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import tvgrabber.beans.CommentBean;
import tvgrabber.beans.CommentValidator;
import tvgrabber.entities.Comment;
import tvgrabber.webservice.soap.PostComment;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Component
public class TVGrabberComment extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(TVGrabberComment.class);
    private static final String CXFENDPOINT = "cxf:http://localhost:8080/spring-soap/PostComment?serviceClass="
            + PostComment.class.getName();
    private static final String JPAENDPOINT = "jpa://tvgrabber.entities.Comment?consumeDelete=false&maximumResults=5&consumer.delay=7000";

    @Override
    public void configure() throws Exception {

        from(CXFENDPOINT)
                .log(LoggingLevel.INFO, "Receiving new SOAP msg from http://localhost:8080/spring-soap/PostComment")
                .errorHandler(deadLetterChannel(TVGrabberDeadLetter.DEAD_LETTER_CHANNEL))
                .bean(CommentValidator.class)
                .bean(CommentBean.class)
                .recipientList(header("recipients"))
                .parallelProcessing();


        from(JPAENDPOINT)
                .errorHandler(deadLetterChannel(TVGrabberDeadLetter.DEAD_LETTER_CHANNEL))
                .log(LoggingLevel.INFO, "Reading comments from DB")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Comment c = exchange.getIn().getBody(Comment.class);
                        logger.debug("Comment ID : " + c.getId() + ", TVProgram_ID: " + c.getTvprogram().getId()
                                + ", Email: " + c.getEmail() + ", Content: " + c.getComment());
                    }
                });
    }

}