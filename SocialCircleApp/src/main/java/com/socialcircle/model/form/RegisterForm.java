package com.socialcircle.model.form;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterForm {

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;

    private String city;
    private Long phone;

    private Integer personalityType;

    private String attribute1;
    private String attribute2;
    private String attribute3;
    private String attribute4;
    private String attribute5;

}
