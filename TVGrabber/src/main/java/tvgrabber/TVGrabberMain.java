package tvgrabber;

import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import tvgrabber.routes.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrickgrutsch on 30.04.14.
 */
@Component
public class TVGrabberMain extends org.apache.camel.main.Main {

    private static final Logger logger = Logger.getLogger(TVGrabberMain.class);

    @Autowired
    private TVGrabberBuild tvGrabberBuild;
    @Autowired
    private TVGrabberComment tvGrabberComment;
    @Autowired
    private TVGrabberNewsletter tvGrabberNewsletter;
    @Autowired
    private TVGrabberSubscribe tvGrabberSubscribe;
    @Autowired
    private TVGrabberDeadLetter tvGrabberDeadLetter;

    public static void main(String args[]) {

        AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext(TVGrabberConfig.class);

        /* Use getBean to start the application context
           Using new TVGrabberMain() would cause @Autowired not to work, because it's not started within the spring context
         */
        TVGrabberMain tvGrabberMain = springContext.getBean(TVGrabberMain.class);
        tvGrabberMain.startApp();
    }

    public void startApp() {

        List<RouteBuilder> routeBuilders = new ArrayList<RouteBuilder>();
        routeBuilders.add(tvGrabberBuild);
        routeBuilders.add(tvGrabberComment);
        routeBuilders.add(tvGrabberNewsletter);
        routeBuilders.add(tvGrabberSubscribe);
        routeBuilders.add(tvGrabberDeadLetter);

        super.setRouteBuilders(routeBuilders);
        super.enableHangupSupport();

        try {
            super.run();
        } catch (Exception e) {
            logger.error("Error starting app");
            e.printStackTrace();
            return;
        }

    }

}
