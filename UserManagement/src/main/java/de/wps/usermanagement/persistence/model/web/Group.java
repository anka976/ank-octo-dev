package de.wps.usermanagement.persistence.model.web;

/**
 * REST data transfer object for notary board
 * @author anna
 *
 */
public class Group {

    /**
     * LDAP Attribute: ou
     * Name of user
     */
    private String name;
    /**
     * LDAP Operational Attribute entryDN
     */
    private String entryDN;
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the entryDN
     */
    public String getEntryDN() {
        return entryDN;
    }
    /**
     * @param entryDN the entryDN to set
     */
    public void setEntryDN(String entryDN) {
        this.entryDN = entryDN;
    }
}
