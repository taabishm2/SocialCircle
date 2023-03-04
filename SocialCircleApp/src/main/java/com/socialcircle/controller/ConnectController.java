package com.socialcircle.controller;

import com.socialcircle.api.ApiException;
import com.socialcircle.api.ConnectAPI;
import com.socialcircle.entity.Connect;
import com.socialcircle.entity.User;
import com.socialcircle.model.form.ConnectForm;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/connects")
public class ConnectController {
    @Autowired
    private ConnectAPI connectAPI;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Connect addConnect(@RequestBody ConnectForm form) throws ApiException {
        return connectAPI.add(form);
    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public List<Connect> getConnectsToUser(@PathVariable Long userId) throws ApiException {
        return connectAPI.getConnectsToUser(userId);
    }

}