package tvgrabber;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by patrickgrutsch on 30.04.14.
 */
public class TVGrabberMain extends org.apache.camel.main.Main {

    static final Logger log = Logger.getLogger(TVGrabberMain.class);

    public static void main(String args[]) throws Exception {

        AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext(Config.class);
        springContext.start();

        final Producer producer = springContext.getBean("producer", Producer.class);


        /* SEDA is asynchronous!!!
           use a delay to ensure that the messages are really consumed before the program quits */

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {
                    try {
                        log.info("========= SEND MSG");

                        producer.send("my msg " + Math.random()*100);

                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        }).start();


    }


}
