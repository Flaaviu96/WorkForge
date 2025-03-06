package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "UserAcc")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_id_seq", allocationSize = 50)
    private long id;

    private String username;
    private String password;
    private String emailAddress;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @OneToOne
    private AppUserDetails userDetails;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
