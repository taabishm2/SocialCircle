package com.socialcircle.entity;

import com.socialcircle.config.security.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.dom4j.tree.AbstractEntity;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(
        indexes = {@Index(name = "user_username_index", columnList = "email")},
        uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class User extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordDigest;

    @Column(nullable = false)
    private String name;

    private Long phone;

    private String city;

    @Column
    private String attribute1;

    @Column
    private String attribute2;

    @Column
    private String attribute3;

    @Column
    private String attribute4;

    @Column
    private String attribute5;

    @Column
    private Integer personalityType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.ADMIN;

}
