package de.wps.usermanagement.service;

import java.util.List;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wps.usermanagement.persistence.model.web.Group;

/**
 * External Board Service used for external request of Boards
 * @author anna
 *
 */
@Service
public class BoardExtService {
    /**
     * Internal BoardService
     */
    @Autowired
    private BoardService boardService;
    
    /**
     * gets all boards of the provided users
     * @param userId iniqueIdentifier of user
     * @return list of boards
     * @throws NamingException in case of invalid user
     */
    public List<Group> getBoardsByUserId(String userId) throws NamingException {
        return boardService.getBoardsByUserId(userId);
    }
}
