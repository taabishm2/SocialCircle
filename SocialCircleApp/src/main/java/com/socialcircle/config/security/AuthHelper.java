package com.socialcircle.config.security;

import com.socialcircle.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class AuthHelper {

    public static void createContext(HttpSession session) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
    }

    public static UsernamePasswordAuthenticationToken getAuthenticationToken(User user, UserRole role) {
        UserPrincipal principal = getPrincipal(user, role);
        List<GrantedAuthority> authorityList = getAuthorities(principal.getUserRole().name());
        return new UsernamePasswordAuthenticationToken(principal, null, authorityList);
    }

    public static UserPrincipal getPrincipal(User userEntity, UserRole role) {
        UserPrincipal principal = new UserPrincipal();
        principal.setUserId(userEntity.getUserId());
        principal.setEmail(userEntity.getEmail());
        principal.setUserRole(role);
        return principal;
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static void setAuthentication(Authentication token) {
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public static UserPrincipal getPrincipal() {
        Authentication token = getAuthentication();
        return token == null ? null : (UserPrincipal) getAuthentication().getPrincipal();
    }

    public static List<GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority(role));
        return authorityList;
    }

}

