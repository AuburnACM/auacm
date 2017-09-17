package io.github.auburnacm.auacm.database.service;

import io.github.auburnacm.auacm.api.model.RankedUser;
import io.github.auburnacm.auacm.database.dao.ProblemDao;
import io.github.auburnacm.auacm.database.dao.UserDao;
import io.github.auburnacm.auacm.database.model.SolvedProblem;
import io.github.auburnacm.auacm.database.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SolvedProblemService solvedProblemService;



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
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
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
    @Transactional
    public void updatePassword(User user, String newPassword) {
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userDao.updateUser(user);
    }

    @Override
    @Transactional
    public void updateDisplayName(User user, String newDisplayName) {
        user.setDisplay(newDisplayName);
        userDao.updateUser(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        userDao.updateUser(user);
    }

    @Override
    @Transactional
    public boolean userExists(String username) {
        User user = userDao.getUser("username", username);
        return user != null;
    }

    @Override
    @Transactional
    public List<RankedUser> getRanks(String timeFrame) {
        long now = System.currentTimeMillis() / 1000;
        TreeSet<RankedUser> rankedUsers = new TreeSet<>();
        long time = 0;
        if (timeFrame.equals("day")) {
            time = now - (60 * 60 * 24);
        } else if (timeFrame.equals("week")) {
            time = now - (60 * 60 * 24 * 7);
        } else if (timeFrame.equals("month")) {
            time = now - (60 * 60 * 24 * 7 * 4);
        } else if (timeFrame.equals("year")) {
            time = now - (60 * 60 * 24 * 365);
        }
        List<User> users = userDao.getUsers();
        for (User user : users) {
            List<SolvedProblem> submissions = solvedProblemService.getProblemsForUser(user.getUsername());
            int solvedSize = 0;
            for (SolvedProblem problem : submissions) {
                if (problem.getSubmitTime() >= time) {
                    solvedSize++;
                }
            }
            if (solvedSize > 0) {
                rankedUsers.add(new RankedUser(user.getDisplay(), user.getUsername(), 0, solvedSize));
            }
        }
        List<RankedUser> finalList = new ArrayList<>();
        int rank = 1;
        for (RankedUser user : rankedUsers) {
            user.setRank(rank);
            finalList.add(user);
            rank++;
        }
        return finalList;
    }
}
