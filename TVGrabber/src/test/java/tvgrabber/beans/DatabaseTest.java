package tvgrabber.beans;

import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tvgrabber.TestConfig;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by patrickgrutsch on 27.05.14.
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = {TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testing")
public class DatabaseTest {

    private static final Logger logger = Logger.getLogger(DatabaseTest.class);

    @Autowired
    private DataSource dataSource;

    /**
     * Working with database is now possible with EntityManger.
     *
     * @Autowired
     * private EntityManagerFactory entityManagerFactory;
     *
     * private EntityManger entityManger = entityManagerFactory.createEntityManager();
     */

    @Before
    public void setUp() {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("INSERT INTO TVProgram (title, description, startTime, endTime, channel) values (?,?,?,?,?)");
            ps.setString(1, "my title");
            ps.setString(2, "my desc");
            ps.setTimestamp(3, new Timestamp(new Date().getTime()));
            ps.setTimestamp(4, new Timestamp(new Date().getTime()));
            ps.setString(5, "orf eins");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void shouldDeleteSecondDefaultInsertedSeries() {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("DELETE TVPROGRAM WHERE ID = ?");
            ps.setInt(1, 2);

            int numberDelete = ps.executeUpdate();

            assertEquals(1, numberDelete);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void shouldReturnFirstDefaultInsertedSeries() {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("SELECT * FROM TVPROGRAM WHERE ID = ?");
            ps.setInt(1, 1);

            ResultSet rs = ps.executeQuery();
            rs.next();

            assertEquals("The Simpsons", rs.getString("title"));
            assertEquals("the yellows", rs.getString("description"));
            assertEquals(1, rs.getInt("id"));
            assertEquals("ORF eins", rs.getString("channel"));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void shouldReturnSecondDefaultInsertedSeries() {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("SELECT * FROM TVPROGRAM WHERE ID = ?");
            ps.setInt(1, 2);

            ResultSet rs = ps.executeQuery();
            rs.next();

            assertEquals("Two and a half man", rs.getString("title"));
            assertEquals("Charly in action", rs.getString("description"));
            assertEquals(2, rs.getInt("id"));
            assertEquals("Pro Sieben", rs.getString("channel"));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void shouldReturnFirstDefaultInsertedComment() {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("SELECT * FROM Comment WHERE ID = ?");
            ps.setInt(1, 1);

            ResultSet rs = ps.executeQuery();
            rs.next();

            assertEquals("a@b.com", rs.getString("email"));
            assertEquals("nice series", rs.getString("comment"));
            assertEquals(1, rs.getInt("id"));
            assertEquals(1, rs.getInt("tvProgram_id"));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void shouldReturnSeriesFromSetUp() {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("SELECT * FROM TVPROGRAM WHERE ID = ?");
            ps.setInt(1, 6);

            ResultSet rs = ps.executeQuery();
            rs.next();

            assertEquals("my title", rs.getString("title"));
            assertEquals("my desc", rs.getString("description"));
            assertEquals(6, rs.getInt("id"));
            assertEquals("orf eins", rs.getString("channel"));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
