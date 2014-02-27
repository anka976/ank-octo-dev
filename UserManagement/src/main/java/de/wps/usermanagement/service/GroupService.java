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
 * Internal Group Service used for internal group business logic
 * @author anna
 *
 */
@Service
public class GroupService {
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
     * retrieves all Members of the given group name
     * @param groupEntryDn full entry DN
     * @return list of members: end users or groups with end user children
     * @throws NamingException 
     */
    public List<User> getMembersOfGroup(String groupEntryDn) throws NamingException  {
        return userDao.loadUsersByGroup(groupEntryDn);
    }
    /**
     * retrieves all groups that the given user is member of the groups must be or be child of given baseDN
     * @param userId uniqueIdentifier of user
     * @param baseDn base DN of the Group
     * @return list of groups with group names
     * @throws NamingException in case of invalid users
     */
    public List<Group> getGroupsByUser(String userId, String baseDn) throws NamingException {
        return boardDao.loadGroupsByUser(userId, baseDn);
    }
}
