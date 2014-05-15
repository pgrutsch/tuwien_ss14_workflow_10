import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tvgrabber.Producer;
import tvgrabber.TVGrabberConfig;

/**
 * Created by stamm_000 on 15.05.14.
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes = TVGrabberConfig.class)
public class SampleTest {

    @Autowired
    Producer producer;

    @Test
    public void SimpleTest(){
        Assert.assertEquals(true, true);
        //System.out.println("Hello Test");
    }

    @Test
    public void TestForTest(){
        Assert.assertEquals("Hello Test!!", producer.forTest());
    }
}


