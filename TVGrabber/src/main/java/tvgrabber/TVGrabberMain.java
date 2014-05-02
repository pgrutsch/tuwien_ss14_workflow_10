package tvgrabber;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.PollingConsumer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import tvgrabber.routes.TVGrabberRouteBuilder;

/**
 * Created by patrickgrutsch on 30.04.14.
 */
@Component
public class TVGrabberMain extends org.apache.camel.main.Main {

    private static final Logger logger = Logger.getLogger(TVGrabberMain.class);

    @Autowired
    private  Producer producer;

    public static void main(String args[]) throws Exception {

        AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext(TVGrabberConfig.class);

        /* Use getBean to start the application context
           Using new TVGrabberMain() would cause @Autowired not to work, because it's not started within the spring context
         */
        TVGrabberMain tvGrabberMain = springContext.getBean(TVGrabberMain.class);
        tvGrabberMain.startApp();

    }

    public void startApp() {

       /* SEDA is asynchronous!
       use a delay to ensure that the messages are really consumed before the program quits */

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {
                    try {
                        logger.info("========= SEND MSG");

                        producer.send("my msg " + Math.random()*100);

                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }


                }
            }
        }).start();


    }


}
