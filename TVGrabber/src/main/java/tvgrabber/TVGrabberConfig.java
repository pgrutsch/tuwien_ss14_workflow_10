package tvgrabber;

import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.log4j.Logger;
import org.apache.openjpa.jdbc.sql.H2Dictionary;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.OpenJpaDialect;
import org.springframework.orm.jpa.vendor.OpenJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Created by patrickgrutsch on 30.04.14.
 */

@Configuration
@PropertySource(value = {"classpath:data.properties", "classpath:sql.properties",
                "classpath:facebook4j.properties", "classpath:twitter4j.properties"})
@ImportResource({"classpath:META-INF/cxf/cxf.xml"})
@ComponentScan(basePackages = {"tvgrabber"})
@EnableTransactionManagement
@Profile("production")
public class TVGrabberConfig extends CamelConfiguration {

    private static final Logger logger = Logger.getLogger(TVGrabberConfig.class);

    public TVGrabberConfig() {
        logger.debug("CREATING NEW TVGRABBERCONFIG");
    }

    /* Don't define Beans here, use @Autowired instead to get instances */

    /* CamelConfiguration's .routes() method has not to be overwritten since 2.13.
       @Component annotation in RouteBuilders and @ComponentScan is used instead
       http://camel.apache.org/spring-java-config.html
     */

    /* Initialize embedded H2 database */
    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
        return embeddedDatabaseBuilder
                .addScript("classpath:sql/create.sql")
                .setName("h2proddb")
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .build();
    }

    /*
     * Following error can be safely ignored, because no loadTimeWeaver is defined and enhancement isn't needed.
     * see http://openjpa.apache.org/integration.html
     *
     * 60  camel  WARN   [main] openjpa.Runtime - An error occurred while registering a ClassTransformer with PersistenceUnitInfo: name 'camel'
     */

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setDataSource(dataSource());
        lcemfb.setJpaDialect(new OpenJpaDialect());
        lcemfb.setJpaVendorAdapter(jpaVendorAdapter());
        lcemfb.setPackagesToScan("tvgrabber.entities");
        lcemfb.setPersistenceUnitName("camel");
        lcemfb.setPersistenceXmlLocation("classpath:META-INF/persistence.xml");
        lcemfb.afterPropertiesSet();
        return lcemfb.getObject();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        OpenJpaVendorAdapter jpaVendorAdapter = new OpenJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(false);
        jpaVendorAdapter.setDatabase(Database.H2);
        jpaVendorAdapter.setDatabasePlatform(H2Dictionary.class.getName());
        jpaVendorAdapter.setGenerateDdl(false);
        return jpaVendorAdapter;
    }

    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory());
        return jpaTransactionManager;
    }

    @Bean
    public static PropertiesComponent properties() throws Exception {
        PropertiesComponent pc = new PropertiesComponent();
        String[] locations= {"classpath:data.properties", "classpath:sql.properties",
                "classpath:facebook4j.properties", "classpath:twitter4j.properties"};
        pc.setLocations(locations);
        return pc;
    }

}