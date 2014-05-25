package tvgrabber.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Created by patrickgrutsch on 25.05.14.
 *
 * This class can be used to send messages to DEAD_LETTER_CHANNEL
 * if exceptions are thrown.
 *
 * Usage in route builders: .errorHandler(deadLetterChannel(TVGrabberDeadLetter.DEAD_LETTER_CHANNEL))
 */

@Component
public class TVGrabberDeadLetter extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(TVGrabberDeadLetter.class);
    public static final String DEAD_LETTER_CHANNEL = "seda:errors";

    @Override
    public void configure() throws Exception {

        from(DEAD_LETTER_CHANNEL)
                .log(LoggingLevel.INFO, "New message in Dead Letter Channel");
    }
}
