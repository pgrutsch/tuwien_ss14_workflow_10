package tvgrabber.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by patrickgrutsch on 15.05.14.
 */
@XmlRootElement(name = "programme", namespace="")
@XmlAccessorType(XmlAccessType.FIELD)
public class Series {

    @XmlElement(name = "title")
    private String title;

    @XmlElement(name = "desc")
    private String desc;

    public String getTitle() {
        return this.title;
    }
}
