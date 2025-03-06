package dev.workforge.app.WorkForge.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
    @SequenceGenerator(name = "address_seq", sequenceName = "address_id_seq", allocationSize = 50)
    private long id;

    private String adress;
    private String city;
    private String postCode;
    private String country;
}
