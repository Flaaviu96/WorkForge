package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "UserAcc")
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_id_seq", allocationSize = 50)
    private long id;

    public AppUser() {

    }

    private String username;
    private String password;
    private String emailAddress;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @OneToOne
    private AppUserDetails userDetails;
}
