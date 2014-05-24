package tvgrabber.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import tvgrabber.beans.CommentBean;
import tvgrabber.entities.Comment;
import tvgrabber.webservice.soap.PostComment;
import tvgrabber.webservice.soap.SOAPComment;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Component
public class TVGrabberComment extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(TVGrabberComment.class);

    @Override
    public void configure() throws Exception {

        String url = "cxf://http://localhost:8080/spring-soap/PostComment?serviceClass=" + PostComment.class.getName();

        from(url)
                .log(LoggingLevel.INFO, "Receiving SOAP msg from http://localhost:8080/spring-soap/PostComment")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String comment = exchange.getIn().getBody(SOAPComment.class).getComment();
                        String email = exchange.getIn().getBody(SOAPComment.class).getEmail();
                        int tvprogram_id = exchange.getIn().getBody(SOAPComment.class).getTvprogram();

                        logger.debug("Postcomment = Email: " + email + " TVProgramm id: " + tvprogram_id + " Comment: " + comment);
                    }
                })
                .bean(CommentBean.class)
                .recipientList(header("recipients")).parallelProcessing();


        from("jpa://tvgrabber.entities.Comment?consumeDelete=false&maximumResults=5&consumer.delay=7000")
                .log(LoggingLevel.INFO, "Reading comments from DB")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {

                        logger.debug("Comment: " + exchange.getIn().getBody(Comment.class).getComment());
                    }
                });
    }

}