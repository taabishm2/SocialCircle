package com.socialcircle.controller;

import com.socialcircle.config.ApplicationProperties;
import com.socialcircle.config.security.AuthHelper;
import com.socialcircle.config.security.SecurityUtil;
import com.socialcircle.config.security.UserPrincipal;
import com.socialcircle.model.data.LoginMessageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public abstract class AbstractUiController {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private LoginMessageData data;

    protected ModelAndView getModelAndView(String page) {
        UserPrincipal principal = AuthHelper.getPrincipal();
        data.setEmail(principal == null ? "" : principal.getEmail());
        ModelAndView mav = new ModelAndView(page);
        mav.addObject("baseUrl", applicationProperties.getBaseUrl());
        mav.addObject("data", data);
        return mav;
    }

}
