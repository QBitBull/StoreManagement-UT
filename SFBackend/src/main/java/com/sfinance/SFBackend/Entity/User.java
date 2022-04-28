package com.sfinance.SFBackend.Entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String userID;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String profileImageURL;

    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;

    private String role;
    @Column(length = 100000)
    private String[] authorities;

    private boolean isActive;
    private boolean isNotLocked;
}
