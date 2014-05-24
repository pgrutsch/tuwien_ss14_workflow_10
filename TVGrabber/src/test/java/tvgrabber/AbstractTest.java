package tvgrabber;

import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Created by patrickgrutsch on 24.05.14.
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = {TVGrabberConfig.class, StandAloneDerby.class})
@ActiveProfiles("testing")
public abstract class AbstractTest {

    // Description of annotations:
    // http://spring.io/blog/2011/06/21/spring-3-1-m2-testing-with-configuration-classes-and-profiles/
}
