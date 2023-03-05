package com.socialcircle.dao;

import com.socialcircle.entity.Contact;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ContactDao extends AbstractDao<Contact> {

    public List<Contact> selectForUser(Long thisUserId) {
        List<Contact> contacts = selectMultiple("userAId", thisUserId);
        contacts.addAll(selectMultiple("userBId", thisUserId));
        return contacts;
    }

}
