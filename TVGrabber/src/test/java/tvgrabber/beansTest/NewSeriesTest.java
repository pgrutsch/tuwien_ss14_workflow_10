package tvgrabber.beansTest;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tvgrabber.AbstractTest;
import tvgrabber.beans.NewSeries;
import tvgrabber.entities.Series;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by LeBon on 26.05.14.
 */

public class NewSeriesTest extends AbstractTest{

    private Exchange exchange;
    private Series series;


    @Autowired
    private NewSeries newSeries;

    @Autowired
    private DataSource  dataSource;


    @Before
    public void setup() throws SQLException {
        newSeries.setCon(dataSource.getConnection());
    }

    @Test
    public void testExistingSeries(){

        exchange = mock(Exchange.class);
        series = new Series();
        series.setTitle("test1");

        Message message = mock(Message.class);
        when(message.getBody(Series.class)).thenReturn(series);

        when(exchange.getIn()).thenReturn(message);
        Assert.assertFalse(newSeries.filterExistingSeries(new HashMap<String, Object>(), exchange));

    }

    @Test
    public void testNonExistingSeries() {

        exchange = mock(Exchange.class);
        series = new Series();
        series.setTitle("testfail3");

        Message message = mock(Message.class);
        when(message.getBody(Series.class)).thenReturn(series);

        when(exchange.getIn()).thenReturn(message);
        Assert.assertTrue(newSeries.filterExistingSeries(new HashMap<String, Object>(), exchange));
    }

    @Test
    public void testMoreThanOneExisting() {
        exchange = mock(Exchange.class);
        series = new Series();
        series.setTitle("test2");

        Message message = mock(Message.class);
        when(message.getBody(Series.class)).thenReturn(series);

        when(exchange.getIn()).thenReturn(message);
        Assert.assertFalse(newSeries.filterExistingSeries(new HashMap<String, Object>(), exchange));

    }


}
