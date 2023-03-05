package com.socialcircle.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table
public class Recommendation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private Long userA;

    private Long userB;

    private Integer ranking;

}
