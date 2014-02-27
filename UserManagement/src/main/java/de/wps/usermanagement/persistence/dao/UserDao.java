/**
 * 
 */
package de.wps.usermanagement.persistence.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.filter.EqualsFilter;

import com.googlecode.ehcache.annotations.Cacheable;

import de.wps.usermanagement.persistence.model.LdapNodeEntityContextMapper;
import de.wps.usermanagement.persistence.model.impl.LdapNodeEntity;
import de.wps.usermanagement.persistence.model.impl.LdapNodeEntity.SUBCLASS_TYPE;
import de.wps.usermanagement.persistence.model.web.User;

/**
 * Data access interface to LDAP for User
 * @author anna
 */
public class UserDao extends LdapEntityDao {
    
    @Value("${de.wps.usermanagement.ldap.usersOfBoardSearchFilter}")
    private String usersOfBoardSearchFilter;
    
    /**
     * DAO implementation for Retrieve Boards of a given User or Sequence
     * @param board Board name
     * @return list of users
     * @throws NamingException 
     */
    @Cacheable(cacheName = "ldapServiceCache")
    public List<User> loadUsersByBoard(String board) throws NamingException {
        if (board == null || board.isEmpty()) {
            return Collections.emptyList();
        }

        String filterString = usersOfBoardSearchFilter.replace("{0}", board);
        String[] parentBoardPath = board.trim().split("_");
        String parent = StringUtils.join(parentBoardPath, "_", 1, parentBoardPath.length - 1);
        filterString = filterString.replace("{1}", parent);

        List<User> members = loadUsersByGroup(filterString);
        
        return members;
    }

    /**
     * loads users by group dn
     * @param groupFilterString group dn
     * @return users
     * @throws NamingException 
     */
    @Cacheable(cacheName = "ldapServiceCache")
    public List<User> loadUsersByGroup(String groupFilterString) throws NamingException {
        
        EqualsFilter filter = new EqualsFilter("entryDN", groupFilterString);
        @SuppressWarnings("unchecked")
        List<LdapNodeEntity> boards = getLdapTemplate().search(getBoardSearchBase(), filter.encode(), SearchControls.SUBTREE_SCOPE,
            UserManagementConstants.USER_ATTRS, new LdapNodeEntityContextMapper());
        if (boards.isEmpty()) {
            throw new NamingException("Group does not exist: "+groupFilterString);
        }
        
        filter = new EqualsFilter("memberOf", groupFilterString);

        LdapNodeEntityContextMapper operationalAttrContextMapper = new LdapNodeEntityContextMapper();

        //search notary members
        @SuppressWarnings("unchecked")
        List<LdapNodeEntity> members = getLdapTemplate().search(getUserSearchBase(), filter.encode(),
            SearchControls.SUBTREE_SCOPE, UserManagementConstants.USER_ATTRS, operationalAttrContextMapper);
        
        
      //search technical users
        @SuppressWarnings("unchecked")
        List<LdapNodeEntity> uniqueUsers = getLdapTemplate().search(getTechnicalUserSearchBase(), filter.encode(),
            SearchControls.SUBTREE_SCOPE, UserManagementConstants.USER_ATTRS, operationalAttrContextMapper);
        
        members.addAll(uniqueUsers);

        filter = new EqualsFilter("objectClass", "kvzBNOTK");
        List<User> users = new ArrayList<User>();
        
        // process groups of users
        for (LdapNodeEntity userEntity : members) {
            
            if (userEntity.getSubclassType() == SUBCLASS_TYPE.groupEntity) {
                @SuppressWarnings("unchecked")
                List<LdapNodeEntity> children = getLdapTemplate().search(userEntity.getDn(), filter.encode(),
                    SearchControls.ONELEVEL_SCOPE, UserManagementConstants.USER_ATTRS, operationalAttrContextMapper);
                for (LdapNodeEntity child : children) {
                    if (child.getSubclassType() == SUBCLASS_TYPE.user) {
                        users.add(createEndUser(child));
                    }
                }
            } else {
                users.add(createEndUser(userEntity));
            }
        }
        
     

        return users;
    }
    
    /**
     * Gets notary (end user) by userId
     * @param userId  userId to search LDAP
     * @return user
     * @throws NamingException in case of invalid user
     */
    public User getNotaryMember(String userId) throws NamingException {
        EqualsFilter filter = new EqualsFilter("userId", StringUtils.trimToEmpty(userId));
        LdapNodeEntity entity =  getEntity(getUserSearchBase(), filter.encode());
        return createEndUser(entity);
    }
    
    /**
     * Gets technical user by uid
     * @param uid  uid to search LDAP
     * @return user
     * @throws NamingException in case of invalid user
     */
    public User getTechnicalUser(String uid) throws NamingException {
        EqualsFilter filter = new EqualsFilter("uid", StringUtils.trimToEmpty(uid));
        LdapNodeEntity entity =  getEntity(getTechnicalUserSearchBase(), filter.encode());
        return createEndUser(entity);
    }
    
    /**
     * Modifies cryptoContainer for user
     * @param user to modify
     * @return true if success
     * @throws NamingException if no user found
     */
    public boolean updateCryptocontainer(User user) throws NamingException {
        return updateUser(user.getUid(), "cryptoContainer", user.getCryptoContainer());
    }
    
    /**
     * Modifies userPKCS12 for user
     * @param user to modify
     * @return true if success
     * @throws NamingException if no user found
     */
    public boolean updateUserPKCS12(User user) throws NamingException {
        return updateUser(user.getUid(), "userPKCS12", user.getUserPKCS12());
    }
    
    /**
     * Modifies userCertificate for user
     * @param user to modify
     * @return true if success
     * @throws NamingException if no user found
     */
    public boolean updateUserCertificate(User user) throws NamingException {
        return updateUser(user.getUid(), "userCertificate;binary", user.getUserCertificate());
    }
    
    /**
     * Modifies an attribute for an user
     * @param uid user id
     * @param attribute ldap attr name
     * @param value new value
     * @return true if success
     * @throws NamingException if no user found
     */
    private boolean updateUser(String uid, String attribute, Object value) throws NamingException {
        EqualsFilter filter = new EqualsFilter("uid", StringUtils.trimToEmpty(uid));
        LdapNodeEntity entity =  getEntity(getTechnicalUserSearchBase(), filter.encode());
        Name dn = entity.getDn();
        Attribute attr = new BasicAttribute(attribute, value);
        ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
        getLdapTemplate().modifyAttributes(dn, 
           new ModificationItem[] {item});
        return true;
    }

    /**
     * Fills end User object
     * @param entity LdapNodeEntity
     */
    private User createEndUser(LdapNodeEntity entity) {
        User user = new User();
        BeanUtils.copyProperties(entity, user);
        return user;
    }

}
