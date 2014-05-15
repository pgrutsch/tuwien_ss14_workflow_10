package tvgrabber.entities;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * Created by patrickgrutsch on 15.05.14.
 */
@XmlRegistry
public class ObjectFactory {

    /* Factory methods MUST be public, static, zero argument */

    public static Series createSeries() {
        return new Series();
    }
}
