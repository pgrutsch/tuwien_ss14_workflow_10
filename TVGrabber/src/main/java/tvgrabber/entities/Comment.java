package tvgrabber.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by patrickgrutsch on 18.05.14.
 */

@Entity
@Table(name ="Comment", schema = "TVGRABBER")
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "email", length = 200, nullable = false)
    private String email;

    @Column(name = "comment", nullable = false)
    @Lob //prevent max length of 255
    private String comment;

    @ManyToOne
    @JoinColumn(name = "tvProgram_id")
    private Series series;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getComment() {
        return comment;
    }

    public Series getTvprogram() {
        return series;
    }

    public void setTvprogram(Series tvprogram) {
        this.series = tvprogram;
    }
}
