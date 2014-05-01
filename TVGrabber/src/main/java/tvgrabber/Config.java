package tvgrabber;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Configuration
@ImportResource({"classpath:/camel-config.xml"})
@ComponentScan(basePackages = {"tvgrabber"})
public class Config {


    @Bean
    public Producer producer() {
        return new Producer();
    }


}
