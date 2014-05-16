package tvgrabber;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * Created by patrickgrutsch on 30.04.14.
 */
@Component
public class TVGrabberMain extends org.apache.camel.main.Main {

    private static final Logger logger = Logger.getLogger(TVGrabberMain.class);

    public static void main(String args[]) throws Exception {

        AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext(TVGrabberConfig.class);

        /* Use getBean to start the application context
           Using new TVGrabberMain() would cause @Autowired not to work, because it's not started within the spring context
         */
        TVGrabberMain tvGrabberMain = springContext.getBean(TVGrabberMain.class);
        tvGrabberMain.startApp();

    }

    public void startApp() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                new Scanner(System.in).nextLine();
                enableHangupSupport();
            }
        }).start();

    }

}
