package tvgrabber.entities;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * Created by patrickgrutsch on 15.05.14.
 */
@XmlRegistry
public class ObjectFactory {


    public Series getSeries() {
        return new Series();
    }
}
