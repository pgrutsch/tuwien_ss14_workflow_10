import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tvgrabber.TVGrabberConfig;

/**
 * Created by patrickgrutsch on 24.05.14.
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = {TVGrabberConfig.class, StandAloneDerby.class})
@ActiveProfiles("testing")
public class CommentRouteTest {

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void shouldReceiveSOAPMessage() {

    }

    @Test
    public void shouldTransformSOAPCommentToComment() {

    }

    @Test
    public void shouldSaveCommentToDB() {

    }

    @Test
    public void shouldSendCommentToTwitter() {

    }
}
