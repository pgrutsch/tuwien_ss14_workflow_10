import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tvgrabber.Producer;
import tvgrabber.TVGrabberConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by stamm_000 on 15.05.14.
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = {TVGrabberConfig.class, StandAloneHsql.class})
@ActiveProfiles("testing")
public class SampleTest {

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
            rs = stm.executeQuery("Select email from TVUser");

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
}


