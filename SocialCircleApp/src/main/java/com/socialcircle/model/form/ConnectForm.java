package com.socialcircle.model.form;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConnectForm {
    @NotNull
    private Long connectedWithUserId;

    @NotNull
    private Integer score;

    private String notes;

}
