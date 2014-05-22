package tvgrabber.entities;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * Created by patrickgrutsch on 15.05.14.
 */
@Entity
@Table(name ="TVProgram", schema = "TVGRABBER")
@XmlRootElement(name = "programme")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(factoryClass=ObjectFactory.class, factoryMethod="createSeries")
public class Series implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @XmlElement(name = "title")
    @Column(name = "title", length = 200)
    private String title;

    @XmlElement(name = "desc")
    @Column(name = "description")
    @Lob //prevent max length of 255
    private String desc;

    //TODO: convert to date
    @XmlAttribute(name = "start")
    @Column(name = "startTime", length = 29)
    private String start;

    //TODO: convert to date
    @XmlAttribute(name = "stop")
    @Column(name = "endTime", length = 29)
    private String stop;

    @XmlAttribute(name = "channel")
    @Column(name = "channel", length = 200)
    private String channel;

    public Integer getId() {
        return id;
    }

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
