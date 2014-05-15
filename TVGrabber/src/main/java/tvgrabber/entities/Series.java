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

    //TODO: convert to date
    @XmlAttribute(name = "start")
    private String start;

    //TODO: convert to date
    @XmlAttribute(name = "stop")
    private String stop;

    @XmlAttribute(name = "channel")
    private String channel;

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getStart() {
        return start;
    }

    public String getStop() {
        return stop;
    }

    public String getChannel() {
        return channel;
    }
}
