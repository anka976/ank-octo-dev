package de.wps.usermanagement.service;

import java.util.List;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wps.usermanagement.persistence.dao.BoardDao;
import de.wps.usermanagement.persistence.dao.UserDao;
import de.wps.usermanagement.persistence.model.web.Group;
import de.wps.usermanagement.persistence.model.web.User;

/**
 * Internal Board Service used for internal board business logic
 * @author anna
 *
 */
@Service
public class BoardService {
    /**
     * UserDao
     */
    @Autowired
    private UserDao userDao;
    /**
     * BoardDao
     */
    @Autowired
    private BoardDao boardDao;

    /**
     * Retrieves all users of the requested board
     * @param boardName name of the board
     * @return list of end users
     * @throws NamingException 
     */
    public List<User> getMembersOfBoard(String boardName) throws NamingException {
        return userDao.loadUsersByBoard(boardName);
    }
    /**
     * gets all boards of the provided users
     * @param userId iniqueIdentifier of user
     * @return list of boards
     * @throws NamingException in case of invalid user
     */
    public List<Group> getBoardsByUserId(String userId) throws NamingException {
        return boardDao.loadBoardsByUser(userId);
    }
}
