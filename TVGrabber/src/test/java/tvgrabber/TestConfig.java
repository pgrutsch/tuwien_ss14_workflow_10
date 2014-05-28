package tvgrabber;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tvgrabber.routes.TVGrabberComment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrickgrutsch on 28.05.14.
 */
@Configuration
public class TestConfig extends CamelConfiguration {

    @Bean
    public TVGrabberComment tvGrabberComment() {
        return new TVGrabberComment();
    }

    @Override
    public List<RouteBuilder> routes() {
        List<RouteBuilder> routes = new ArrayList<RouteBuilder>();
        routes.add(tvGrabberComment());
        return routes;
    }
}
