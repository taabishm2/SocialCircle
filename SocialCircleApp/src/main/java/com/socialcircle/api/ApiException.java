package com.socialcircle.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiException extends Exception {

    private Type type;
    private static final long serialVersionUID = 1L;

    public ApiException(Type type, String string) {
        super(string);
        this.type = type;
    }

    public enum Type {
        SERVER_ERROR,
        USER_ERROR
    }

}
