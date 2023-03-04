package com.socialcircle.config.security;

import com.socialcircle.api.ApiException;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static UserPrincipal getPrincipal() throws ApiException {
        try {
            return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new ApiException(ApiException.Type.USER_ERROR, "You must be logged in for this");
        }
    }

}
