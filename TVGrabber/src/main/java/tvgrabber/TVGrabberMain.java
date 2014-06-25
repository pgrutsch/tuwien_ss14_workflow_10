package tvgrabber;

import org.apache.camel.spring.Main;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by patrickgrutsch on 30.04.14.
 */
public class TVGrabberMain extends Main {

    private static final Logger logger = Logger.getLogger(TVGrabberMain.class);

    public static void main(String args[]) {

        System.setProperty("spring.profiles.active", "production");
        AnnotationConfigApplicationContext ctx =new AnnotationConfigApplicationContext(TVGrabberConfig.class);
        TVGrabberMain m = new TVGrabberMain();
        m.setApplicationContext(ctx);
        m.startApp();
    }

    public void startApp() {
        super.enableHangupSupport();

        try {
            super.run();
        } catch (Exception e) {
            logger.error("Error starting app");
            logger.error(e.toString());
        }
    }

}
