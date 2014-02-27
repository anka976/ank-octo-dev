package de.wps.usermanagement.persistence.dao;

import java.util.List;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;

import de.wps.usermanagement.persistence.model.LdapNodeEntityContextMapper;
import de.wps.usermanagement.persistence.model.impl.LdapNodeEntity;

/**
 * Basic LDAP entity DAO
 * @author anna
 *
 */
public abstract class LdapEntityDao {

    @Autowired
    private LdapTemplate ldapTemplate;
    @Value("${de.wps.usermanagement.ldap.userSearchBase}") 
    private String userSearchBase;
    @Value("${de.wps.usermanagement.ldap.boardSearchBase}")
    private String boardSearchBase;
    @Value("${de.wps.usermanagement.ldap.technicalUserSearchBase}")
    private String technicalUserSearchBase;

    /**
     * gets entity by DN Name
     * @param dn Name
     * @return entity
     */
    protected LdapNodeEntity getEntity(Name dn) {
        return (LdapNodeEntity) ldapTemplate.lookup(dn, UserManagementConstants.USER_ATTRS, new LdapNodeEntityContextMapper());
        
    }

    /**
     * Gets any LdapNodeEntity by filter
     * @param base ldap search base
     * @param filter filter to search LDAP
     * @return LdapNodeEntity
     * @throws NamingException in case of invalid user
     */
    protected LdapNodeEntity getEntity(String base, String filter) throws NamingException {
        @SuppressWarnings("unchecked")
        List<LdapNodeEntity> uniqueMembers = ldapTemplate.search(base, filter, SearchControls.SUBTREE_SCOPE,
            UserManagementConstants.USER_ATTRS, new LdapNodeEntityContextMapper());
        if (uniqueMembers.size() != 1) {
            throw new NamingException("Duplicate or nonexistent entity. " + "Found number of entities: "
                    + uniqueMembers.size());
        }
        return uniqueMembers.get(0);
    }
    
    /**
     * Spring autowired LDAP template
     * @return the ldapTemplate
     */
    protected LdapTemplate getLdapTemplate() {
        return ldapTemplate;
    }

    /**
     * @return the userSearchBase
     */
    public String getUserSearchBase() {
        return userSearchBase;
    }

    /**
     * @return the boardSearchBase
     */
    public String getBoardSearchBase() {
        return boardSearchBase;
    }

    /**
     * @return the technicalUserSearchBase
     */
    public String getTechnicalUserSearchBase() {
        return technicalUserSearchBase;
    }

}