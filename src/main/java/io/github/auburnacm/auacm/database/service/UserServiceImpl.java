package io.github.auburnacm.auacm.database.service;

import io.github.auburnacm.auacm.database.dao.UserDao;
import io.github.auburnacm.auacm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public boolean validatePassword(String username, String password) {
        User user = userDao.getUser("username", username);
        return BCrypt.checkpw(password, user.getPassword());
    }

    @Override
    @Transactional
    public void createUser(User user) {
        userDao.addUser(user);
    }

    @Override
    @Transactional
    public void createUser(String displayName, String username, String password, boolean isAdmin) {
        User user = new User();
        user.setDisplay(displayName);
        user.setPassword(BCrypt.hashpw(password, "2a"));
        user.setUsername(username);
        user.setAdmin(isAdmin);
        userDao.addUser(user);
    }

    @Override
    @Transactional
    public User getUser(String username) {
        return userDao.getUser("username", username);
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        deleteUser(user);
    }

    @Override
    @Transactional
    public void updateUsername(User user, String newUsername) {
        user.setUsername(newUsername);
        userDao.updateUser(user);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        String hashedPassword = BCrypt.hashpw(newPassword, "");
        user.setPassword(hashedPassword);
        userDao.updateUser(user);
    }

    @Override
    public void updateDisplayName(User user, String newDisplayName) {
        user.setDisplay(newDisplayName);
        userDao.updateUser(user);
    }

    @Override
    public boolean userExists(String username) {
        User user = userDao.getUser("username", username);
        return user != null;
    }
}
