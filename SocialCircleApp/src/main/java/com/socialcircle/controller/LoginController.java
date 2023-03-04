package com.socialcircle.controller;

import com.socialcircle.api.ApiException;
import com.socialcircle.api.UserAPI;
import com.socialcircle.config.security.AuthHelper;
import com.socialcircle.entity.User;
import com.socialcircle.model.form.LoginForm;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserAPI userApi;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final int SESSION_MAX_ACTIVE_SECONDS = 60 * 60;

    @ApiOperation(value = "Log in a user")
    @RequestMapping(path = "/api/login", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView login(HttpServletRequest httpRequest, LoginForm loginForm) throws ApiException {
        User user = userApi.attemptUserLogin(loginForm.getEmail(), loginForm.getPassword());
        Authentication authentication = AuthHelper.getAuthenticationToken(user, user.getUserRole());

        HttpSession session = httpRequest.getSession(true);
        session.setMaxInactiveInterval(SESSION_MAX_ACTIVE_SECONDS);

        AuthHelper.createContext(session);
        AuthHelper.setAuthentication(authentication);

        /* Redirect user to homepage */
        return new ModelAndView("redirect:/");
    }

    @ApiOperation(value = "Log out a user")
    @RequestMapping(path = "/api/logout", method = RequestMethod.POST)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
    }

}

