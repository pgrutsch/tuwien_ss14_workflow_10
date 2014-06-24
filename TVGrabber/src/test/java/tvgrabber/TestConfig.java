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
 * Created by patrickgrutsch on 28.05.14.
 */
@Configuration
@Profile("testing")
@ComponentScan(basePackages = {"tvgrabber"} )
@PropertySource(value = {"classpath:data.properties", "classpath:sql.properties",
        "classpath:facebook4j.properties", "classpath:twitter4j.properties"})
@EnableTransactionManagement
@ImportResource({"classpath:META-INF/cxf/cxf.xml"})
public class TestConfig extends CamelConfiguration {

    private static final Logger logger = Logger.getLogger(TestConfig.class);

    public TestConfig() {
        logger.debug("CREATING NEW TESTCONFIG");
    }

    @Bean
    public DataSource dataSource(){
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("h2testdb")
                .addScript("create.sql").addScript("testinsert.sql")
                .build();
    }

    /*
     * Following error can be safely ignored, because no loadTimeWeaver is defined and enhancement isn't needed.
     * see http://openjpa.apache.org/integration.html
     *
     * 60  camel  WARN   [main] openjpa.Runtime - An error occurred while registering a ClassTransformer with PersistenceUnitInfo: name 'camel-test'
     */

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setDataSource(dataSource());
        lcemfb.setJpaDialect(new OpenJpaDialect());
        lcemfb.setJpaVendorAdapter(jpaVendorAdapter());
        lcemfb.setPackagesToScan("tvgrabber.entities");
        lcemfb.setPersistenceUnitName("camel-test");
        lcemfb.setPersistenceXmlLocation("META-INF/persistence.xml");
        lcemfb.afterPropertiesSet();
        return lcemfb.getObject();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        OpenJpaVendorAdapter jpaVendorAdapter = new OpenJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(true);
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