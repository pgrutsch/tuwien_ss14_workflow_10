package tvgrabber;

import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by stamm_000 on 15.05.14.
 */
public class SampleTest extends AbstractTest {

    @Autowired
    Producer producer;

    @Autowired
    DataSource dataSource;

    @Test
    public void SimpleTest() {
        Assert.assertEquals(true, true);
        //System.out.println("Hello Test");
    }

    @Test
    public void TestForTest() {
        Assert.assertEquals("Hello Test!!", producer.forTest());
    }

    @Test
    public void TestDB() {

        Connection cs = null;
        Statement stm = null;
        ResultSet rs = null;

        try {
            cs = dataSource.getConnection();
            stm = cs.createStatement();
            rs = stm.executeQuery("Select email from TVGRABBER.TVUser");

            rs.next();

            Assert.assertEquals("bla", rs.getString("email"));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stm != null) stm.close();
                if (cs != null) cs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void MockitoSampleTest() throws Exception {
        // for further reading:
        // http://docs.mockito.googlecode.com/hg-history/be6d53f62790ac7c9cf07c32485343ce94e1b563/1.9.5/org/mockito/Mockito.html

        String msgToSend = "Hi, I am a Test.";

        ProducerTemplate prodtemp = mock(ProducerTemplate.class);

        producer.setProducer(prodtemp);
        producer.send(msgToSend);

        verify(prodtemp).sendBody(msgToSend);

    }
}


