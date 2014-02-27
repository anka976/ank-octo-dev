package de.wps.usermanagement.persistence.model.web;

/**
 * REST data transfer object for notary member
 * @author anna
 */
public class User {

    /**
     * LDAP Attribute: ou Name of user
     */
    private String name;
    /**
     * LDAP Operational Attribute entryDN
     */
    private String entryDN;

    /**
     * Unique Identifier of User Also called AmtstaetId in other context LDAP Attribute: uniqueIdentifier
     */
    private String userId;
    /**
     * Default email address LDAP Attribute: email
     */
    private String email;
    /**
     * Email adress for ZTR LDAP Attribute: emailztr
     */
    private String emailZtr;
    /**
     * Email adress for ZVR LDAP Attribute: emailZVR
     */
    private String emailZvr;
    /**
     * emailSubscription Email adress for subscriptions LDAP Attribute: emailpbb
     */
    private String emailSubscription;
    /**
     * telephone Telephone number of user LDAP Attribute: facsimileTelephoneNumber
     */
    private String telephone;
    /**
     * Id for sending EGVP/OSCI messages LDAP Attribute: govelloID
     */
    private String govelloId;
    /**
     * flag is user is member of notar network LDAP Attribute: notarnetz
     */
    private boolean notarNet;
    /**
     * boardNotification message type user wants for board notifications LDAP Attribute: kvzBoardNotification
     * EnumValue=Mail,EGVP
     */
    private String boardNotification;

    /**
     * ztrNotification message type user wants for ztr notifications LDAP Attribute: kvzPreferredDeliveryMethod
     * EnumValue=Mail,EGVP,Fax
     */
    private String ztrNotification;
    
    /**
     * technical user id
     */
    private String uid;
    /**
     * technical user employeeNumber
     */
    private String employeeNumber;
    /**
     * crypto user data 
     */
    private byte[] cryptoContainer;
    
    /**
     * certificate
     */
    private byte[] userCertificate;
    /**
     * PKCS12
     */
    private byte[] userPKCS12;

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

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the emailZtr
     */
    public String getEmailZtr() {
        return emailZtr;
    }

    /**
     * @param emailZtr the emailZtr to set
     */
    public void setEmailZtr(String emailZtr) {
        this.emailZtr = emailZtr;
    }

    /**
     * @return the emailZvr
     */
    public String getEmailZvr() {
        return emailZvr;
    }

    /**
     * @param emailZvr the emailZvr to set
     */
    public void setEmailZvr(String emailZvr) {
        this.emailZvr = emailZvr;
    }

    /**
     * @return the emailSubscription
     */
    public String getEmailSubscription() {
        return emailSubscription;
    }

    /**
     * @param emailSubscription the emailSubscription to set
     */
    public void setEmailSubscription(String emailSubscription) {
        this.emailSubscription = emailSubscription;
    }

    /**
     * @return the telephone
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * @param telephone the telephone to set
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * @return the govelloId
     */
    public String getGovelloId() {
        return govelloId;
    }

    /**
     * @param govelloId the govelloId to set
     */
    public void setGovelloId(String govelloId) {
        this.govelloId = govelloId;
    }

    /**
     * @return the notarNet
     */
    public boolean isNotarNet() {
        return notarNet;
    }

    /**
     * @param notarNet the notarNet to set
     */
    public void setNotarNet(boolean notarNet) {
        this.notarNet = notarNet;
    }

    /**
     * @return the boardNotification
     */
    public String getBoardNotification() {
        return boardNotification;
    }

    /**
     * @param boardNotification the boardNotification to set
     */
    public void setBoardNotification(String boardNotification) {
        this.boardNotification = boardNotification;
    }

    /**
     * @return the ztrNotification
     */
    public String getZtrNotification() {
        return ztrNotification;
    }

    /**
     * @param ztrNotification the ztrNotification to set
     */
    public void setZtrNotification(String ztrNotification) {
        this.ztrNotification = ztrNotification;
    }

    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return the employeeNumber
     */
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    /**
     * @param employeeNumber the employeeNumber to set
     */
    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
    /**
     * @return the cryptoContainer
     */
    public byte[] getCryptoContainer() {
        return cryptoContainer == null ? null : cryptoContainer.clone();
    }
    /**
     * @param cryptoContainer the cryptoContainer to set
     */
    public void setCryptoContainer(byte[] cryptoContainer) {
        this.cryptoContainer = cryptoContainer == null ? null : cryptoContainer.clone();
    }
    /**
     * @return the userCertificate
     */
    public byte[] getUserCertificate() {
        return userCertificate == null ? null : userCertificate.clone();
    }
    /**
     * @param userCertificate the userCertificate to set
     */
    public void setUserCertificate(byte[] userCertificate) {
        this.userCertificate = userCertificate == null ? null : userCertificate.clone();
    }
    /**
     * @return the userPKCS12
     */
    public byte[] getUserPKCS12() {
        return userPKCS12 == null ? null : userPKCS12.clone();
    }
    /**
     * @param userPKCS12 the userPKCS12 to set
     */
    public void setUserPKCS12(byte[] userPKCS12) {
        this.userPKCS12 = userPKCS12 == null ? null : userPKCS12.clone();
    }
}
