package tvgrabber.entities;

import org.apache.camel.Exchange;
import tvgrabber.beans.XMLDateAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private String title ;

    @XmlElement(name = "desc")
    @Column(name = "description")
    @Lob //prevent max length of 255
    private String desc;

    @XmlElement(name = "category")
    @Transient // @Column(name = "category") - not stored in DB
    private List<String> category = new ArrayList<String>();

    @XmlAttribute(name = "start")
    @Column(name = "startTime", length = 29)
    @XmlJavaTypeAdapter(XMLDateAdapter.class)
    private Date start;

    @XmlAttribute(name = "stop")
    @Column(name = "endTime", length = 29)
    @XmlJavaTypeAdapter(XMLDateAdapter.class)
    private Date stop;

    @XmlAttribute(name = "channel")
    @Column(name = "channel", length = 200)
    private String channel;

    public List<String> getCategory() {
        return category;
    }

    public boolean isSeries(Exchange exchange) {
        return exchange.getIn().getBody(Series.class).getCategory().contains("Serie");
    }


    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public Date getStart() {
        return start;
    }

    public Date getStop() {
        return stop;
    }

    public String getChannel() {
        return channel;
    }
}
