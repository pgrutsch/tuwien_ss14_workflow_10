package tvgrabber.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import tvgrabber.beans.Addressmanager;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Component
public class TVGrabberSubscribe extends RouteBuilder {

    private static final Logger logger = Logger.getLogger(TVGrabberSubscribe.class);

    private @Autowired Environment env;

    @Override
    public void configure() throws Exception {

        /*Recieve mail and distribute to Subscribe / Unsubscribe Queue */
        from("{{subscribe.pop}}")
            .choice().when(header(env.getProperty("subscribe.criteria")).contains(env.getProperty("subscribe.criteriaValue")))
                .to("{{subscribe.unsubscribeQueue}}")
            .otherwise().to("{{subscribe.subscribeQueue}}");


        /* Unsubscribe user  */
        from("{{subscribe.unsubscribeQueue}}").bean(Addressmanager.class, "unsubscribe")
                .to("{{subscribe.jpaUser}}")
                .to("{{subscribe.smtp}}")
                .errorHandler(deadLetterChannel(TVGrabberDeadLetter.DEAD_LETTER_CHANNEL));

        /* Subscribe user  */
        from("{{subscribe.subscribeQueue").bean(Addressmanager.class, "subscribe")
                .to("{{subscribe.jpaUser}}")
                .to("{{subscribe.smtp}}");
        }


    }