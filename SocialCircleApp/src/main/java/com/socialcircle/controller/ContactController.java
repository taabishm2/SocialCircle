package com.socialcircle.controller;

import com.socialcircle.api.ApiException;
import com.socialcircle.api.ContactAPI;
import com.socialcircle.api.RecommendationAPI;
import com.socialcircle.entity.User;
import com.socialcircle.model.data.AuthData;
import com.socialcircle.model.data.UserData;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    @Autowired
    private ContactAPI contactAPI;

    @Autowired
    private RecommendationAPI recommendationAPI;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<User> getContact() throws ApiException {
        return contactAPI.getContacts();
    }

    @RequestMapping(value = "/suggested", method = RequestMethod.GET)
    public List<User> getSuggested() throws ApiException {
        return contactAPI.getSuggested();
    }

    @RequestMapping(value = "/add-contact/{userId}", method = RequestMethod.POST)
    public void addContact(@PathVariable Long userId, @RequestParam(required = false) Integer initialRate, @RequestParam(required = false) Integer targetRate, @RequestParam(required = false) Integer timeframe) throws ApiException {
        contactAPI.addContact(userId, initialRate, targetRate, timeframe);
    }

    @RequestMapping(value = "/recommendations", method = RequestMethod.GET)
    public List<UserData> getLoggedInUser() throws ApiException {
        return recommendationAPI.getRecommendations();
    }

}