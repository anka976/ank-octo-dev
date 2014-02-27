package de.wps.usermanagement.persistence.model;

import java.util.SortedSet;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.simple.AbstractParameterizedContextMapper;

import de.wps.usermanagement.persistence.model.impl.LdapNodeEntity;
import de.wps.usermanagement.persistence.model.impl.LdapNodeEntity.SUBCLASS_TYPE;

/**
 * ContextMapper for mapping user properties from LDAP to DTO
 */
public class LdapNodeEntityContextMapper extends AbstractParameterizedContextMapper<LdapNodeEntity> {

    /**
     * Main mapping method
     */
    @SuppressWarnings("unchecked")
    public LdapNodeEntity doMapFromContext(DirContextOperations context) {
        LdapNodeEntity nodeEntity;

        SortedSet<String> objectClassSet = context.getAttributeSortedStringSet("objectClass");
        nodeEntity = new LdapNodeEntity();

        if (objectClassSet.contains("kvzBNOTK") || objectClassSet.contains("person")) {
            nodeEntity.setSubclassType(SUBCLASS_TYPE.user);
        } else {
            nodeEntity.setSubclassType(SUBCLASS_TYPE.groupEntity);
        }

        nodeEntity.setUserId(context.getStringAttribute("uniqueIdentifier"));
        nodeEntity.setTelephone(context.getStringAttribute("facsimileTelephoneNumber"));
        nodeEntity.setBoardNotification(context.getStringAttribute("boardNotification"));
        nodeEntity.setEmailSubscription(context.getStringAttribute("emailpbb"));
        nodeEntity.setEmailZtr(context.getStringAttribute("emailztr"));
        nodeEntity.setEmailZvr(context.getStringAttribute("emailZVR"));
        nodeEntity.setGovelloId(context.getStringAttribute("govelloID"));
        nodeEntity.setNotarNet(Boolean.valueOf(context.getStringAttribute("notarnetz")));
        nodeEntity.setZtrNotification(context.getStringAttribute("kvzPreferredDeliveryMethod"));
        nodeEntity.setUid(context.getStringAttribute("uid"));
        nodeEntity.setEmployeeNumber(context.getStringAttribute("employeeNumber"));

        nodeEntity.setName(context.getStringAttribute("ou"));
        if (nodeEntity.getName() == null) {
            nodeEntity.setName(context.getStringAttribute("cn"));
        }
        nodeEntity.setEmail(context.getStringAttribute("mail"));
        if (nodeEntity.getEmail() == null) {
            nodeEntity.setEmail(context.getStringAttribute("email"));
        }
        nodeEntity.setDn(context.getDn());
        nodeEntity.setEntryDN(context.getStringAttribute("entryDN"));
        nodeEntity.setObjectClass(objectClassSet);
        nodeEntity.setMemberOf(context.getAttributeSortedStringSet("memberOf"));
        nodeEntity.setCryptoContainer((byte[]) context.getObjectAttribute("cryptoContainer"));
        nodeEntity.setUserPKCS12((byte[]) context.getObjectAttribute("userPKCS12"));
        nodeEntity.setUserCertificate((byte[]) context.getObjectAttribute("userCertificate;binary"));

        return nodeEntity;
    }
}