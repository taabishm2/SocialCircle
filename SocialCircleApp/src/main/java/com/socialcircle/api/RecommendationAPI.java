package com.socialcircle.api;

import com.socialcircle.config.security.SecurityUtil;
import com.socialcircle.dao.ContactDao;
import com.socialcircle.dao.RecommendationDao;
import com.socialcircle.entity.Contact;
import com.socialcircle.entity.Recommendation;
import com.socialcircle.entity.User;
import com.socialcircle.model.data.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RecommendationAPI {

    @Autowired
    private RecommendationDao recommendationDao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private UserAPI userApi;

    public List<UserData> getRecommendations() throws ApiException {
        Long thisUserId = SecurityUtil.getPrincipal().getUserId();

        List<Contact> existingContacts = contactDao.selectForUser(thisUserId);
        Set<Long> existingContactIds = new HashSet<>();
        for (Contact contact : existingContacts)
            existingContactIds.add(contact.getUserAId().equals(thisUserId) ? contact.getUserBId() : contact.getUserAId());

        List<Recommendation> recommendations = recommendationDao.selectMultiple("userA", thisUserId);
        recommendations.addAll(recommendationDao.selectMultiple("userB", thisUserId));
        recommendations.sort(Comparator.comparing(o -> o.getRanking()));

        List<UserData> users = new ArrayList<>();
        for (Recommendation recommendation : recommendations) {
            Long otherUserId = recommendation.getUserA().equals(thisUserId) ? recommendation.getUserB() : recommendation.getUserA();
            if (existingContactIds.contains(otherUserId)) continue;

            UserData userData = new UserData();
            userData.setUser(userApi.getCheck(otherUserId));
            userData.setIsContact(false);
            users.add(userData);
        }

        return users;
    }

}
