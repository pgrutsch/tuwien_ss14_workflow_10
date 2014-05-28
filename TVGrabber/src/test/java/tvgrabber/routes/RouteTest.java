package tvgrabber.routes;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tvgrabber.StandAloneTestH2;
import tvgrabber.TVGrabberConfig;

/**
 * Created by patrickgrutsch on 28.05.14.
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {StandAloneTestH2.class},
        loader = AnnotationConfigContextLoader.class
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testing")
public class RouteTest extends CamelTestSupport {

    @Test
    public void testIsUseAdviceWith() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                // replace the from with seda:foo
                replaceFromWith("seda:foo");
            }
        });
        // we must manually start when we are done with all the advice with
        context.start();

        getMockEndpoint("mock:result").expectedMessageCount(1);

        template.sendBody("seda:foo", "Hello World");

        assertMockEndpointsSatisfied();
    }

    @Override
    public boolean isUseAdviceWith() {
        // tell we are using advice with, which allows us to advice the route
        // before Camel is being started, and thus can replace activemq with something else.
        return true;
    }

    // This is the route we want to test
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // we do not have activemq on the classpath
                // but the route has it included
                from("activemq:queue:foo")
                        .to("mock:result");
            }
        };
    }
}
