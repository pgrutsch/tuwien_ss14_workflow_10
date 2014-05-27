package tvgrabber;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Configuration
@ImportResource({"classpath:META-INF/cxf/cxf.xml"})
@ComponentScan(basePackages = {"tvgrabber"})
public class TVGrabberConfig {

    private static final Logger logger = Logger.getLogger(TVGrabberConfig.class);


    /* Don't define Beans here, use @Autowired instead to get instances */


    /* Initialize embedded Derby database */
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

}