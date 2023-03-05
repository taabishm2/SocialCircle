package com.socialcircle.config.security;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class UserPrincipal {

    @NotNull
    private Long userId;

    @NotNull
    private String email;

    @NotNull
    private UserRole userRole;

}
