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

        try {
            clearDatabase();
        } catch (SQLException e) {
            logger.error("Error clearing Database");
            e.printStackTrace();
            return;
        }

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


    /* Clear all tables.
       As we use an embedded database all the db files are stored in the target folder.
       This cleaning allows you running the app without deleting the target folder every run.
     */
    private void clearDatabase() throws SQLException {
        /*
        logger.debug("Getting Database connection");

        Connection dbConn = getConnection();
        Statement statement = dbConn.createStatement();

        logger.debug("Executing delete statements");
        try {
            statement.executeUpdate("DELETE FROM TVGRABBER.Comment");
        } catch (SQLException e) {}

        try {
            statement.executeUpdate("DELETE FROM TVGRABBER.TVProgram");
        } catch (SQLException e) {}

        try {
            statement.executeUpdate("DELETE FROM TVGRABBER.TVUser");
        } catch (SQLException e) {}

        statement.close();
        dbConn.close();*/

    }

    public static Connection getConnection() throws SQLException{
        //return DriverManager.getConnection("jdbc:derby:target/derby;create=true");
        return DriverManager.getConnection("jdbc:h2:./mem:h2mem");
    }

}
