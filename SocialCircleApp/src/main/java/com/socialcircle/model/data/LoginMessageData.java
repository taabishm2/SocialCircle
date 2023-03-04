package com.socialcircle.model.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Getter
@Setter
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LoginMessageData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String message;
    private String email;

    public LoginMessageData() {
        message = "";
        email = "No email present";
    }

}

