package com.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "contacts")
public class ContactsOfOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    public ContactsOfOrder (ContactsOfOrderDTO contacts) {
        this.email = contacts.getEmail();
        this.phone = contacts.getPhone();
    }
}
