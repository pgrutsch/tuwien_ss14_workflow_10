package tvgrabber.routes;



import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import tvgrabber.webservice.soap.Comment;
import tvgrabber.webservice.soap.PostComment;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Component
public class TVGrabberComment extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(TVGrabberComment.class);

    @Override
    public void configure() throws Exception {

        String url = "cxf://http://127.0.0.1:8080/spring-soap/PostComment?serviceClass=" + PostComment.class.getName() +
                "&serviceName=PostComment&endpointName={http://www.tvgrabber.com/soap}TVGrabberSOAP";

        from(url)
                .log(LoggingLevel.INFO, "Receiving SOAP msg from: http://localhost:8080/spring/PostComment")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String content = exchange.getIn().getBody(Comment.class).getContent();
                        logger.debug("PostComment content: " + content);
                    }
                });
    }

}