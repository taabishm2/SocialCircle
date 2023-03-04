package com.socialcircle.config;

import com.socialcircle.api.ApiException;
import com.socialcircle.api.ContactAPI;
import com.socialcircle.api.UserAPI;
import com.socialcircle.config.security.UserRole;
import com.socialcircle.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class StartupAdminInitialise implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserAPI userApi;
    @Autowired
    private ContactAPI contactApi;

    @Autowired
    private ApplicationProperties properties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (userApi.getByRole(UserRole.ADMIN).isEmpty()) {
            User adminUser = new User();
            adminUser.setUserRole(UserRole.ADMIN);
            adminUser.setEmail(properties.getAdminEmail());
            adminUser.setPasswordDigest(passwordEncoder.encode(properties.getAdminPassword()));
            adminUser.setName("ADMIN");

            try {
                userApi.add(adminUser);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }

    }
}
