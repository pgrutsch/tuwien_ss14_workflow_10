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
    private Properties twitterProperties;
    private Properties facebookProperties;

    public PropertiesUtil() throws IOException {
        properties = new Properties();
        sqlProperties = new Properties();
        twitterProperties = new Properties();
        facebookProperties = new Properties();

        InputStream input = TVGrabberMain.class.getClassLoader().getResourceAsStream("data.properties");
        InputStream sqlInput = TVGrabberMain.class.getClassLoader().getResourceAsStream("sql.properties");
        InputStream in = TVGrabberMain.class.getClassLoader().getResourceAsStream("twitter4j.properties");


        properties.load(input);
        sqlProperties.load(sqlInput);
        twitterProperties.load(in);

        in = TVGrabberMain.class.getClassLoader().getResourceAsStream("facebook4j.properties");
        facebookProperties.load(in);

        sqlInput.close();
        input.close();
        in.close();
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public String getSQL(String sqlName) {
        return sqlProperties.getProperty(sqlName);
    }

    public String getTwitterProperty(String twitterProp) {
        return twitterProperties.getProperty(twitterProp);

    }

    public String getFaceBookProperty(String fbProp) {
        return facebookProperties.getProperty(fbProp);
    }


}
