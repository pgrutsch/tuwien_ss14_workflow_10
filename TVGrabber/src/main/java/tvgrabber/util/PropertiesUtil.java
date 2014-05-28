package tvgrabber.util;

import org.springframework.stereotype.Component;
import tvgrabber.TVGrabberMain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Cle
 * Date: 28.05.2014
 */
@Component
public class PropertiesUtil {

    private Properties properties;
    private Properties sqlProperties;

     public PropertiesUtil() throws IOException {
         properties = new Properties();
         sqlProperties = new Properties();

         InputStream input = TVGrabberMain.class.getClassLoader().getResourceAsStream("data.properties");
         InputStream sqlInput = TVGrabberMain.class.getClassLoader().getResourceAsStream("sql.properties");

         properties.load(input);
         sqlProperties.load(sqlInput);
         sqlInput.close();
         input.close();
    }

    public String getProperty(String propertyName){
        return properties.getProperty(propertyName);
    }

    public String getSQL(String sqlName){
        return sqlProperties.getProperty(sqlName);
    }
}
