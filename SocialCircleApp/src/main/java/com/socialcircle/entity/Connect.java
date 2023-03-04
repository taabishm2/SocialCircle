package com.socialcircle.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table
public class Connect extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long connectId;

    /* Assume only one contact will add connect */
    /* TODO: Order this while creation source < destination ID */
    @Column(nullable = false)
    private Long sourceUserId;

    @Column(nullable = false)
    private Long destinationUserId;

    private ZonedDateTime connectTime;

    /* Connect score 0-10 */
    @Column(nullable = false)
    private Integer score = 0;

    @Column
    private String notes;

    @Column(nullable = false)
    private Boolean isSuggestion;

}
