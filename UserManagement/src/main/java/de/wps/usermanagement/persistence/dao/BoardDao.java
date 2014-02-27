/**
 * 
 */
package de.wps.usermanagement.persistence.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.naming.directory.SearchControls;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.util.StringUtils;

import com.googlecode.ehcache.annotations.Cacheable;

import de.wps.usermanagement.persistence.model.LdapNodeEntityContextMapper;
import de.wps.usermanagement.persistence.model.impl.LdapNodeEntity;
import de.wps.usermanagement.persistence.model.impl.LdapNodeEntity.SUBCLASS_TYPE;
import de.wps.usermanagement.persistence.model.web.Group;

/**
 * Data access interface to ldap for Board
 * @author anna
 */
public class BoardDao extends LdapEntityDao {

    @Value("${de.wps.usermanagement.ldap.boardsOfUserRegexFilter}")
    private String boardsOfUserRegexFilter;
    @Value("${de.wps.usermanagement.ldap.membersOfTechnicalUserRegexFilter}")
    private String membersOfTechnicalUserRegexFilter;

    /**
     * loads boards by uniqueIdentifier of user
     * @param uid uid of user
     * @return boards
     * @throws NamingException in case of invalid user
     */
    @Cacheable(cacheName = "ldapServiceCache")
    public List<Group> loadBoardsByUser(String uid) throws NamingException {
        return loadGroupsByUser(uid, boardsOfUserRegexFilter);
    }

    /**
     * Loads groups of technical user
     * @param uid uid of user
     * @param groupFilterRegex group filter
     * @return groups
     * @throws NamingException in case of invalid user
     */
    @Cacheable(cacheName = "ldapServiceCache")
    public List<Group> loadGroupsByUser(String uid, String groupFilterRegex) throws NamingException {
        EqualsFilter filter = new EqualsFilter("uid", uid);
        LdapNodeEntity user = getEntity(getTechnicalUserSearchBase(), filter.encode());

        if (user == null || user.getSubclassType() != SUBCLASS_TYPE.user) {
            throw new NamingException("Non-existent user: " + uid);
        }
        
        SortedSet<String> groups = user.getMemberOf();
        List<LdapNodeEntity> members = getNotaryMembersForTechnicalUser(user);
        
        for (LdapNodeEntity member : members) {
            if (member.getMemberOf() != null && !member.getMemberOf().isEmpty()) {
                groups.addAll(member.getMemberOf());
            }
        }
        
        List<LdapNodeEntity> parents = findUsersParents(members);

        for (LdapNodeEntity parent : parents) {
            if (parent.getMemberOf() != null && !parent.getMemberOf().isEmpty()) {
                groups.addAll(parent.getMemberOf());
            }
        }
        
        OrFilter boardsFilter = new OrFilter();
        Pattern p = Pattern.compile(groupFilterRegex);
        Matcher m;

        if (groups != null) {
            for (String group : groups) {
                group = group.trim();
                m = p.matcher(group);
                if (m.matches()) {
                    boardsFilter.append(new EqualsFilter("entryDN", group));
                }
            }
        }
        List<Group> groupList = new ArrayList<Group>();

        if (StringUtils.hasText(boardsFilter.encode())) {
            @SuppressWarnings("unchecked")
            List<LdapNodeEntity> uniqueMembers = getLdapTemplate().search(getBoardSearchBase(), boardsFilter.encode(),
                SearchControls.SUBTREE_SCOPE, UserManagementConstants.USER_ATTRS, new LdapNodeEntityContextMapper());

            for (LdapNodeEntity entity : uniqueMembers) {
                groupList.add(createEndGroup(entity));
            }
        }
        return groupList;
    }

    /**
     * Returns notary members for technical user
     * @param technicalUser LdapNodeEntity end user
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<LdapNodeEntity> getNotaryMembersForTechnicalUser(LdapNodeEntity technicalUser) {

        SortedSet<String> groups = technicalUser.getMemberOf();

        OrFilter usersFilter = new OrFilter();
        Pattern p = Pattern.compile(membersOfTechnicalUserRegexFilter);
        Matcher m;

        if (groups != null) {
            for (String group : groups) {
                group = group.trim();
                m = p.matcher(group);
                if (m.matches()) {
                    usersFilter.append(new EqualsFilter("entryDN", findParentDn(group)));
                }
            }
        }
        List<LdapNodeEntity> uniqueMembers = Collections.emptyList();

        if (StringUtils.hasText(usersFilter.encode())) {
            uniqueMembers = getLdapTemplate().search(getUserSearchBase(), usersFilter.encode(), SearchControls.SUBTREE_SCOPE,
                UserManagementConstants.USER_ATTRS, new LdapNodeEntityContextMapper());
        }
        return uniqueMembers;
    }

    /**
     * Fills end Group object
     * @param entity LdapNodeEntity
     */
    private Group createEndGroup(LdapNodeEntity entity) {
        Group group = new Group();
        BeanUtils.copyProperties(entity, group);
        return group;
    }

    /**
     * finds users parents
     * @param users child users
     * @return user parents
     */
    private List<LdapNodeEntity> findUsersParents(List<LdapNodeEntity> users) {
        
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        
        String[] parentNamePath;
        String[] userParentFilter;
        EqualsFilter eq;
        OrFilter usersFilter = new OrFilter();
        
        for (LdapNodeEntity user : users) {
            parentNamePath = user.getEntryDN().trim().split(",");
            userParentFilter = parentNamePath[1].trim().split("=", 2);
            eq = new EqualsFilter(userParentFilter[0], userParentFilter[1]);
            usersFilter.append(eq);
        }
        
        @SuppressWarnings("unchecked")
        List<LdapNodeEntity> uniqueMembers = getLdapTemplate().search(getUserSearchBase(), usersFilter.encode(),
            SearchControls.ONELEVEL_SCOPE, UserManagementConstants.USER_ATTRS, new LdapNodeEntityContextMapper());
    
        
        return uniqueMembers;
    }

    /**
     * finds parent dn
     * @param dn child dn
     * @return parent dn
     */
    private String findParentDn(String dn) {
        int index = dn.indexOf(',') + 1;
        return dn.substring(index);
    }

}
