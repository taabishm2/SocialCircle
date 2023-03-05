package com.socialcircle.model.data;

import com.socialcircle.api.ApiException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class ErrorData {

    private ApiException.Type code;
    private String message;
    private String description;
    private Collection<FieldErrorData> errors;

    public ErrorData() {
        setErrors(new ArrayList<FieldErrorData>(0));
    }

}
