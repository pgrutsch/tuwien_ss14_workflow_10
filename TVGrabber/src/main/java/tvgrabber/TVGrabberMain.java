package tvgrabber;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

/**
 * Created by patrickgrutsch on 30.04.14.
 */
@Component
public class TVGrabberMain extends org.apache.camel.main.Main {

    private static final Logger logger = Logger.getLogger(TVGrabberMain.class);

    public static void main(String args[]) {

        System.setProperty("spring.profiles.active", "production");
        new AnnotationConfigApplicationContext(TVGrabberConfig.class);
        new TVGrabberMain().startApp();
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
