package cz.cvut.felk.kbss.freeplane.server.model;

import javax.persistence.*;
import java.util.List;

/**
 * User entity
 */
@Entity
@Table(name = "user")
@PrimaryKeyJoinColumn(name = "collaborator_id")
public class User extends Collaborator {

    private String firstname;
    private String lastname;
    private String password;
    private String authenticationType;
    private String authenticatorUri;
    private boolean isAdmin;

    private List<Mindmap> mindmaps;

    @Basic
    @Column(name = "firstname")
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Basic
    @Column(name = "lastname")
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "authentication_type")
    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    @Basic
    @Column(name = "authenticator_uri")
    public String getAuthenticatorUri() {
        return authenticatorUri;
    }

    public void setAuthenticatorUri(String authenticatorUri) {
        this.authenticatorUri = authenticatorUri;
    }

    @Basic
    @Column(name = "is_admin")
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @OneToMany(mappedBy = "creator", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public List<Mindmap> getMindmaps() {
        return mindmaps;
    }

    public void setMindmaps(List<Mindmap> mindmapsByCollaboratorId) {
        this.mindmaps = mindmapsByCollaboratorId;
    }
}
