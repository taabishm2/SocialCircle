package com.socialcircle.api;

import com.socialcircle.config.security.SecurityUtil;
import com.socialcircle.dao.ContactDao;
import com.socialcircle.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ContactAPI {

    @Autowired
    private ContactDao contactDao;
    @Autowired
    private UserAPI userApi;

    public List<User> getContacts() throws ApiException {
        Long thisUserId = SecurityUtil.getPrincipal().getUserId();
        List<Contact> contacts = contactDao.selectMultiple("userAId", thisUserId);
        contacts.addAll(contactDao.selectMultiple("userBId", thisUserId));

        List<User> users = new ArrayList<>();
        for (Contact contact : contacts) {
            Long contactUserId = contact.getUserAId().equals(thisUserId) ? contact.getUserBId() : contact.getUserAId();
            users.add(userApi.getCheck(contactUserId));
        }
        return users;
    }

    public List<User> getSuggested() throws ApiException {
        /* TODO: Read some DB table with user rankings and return one of them which isn't already present */
        return null;
    }


    @Transactional(rollbackFor = ApiException.class)
    public void addContact(Long userId, Integer initialRate, Integer targetRate, Integer timeframe) throws ApiException {
        userApi.getCheck(userId);
        addContact(SecurityUtil.getPrincipal().getUserId(), userId, initialRate, targetRate, timeframe);
    }

    /* Assume that one direction contact is established i.e. both users implicitly agree upon gompertz values */
    @Transactional(rollbackFor = ApiException.class)
    public void addContact(Long userAId, Long userBId, Integer initialRate, Integer targetRate, Integer timeframe) {
        Contact contact = new Contact();
        contact.setUserAId(userAId);
        contact.setUserBId(userBId);
        contact.setProgress(0L);
        contact.setTimeframe(timeframe);
        contact.setInitialFrequency(getInitialRate(initialRate));
        contact.setTargetFrequency(getTargetRate(targetRate));
        contactDao.persist(contact);
    }

    public List<Contact> getContactsForUser(Long userId) {
        return contactDao.selectForUser(userId);
    }

    private Integer getTargetRate(Integer targetRate) {
        return Objects.isNull(targetRate) ? 30 : targetRate;
    }

    private Integer getInitialRate(Integer initialRate) {
        return Objects.isNull(initialRate) ? 1000 : initialRate;
    }

}
