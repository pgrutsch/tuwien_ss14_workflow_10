package tvgrabber.entities;

import javax.xml.bind.annotation.*;

/**
 * Created by patrickgrutsch on 15.05.14.
 */
@XmlRootElement(name = "programme")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(factoryClass=ObjectFactory.class, factoryMethod="createSeries")
public class Series {

    @XmlElement(name = "title")
    private String title;

    @XmlElement(name = "desc")
    private String desc;

    public String getTitle() {
        return this.title;
    }
}
