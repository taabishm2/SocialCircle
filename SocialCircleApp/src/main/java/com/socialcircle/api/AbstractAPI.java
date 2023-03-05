package com.socialcircle.api;

import com.socialcircle.config.security.SecurityUtil;
import com.socialcircle.config.security.UserPrincipal;
import com.socialcircle.config.security.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Service
public class AbstractAPI {

    public static <T> void checkValid(T form) throws ApiException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(form);
        for (ConstraintViolation<T> violation : violations)
            throw new ApiException(ApiException.Type.USER_ERROR, "Constraint Violation: " + violation.getPropertyPath() + " " + violation.getMessage());
    }

    public static <T> void checkValid(Collection<T> formList) throws ApiException {
        for (T form : formList)
            checkValid(form);
    }

    public void checkNull(Object object, String message) throws ApiException {
        if (!Objects.isNull(object))
            throw new ApiException(ApiException.Type.USER_ERROR, message);
    }

    public void checkNotNull(Object object, String message) throws ApiException {
        if (Objects.isNull(object))
            throw new ApiException(ApiException.Type.USER_ERROR, message);
    }

    public void checkNotEmpty(String string, String message) throws ApiException {
        if (Objects.isNull(string) || string.length() == 0)
            throw new ApiException(ApiException.Type.USER_ERROR, message);
    }

    public static void checkTrue(Boolean bool, String message) throws ApiException {
        if (!bool)
            throw new ApiException(ApiException.Type.USER_ERROR, message);
    }

    public static void checkFalse(Boolean bool, String message) throws ApiException {
        if (bool)
            throw new ApiException(ApiException.Type.USER_ERROR, message);
    }

    public UserPrincipal getLoggedInUserPrincipal() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (Objects.isNull(context)) return null;

        Authentication authentication = context.getAuthentication();
        if (Objects.isNull(authentication)) return null;

        try {
            return (UserPrincipal) authentication.getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

    public @NotNull Long authorizeUser(UserRole requiredRole) throws ApiException {
        UserPrincipal principal = SecurityUtil.getPrincipal();
        if (!principal.getUserRole().equals(requiredRole))
            throw new ApiException(ApiException.Type.USER_ERROR, "User not logged in");
        return principal.getUserId();
    }

    public void authorizeUser(Long userId, UserRole requiredRole) throws ApiException {
        UserPrincipal principal = SecurityUtil.getPrincipal();
        if ((Objects.nonNull(requiredRole) && !principal.getUserRole().equals(requiredRole))
                || !userId.equals(principal.getUserId()))
            throw new ApiException(ApiException.Type.USER_ERROR, "User Role not present");
    }

}