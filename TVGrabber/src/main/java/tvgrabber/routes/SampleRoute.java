package tvgrabber.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Created by stamm_000 on 16.05.14.
 */
@Component
public class SampleRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:test").filter(header("foo").isEqualTo("bar")).to("direct:result");
    }
}
