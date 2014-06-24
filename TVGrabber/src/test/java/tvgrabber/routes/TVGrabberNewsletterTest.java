package tvgrabber.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import tvgrabber.TestConfig;

/**
 * Created by Isabella on 21.06.2014.
 */

//@RunWith(CamelSpringJUnit4ClassRunner.class)
//@ContextConfiguration(
//        classes = {TestConfig.class, TVGrabberNewsletterTest.SpecificTestConfig.class},
//        loader = CamelSpringDelegatingTestContextLoader.class
//)
public class TVGrabberNewsletterTest extends CamelTestSupport{


//    @Autowired
//    private TVGrabberNewsletter TVGrabberNewsletter;
//
//    @Autowired
//    private CamelContext camelContext;
//
//    @Configuration
//    public static class SpecificTestConfig extends SingleRouteCamelConfiguration {
//
//        @Autowired
//        private TVGrabberNewsletter TVGrabberNewsletter ;
//        @Autowired
//        private TVGrabberSubscribe TVGrabberSubscribe;
//
//        @Bean
//        public RouteBuilder route() { /* limits the test class environment to use only one routeBuilder */
//            return TVGrabberNewsletter;
//        }
//    }
//
//    @Override
//    public boolean isUseAdviceWith() { /* defines that tests run with adviceWith */
//        return true;
//    }
//
//    @Override
//    protected RouteBuilder createRouteBuilder() throws Exception { /* returns the route used for the adviceWith tests */
//        return TVGrabberNewsletter;
//    }
//
//    @Override
//    protected CamelContext createCamelContext() throws Exception {
//        return camelContext;
//    }

    /*
    Tests
     */

    //keinen tau wie man das testen soll?????

}
