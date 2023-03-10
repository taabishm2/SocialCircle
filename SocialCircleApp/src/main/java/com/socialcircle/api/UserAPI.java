package com.socialcircle.api;

import com.socialcircle.config.security.SecurityUtil;
import com.socialcircle.config.security.UserPrincipal;
import com.socialcircle.config.security.UserRole;
import com.socialcircle.dao.UserDao;
import com.socialcircle.entity.Contact;
import com.socialcircle.entity.User;
import com.socialcircle.model.data.AuthData;
import com.socialcircle.model.data.UserData;
import com.socialcircle.model.form.RegisterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAPI extends AbstractAPI {

    @Autowired
    private UserDao dao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ContactAPI contactApi;

    public User getByEmail(String email) throws ApiException {
        return dao.select("email", email);
    }

    public User attemptUserLogin(String email, String password) throws ApiException {
        User user = getByEmail(email);

        boolean isAuthenticated = (Objects.nonNull(user) &&
                passwordEncoder.matches(password, user.getPasswordDigest()));
        if (!isAuthenticated)
            throw new ApiException(ApiException.Type.USER_ERROR, "User not authenticated");

        return user;
    }

    public User add(User user) throws ApiException {
        checkEmailNotExists(user.getEmail());
        dao.persist(user);
        return user;
    }

    public User getCheck(Long id) throws ApiException {
        User user = dao.select(id);
        checkNotNull(user, "User does not exist with user ID: " + id);
        return user;
    }

    public User getCheckByEmail(String email) throws ApiException {
        User user = dao.select("email", email);
        checkNotNull(user, "User does not exist with user name: " + email);
        return user;
    }

    private void checkEmailNotExists(String email) throws ApiException {
        checkNull(dao.select("email", email), "User already exists with email: " + email);
    }

    public List<User> getByRole(UserRole role) {
        return dao.selectMultiple("userRole", role);
    }

    @Transactional(rollbackFor = ApiException.class)
    public User register(RegisterForm registerForm) throws ApiException {
        checkNotEmpty(registerForm.getName(), "Name cannot be empty");
        checkNotEmpty(registerForm.getEmail(), "Email cannot be empty");
        checkNotEmpty(registerForm.getPassword(), "Password cannot be empty");

        if (registerForm.getAttribute1().isEmpty() &&
                registerForm.getAttribute2().isEmpty() &&
                registerForm.getAttribute3().isEmpty() &&
                registerForm.getAttribute4().isEmpty() &&
                registerForm.getAttribute5().isEmpty())
            throw new ApiException(ApiException.Type.USER_ERROR, "All attributes cannot be empty");

        checkEmailNotExists(registerForm.getEmail().toLowerCase());

        User user = getUser(registerForm);
        add(user);

        return user;
    }

    private User getUser(RegisterForm registerForm) {
        User user = new User();
        user.setUserRole(UserRole.USER);
        user.setName(registerForm.getName());
        user.setEmail(registerForm.getEmail());
        user.setPhone(registerForm.getPhone());
        user.setCity(registerForm.getCity());
        user.setPersonalityType(registerForm.getPersonalityType());
        user.setAttribute1(registerForm.getAttribute1());
        user.setAttribute2(registerForm.getAttribute2());
        user.setAttribute3(registerForm.getAttribute3());
        user.setAttribute4(registerForm.getAttribute4());
        user.setAttribute5(registerForm.getAttribute5());
        user.setPasswordDigest(passwordEncoder.encode(registerForm.getPassword()));
        return user;
    }

    public AuthData getLoggedInUser() throws ApiException {
        UserPrincipal principal = getLoggedInUserPrincipal();

        AuthData authData = new AuthData();
        if (Objects.nonNull(principal) && Objects.nonNull(principal.getUserId())) {
            authData.setIsAuthenticated(true);
            authData.setUser(getCheck(principal.getUserId()));
        }

        return authData;
    }

    public List<UserData> searchByName(String query) throws ApiException {
        if (query.trim().length() == 0)
            throw new ApiException(ApiException.Type.USER_ERROR, "Query cannot be empty");

        List<User> users = dao.selectAll();
        Long thisUserId = SecurityUtil.getPrincipal().getUserId();
        List<Contact> userContacts = contactApi.getContactsForUser(thisUserId);
        Set<Long> contactUserIds = userContacts.stream().map(Contact::getUserAId).collect(Collectors.toSet());
        contactUserIds.addAll(userContacts.stream().map(Contact::getUserBId).collect(Collectors.toSet()));

        users = users.stream().filter(course -> course.getName().toLowerCase()
                .contains(query.toLowerCase().trim())).collect(Collectors.toList());

        List<UserData> userDataList = new ArrayList<>();
        for (User user: users) {
            if (user.getUserId().equals(thisUserId)) continue;
            UserData userData = new UserData();
            userData.setUser(user);
            userData.setIsContact(contactUserIds.contains(user.getUserId()));
            userDataList.add(userData);
        }

        return userDataList;
    }

}


