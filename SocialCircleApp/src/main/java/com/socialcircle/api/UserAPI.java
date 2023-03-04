package com.socialcircle.api;

import com.socialcircle.config.security.UserPrincipal;
import com.socialcircle.config.security.UserRole;
import com.socialcircle.dao.UserDao;
import com.socialcircle.entity.User;
import com.socialcircle.model.data.AuthData;
import com.socialcircle.model.form.RegisterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
        checkNotNull(registerForm.getName(), "Name cannot be empty");
        checkNotNull(registerForm.getEmail(), "Email cannot be empty");
        checkNotNull(registerForm.getPassword(), "Password cannot be empty");

        if (Objects.isNull(registerForm.getAttribute1()) &&
                Objects.isNull(registerForm.getAttribute2()) &&
                Objects.isNull(registerForm.getAttribute3()) &&
                Objects.isNull(registerForm.getAttribute4()) &&
                Objects.isNull(registerForm.getAttribute5()) )
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


}


