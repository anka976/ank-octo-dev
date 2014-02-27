package de.wps.usermanagement.service;

import java.util.List;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wps.usermanagement.persistence.model.web.User;

/**
 * General User Services used for extern user business logic
 * @author anna
 *
 */
@Service
public class UserExtService {
    /**
     * Internal BoardService
     */
    @Autowired
    private BoardService boardService;
    /**
     * Internal UserService
     */
    @Autowired
    private UserService userService;
    
    
    /**
     * Retrieves all users of the requested board
     * @param boardName name of the board
     * @return list of end users
     * @throws NamingException 
     */
    public List<User> getUsersByBoardName(String boardName) throws NamingException {
        return boardService.getMembersOfBoard(boardName);
    }
    
    /**
     * gets user by id
     * @param userId iniqueIdentifier of user
     * @return User
     * @throws NamingException in case of invalid user
     */
    public User getUser(String userId) throws NamingException {
        return userService.getUser(userId);
    }
    
    /**
     * Modifies cryptoContainer for user
     * @param user to modify
     * @return true if success
     * @throws NamingException in case of invalid user
     */
    public boolean updateCryptocontainer(User user) throws NamingException {
        return userService.updateCryptocontainer(user);
    }
    
    /**
     * Modifies userPKCS12 for user
     * @param user to modify
     * @return true if success
     * @throws NamingException in case of invalid user
     */
    public boolean updateUserPKCS12(User user) throws NamingException {
        return userService.updateUserPKCS12(user);
    }
    
    /**
     * Modifies userCertificate for user
     * @param user to modify
     * @return true if success
     * @throws NamingException in case of invalid user
     */
    public boolean updateUserCertificate(User user) throws NamingException {
        return userService.updateUserCertificate(user);
    }
}
