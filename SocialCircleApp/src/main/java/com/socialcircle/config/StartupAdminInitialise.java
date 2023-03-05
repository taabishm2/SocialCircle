package com.socialcircle.config;

import com.socialcircle.api.ApiException;
import com.socialcircle.api.ContactAPI;
import com.socialcircle.api.UserAPI;
import com.socialcircle.config.security.UserRole;
import com.socialcircle.dao.ContactDao;
import com.socialcircle.dao.RecommendationDao;
import com.socialcircle.dao.UserDao;
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

    @Autowired
    private UserDao userDao;
    @Autowired
    private ContactDao contactDao;
    @Autowired
    private RecommendationDao recommendationDao;

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

        if (userApi.getByRole(UserRole.USER).isEmpty()) {

            User user1 = new User();
            user1.setName("User1");
            user1.setEmail("1@e");
            user1.setCity("City1");
            user1.setPhone(10001L);
            user1.setPasswordDigest(passwordEncoder.encode("123"));
            user1.setAttribute1("U1A1");
            user1.setAttribute2("U1A2");
            user1.setAttribute3("U1A3");
            user1.setAttribute4(null);
            user1.setAttribute5(null);
            userDao.persist(user1);

            User user2 = new User();
            user2.setName("User2");
            user2.setEmail("2@e");
            user2.setCity("City2");
            user2.setPhone(20001L);
            user2.setPasswordDigest(passwordEncoder.encode("123"));
            user2.setAttribute1(null);
            user2.setAttribute2(null);
            user2.setAttribute3("U2A3");
            user2.setAttribute4("U2A4");
            user2.setAttribute5("U2A5");
            userDao.persist(user2);
;
            Contact contactU1U2 = new Contact();
            contactU1U2.setUserAId(user1.getUserId());
            contactU1U2.setUserBId(user2.getUserId());
            contactU1U2.setInitialFrequency(50);
            contactU1U2.setTargetFrequency(5);
            contactU1U2.setTimeframe(100);
            contactU1U2.setProgress(0L);
            contactDao.persist(contactU1U2);

            User user3 = new User();
            user3.setName("User3");
            user3.setEmail("3@e");
            user3.setCity("City3");
            user3.setPhone(20001L);
            user3.setPasswordDigest(passwordEncoder.encode("123"));
            user3.setAttribute1(null);
            user3.setAttribute2(null);
            user3.setAttribute3("U3A3");
            user3.setAttribute4("U3A4");
            user3.setAttribute5("U3A5");
            userDao.persist(user3);

            User user4 = new User();
            user4.setName("User4");
            user4.setEmail("4@e");
            user4.setCity("City4");
            user4.setPhone(20001L);
            user4.setPasswordDigest(passwordEncoder.encode("124"));
            user4.setAttribute1(null);
            user4.setAttribute2(null);
            user4.setAttribute4("U4A4");
            user4.setAttribute4("U4A4");
            user4.setAttribute5("U4A5");
            userDao.persist(user4);
            
            Recommendation recommendation12 = new Recommendation();
            recommendation12.setUserA(user1.getUserId());
            recommendation12.setUserB(user2.getUserId());
            recommendation12.setRanking(1);
            recommendationDao.persist(recommendation12);

            Recommendation recommendation13 = new Recommendation();
            recommendation13.setUserA(user1.getUserId());
            recommendation13.setUserB(user3.getUserId());
            recommendation13.setRanking(2);
            recommendationDao.persist(recommendation13);

            Recommendation recommendation14 = new Recommendation();
            recommendation14.setUserA(user1.getUserId());
            recommendation14.setUserB(user4.getUserId());
            recommendation14.setRanking(3);
            recommendationDao.persist(recommendation14);
            
        }

    }
}
