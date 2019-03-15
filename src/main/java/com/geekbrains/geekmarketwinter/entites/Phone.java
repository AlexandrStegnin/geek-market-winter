package com.geekbrains.geekmarketwinter.entites;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@NoArgsConstructor
@Table(name = "phones")
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 6, max = 20, message = "Phone number must be greater than 6 and less than 20 characters")
    @Column(name = "phone_number")
    private String phoneNumber;

    @ManyToOne
    private User user;

    public Phone(String phoneNumber, User user) {
        this.phoneNumber = phoneNumber;
        this.user = user;
    }

}
