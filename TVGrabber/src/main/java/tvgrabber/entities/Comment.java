package tvgrabber.entities;

import javax.persistence.*;

/**
 * Created by patrickgrutsch on 18.05.14.
 */

@Entity
@Table(name ="Comment", schema = "TVGRABBER")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "comment")
    @Lob //prevent max length of 255
    private String comment;

    @Column(name = "tvprogram")
    private Integer tvprogram;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTvprogram(Integer tvprogram) {
        this.tvprogram = tvprogram;
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

    public Integer getTvprogram() {
        return tvprogram;
    }
}
