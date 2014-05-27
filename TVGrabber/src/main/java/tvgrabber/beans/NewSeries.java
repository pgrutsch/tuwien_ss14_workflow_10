package tvgrabber.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tvgrabber.TVGrabberMain;
import tvgrabber.entities.Series;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by LeBon on 25.05.14.
 */
@Component
public class NewSeries {

    private static final Logger logger = Logger.getLogger(NewSeries.class);

    private int count = 1;
    private Connection con;

    @Autowired
    private DataSource dataSource;

    /**
     * checks if serie is new
     *
     * @param exchange
     * @return
     */
    public boolean filterExistingSeries(@Headers Map<String, Object> headers, Exchange exchange) {

        Series serie2check = exchange.getIn().getBody(Series.class);

        try {
            if (con == null){
                con = dataSource.getConnection();
            }
            PreparedStatement countSeriesWithSameName =
                    con.prepareStatement("SELECT count(*) from TVProgram where title LIKE ?");
            countSeriesWithSameName.setString(1, "%"+serie2check.getTitle()+"%");
            ResultSet rsSerie = countSeriesWithSameName.executeQuery();

            rsSerie.next();
            count = rsSerie.getInt(1);
            logger.debug("How many similar names: " + count + " " + serie2check.getTitle());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (count == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setCon(Connection con) {
        this.con = con;
    }
}
