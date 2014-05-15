package tvgrabber.entities;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * Created by patrickgrutsch on 15.05.14.
 */
@Entity
@Table(name ="TVProgram")
@XmlRootElement(name = "programme")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(factoryClass=ObjectFactory.class, factoryMethod="createSeries")
public class Series implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @XmlElement(name = "title")
    @Column(name = "title")
    private String title;

    @XmlElement(name = "desc")
    @Column(name = "description")
    @Lob //prevent max length of 255
    private String desc;

    //TODO: convert to date
    @XmlAttribute(name = "start")
    @Column(name = "startTime")
    private String start;

    //TODO: convert to date
    @XmlAttribute(name = "stop")
    @Column(name = "endTime")
    private String stop;

    @XmlAttribute(name = "channel")
    @Column(name = "channel")
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
