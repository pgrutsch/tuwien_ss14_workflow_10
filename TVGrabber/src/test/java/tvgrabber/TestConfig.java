package tvgrabber;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * Created by patrickgrutsch on 28.05.14.
 */
@Configuration
@Profile("testing")
@ComponentScan(basePackages = {"tvgrabber"})
public class TestConfig {

    @Bean
    public DataSource dataSource(){
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("h2proddb")
                .addScript("create.sql").addScript("testinsert.sql")
                .build();
    }

}
