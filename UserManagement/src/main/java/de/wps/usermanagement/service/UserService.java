package de.wps.usermanagement.service;


import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wps.usermanagement.persistence.dao.UserDao;
import de.wps.usermanagement.persistence.model.web.User;
/**
 * General User Services used for intern user business logic
 * @author anna
 */
@Service
public class UserService {
    /**
     * UserDao
     */
    @Autowired
    private UserDao userDao;
    
    /**
     * gets user by id
     * @param userId iniqueIdentifier of user
     * @return User
     * @throws NamingException in case of invalid user
     */
    public User getUser(String userId) throws NamingException {
        return userDao.getTechnicalUser(userId);
    }
    
    /**
     * Modifies cryptoContainer for user
     * @param user to modify
     * @return true if success
     * @throws NamingException in case of invalid user
     */
    public boolean updateCryptocontainer(User user) throws NamingException {
        return userDao.updateCryptocontainer(user);
    }
    
    /**
     * Modifies userPKCS12 for user
     * @param user to modify
     * @return true if success
     * @throws NamingException in case of invalid user
     */
    public boolean updateUserPKCS12(User user) throws NamingException {
        return userDao.updateUserPKCS12(user);
    }
    
    /**
     * Modifies userCertificate for user
     * @param user to modify
     * @return true if success
     * @throws NamingException in case of invalid user
     */
    public boolean updateUserCertificate(User user) throws NamingException {
        return userDao.updateUserCertificate(user);
    }
}
