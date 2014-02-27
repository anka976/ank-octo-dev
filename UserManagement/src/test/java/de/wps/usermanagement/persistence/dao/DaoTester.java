package de.wps.usermanagement.persistence.dao;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.security.core.codec.Base64;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.wps.usermanagement.persistence.model.web.Group;
import de.wps.usermanagement.persistence.model.web.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:usermanagement-test.xml"})
public class DaoTester extends AbstractJUnit4SpringContextTests {
    /**
     * logger
     */
    final private Logger logger = LoggerFactory.getLogger(DaoTester.class);

    @Value("${test.de.wps.usermanagement.ldap.testBoard}")
    private String testBoard;

    /**
     * UserDao to test
     */
    @Autowired
    protected UserDao userDao;
    /**
     * BoardDao to test
     */
    @Autowired
    protected BoardDao boardDao;

    /**
     * Test Spring context
     */
    @Test
    public void testSpringContext() {
        Assert.assertNotNull("UserDao is null", userDao);
        Assert.assertNotNull("LDAP template is null", userDao.getLdapTemplate());
    }

    /**
     * Tests DAO impl for sequence: Retrieve all Users of a given Board
     * @throws NamingException
     */
    @Test
    public void testLoadUsersByBoard() throws NamingException {
        List<User> listRes = null;
        try {
            listRes = userDao.loadUsersByBoard(testBoard);
        } catch (NameNotFoundException e) {
            logger.error("Object not found", e);
        }
        Assert.assertNotNull("Users list is null", listRes);
        Assert.assertTrue("LDAP should contain users for test Board", !listRes.isEmpty());

        for (User user : listRes) {
            Assert.assertTrue("Board end user should have at least a uniqueIdentifier",
                StringUtils.hasText(user.getUserId()) || StringUtils.hasText(user.getUid()));
            logger.debug(user.getName() + " : " + user.getUserId() + " : " + user.getTelephone());
        }
    }

    @Test
    public void testLoadGroupsByUser() {
        List<Group> listRes = null;
        try {
            listRes = boardDao.loadBoardsByUser("liferayadmin");
        } catch (NamingException e) {
            logger.error("Parent not found", e);
        }
        Assert.assertNotNull(listRes);
        Assert.assertTrue("LDAP should contain boards for test User", !listRes.isEmpty());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String res = gson.toJson(listRes);
        logger.debug("Found boards: " + res);
    }
    @Test
    public void anna(){
        String str = "{'string':'JSON', 'integer': 1, 'double': 2.0, 'boolean': true}";
          JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(str);
 
        System.out.println(jsonObject.toString());
        System.out.println("string: " + jsonObject.get("string"));
        System.out.println("integer: " + jsonObject.get("integer") + jsonObject.get("integer").getClass());
        System.out.println("double: " + jsonObject.get("double"));
        System.out.println("boolean: " + jsonObject.get("boolean"));
    }

    @Test
    public void testGetUser() {
        User user = null;
        try {
            user = userDao.getTechnicalUser("anna1");
            // listRes = boardDao.loadBoardsByUser("thennen");
        } catch (NamingException e) {
            logger.error("user not found", e);
        }
        Assert.assertNotNull(user);
        Assert.assertNotNull("test user must have cryptocontainer", user.getCryptoContainer());
        
        ObjectMapper mapper = new ObjectMapper();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        try {
//            FileOutputStream fos = new FileOutputStream("C:\\Logs\\test.jpg");
//            fos.write((byte[])user.getCryptoContainer());
//            fos.close();
//        } catch (IOException e1) {
//            logger.debug("", e1);
//        }
        

        String res = null;
        try {
            res = mapper.writeValueAsString(user);
            logger.debug(res);
            TestUser parsed = gson.fromJson(res, TestUser.class);
            Object base64str = parsed.cryptoContainer;
            Assert.assertTrue(base64str instanceof String);
            Assert.assertTrue(Base64.isBase64(((String)base64str).getBytes()));
            base64str = parsed.userCertificate;
            Assert.assertTrue(base64str instanceof String);
            Assert.assertTrue(Base64.isBase64(((String)base64str).getBytes()));
            base64str = parsed.userPKCS12;
            Assert.assertTrue(base64str instanceof String);
            Assert.assertTrue(Base64.isBase64(((String)base64str).getBytes()));
            
            logger.debug("Found user: " + mapper.defaultPrettyPrintingWriter()
                .writeValueAsString(user));
        } catch (JsonGenerationException e) {
            logger.debug("", e);
        } catch (JsonMappingException e) {
            logger.debug("", e);
        } catch (IOException e) {
            logger.debug("", e);
        }
    }
    
    /**
     * internal test user class
     * @author anna
     *
     */
    private class TestUser {
        private String cryptoContainer;
        private String userCertificate;
        private String userPKCS12;

    }

}
