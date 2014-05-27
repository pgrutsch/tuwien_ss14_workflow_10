package tvgrabber.entities;


import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Cle
 * Date: 25.05.2014
 */
@Entity
@Table(name="TVUser")
public class TVGrabberUser implements Serializable {

    public TVGrabberUser() {
    }

    public TVGrabberUser(int id, String email, boolean subscribed, String searchTerm) {
        this.id = id;
        this.email = email;
        this.subscribed = subscribed;
        this.searchTerm = searchTerm;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name="email", length = 200, nullable = false)
    private String email;

    @Column(name="subscribed", nullable = false)
    private Boolean subscribed;

    @Column(name="searchTerm", length = 200, nullable = false)
    private String searchTerm;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
