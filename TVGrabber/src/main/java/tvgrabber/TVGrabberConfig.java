package tvgrabber;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import tvgrabber.routes.TVGrabberComment;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Configuration
@ImportResource({"classpath:META-INF/cxf/cxf.xml"})
//@ComponentScan(basePackages = {"tvgrabber"})
public class TVGrabberConfig extends CamelConfiguration {

    private static final Logger logger = Logger.getLogger(TVGrabberConfig.class);

    /* Don't define Beans here, use @Autowired instead to get instances */

    /* CamelConfiguration's .routes() method has not to be overwritten since 2.13.
       @Component annotation in RouteBuilders and @ComponentScan is used instead
       http://camel.apache.org/spring-java-config.html
     */

    /* Initialize embedded H2 database */
    @Bean
    public DataSource dataSource() {
        logger.debug("Creating DataSource");

        EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
        return embeddedDatabaseBuilder
                .addScript("classpath:sql/create.sql")
                .setName("h2proddb")
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .build();
    }


    @Bean
    public TVGrabberComment tvGrabberComment() {
        return new TVGrabberComment();
    }

    @Override
    public List<RouteBuilder> routes() {
        List<RouteBuilder> routeBuilders = new ArrayList<RouteBuilder>();
        routeBuilders.add(tvGrabberComment());

        return routeBuilders;
    }
}