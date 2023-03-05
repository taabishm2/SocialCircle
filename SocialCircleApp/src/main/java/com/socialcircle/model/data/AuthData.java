package com.socialcircle.model.data;

import com.socialcircle.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthData {

    private Boolean isAuthenticated = false;

    private User user;

}
