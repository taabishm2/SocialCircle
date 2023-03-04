package com.socialcircle.controller;

import com.socialcircle.api.ApiException;
import com.socialcircle.api.ContactAPI;
import com.socialcircle.api.UserAPI;
import com.socialcircle.entity.User;
import com.socialcircle.model.data.AuthData;
import com.socialcircle.model.form.RegisterForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserAPI userApi;

    @ApiOperation(value = "Register a user")
    @RequestMapping(path = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public User register(RegisterForm registerForm) throws ApiException {
        return userApi.register(registerForm);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public User getUser(@PathVariable Long userId) throws ApiException {
        return userApi.getCheck(userId);
    }

    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public AuthData getLoggedInUser() throws ApiException {
        return userApi.getLoggedInUser();
    }

}