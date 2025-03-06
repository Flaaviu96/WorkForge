package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class AppUserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userDetails_seq")
    @SequenceGenerator(name = "userDetails_seq", sequenceName = "userDetails_id_seq", allocationSize = 50)
    private long id;

    private String firstName;
    private String secondName;
    private String mobilePhone;

}
