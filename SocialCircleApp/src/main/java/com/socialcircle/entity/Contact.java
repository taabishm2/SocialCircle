package com.socialcircle.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table
/* If A->B exists, it implies B->A, so the latter won't exist in DB */
public class Contact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(nullable = false)
    private Long userAId;

    @Column(nullable = false)
    private Long userBId;

    @Column(nullable = false)
    private Integer initialFrequency;
    @Column(nullable = false)
    private Integer targetFrequency;
    @Column(nullable = false)
    private Integer timeframe;

    /* Progress scale is 0-100 */
    private Long progress;
    /* Daily job will increment progress by this rate (not if connects pending) */
    //private Long dailyProgressRate;

}
