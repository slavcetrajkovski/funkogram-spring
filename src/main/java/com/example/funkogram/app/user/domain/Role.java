package com.example.funkogram.app.user.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "user_roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column
    private ERole name;

}
