package de.wps.usermanagement.web.ext;

import java.lang.reflect.Type;
import java.util.Collection;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.HandlerAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.wps.usermanagement.persistence.model.web.Group;
import de.wps.usermanagement.persistence.model.web.RestResponseWrapper;
import de.wps.usermanagement.persistence.model.web.User;
import de.wps.usermanagement.web.BaseController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:usermanagement-test.xml"})
public class ControllerTester extends AbstractJUnit4SpringContextTests {
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(ControllerTester.class);
    /**
     * get method string
     */
    private static String GET = "GET";

    @Value("${test.de.wps.usermanagement.ldap.testBoard}")
    private String testBoard;
    @Value("${test.de.wps.usermanagement.ldap.testUser}")
    private String testUser;
    @Value("${test.de.wps.usermanagement.ldap.testUserCryptocontainer}")
    private String testUserCryptocontainer;

    @Autowired
    private BoardServiceController boardServiceController;
    @Autowired
    private UserServiceController userServiceController;

    private String getMockRESTresponce(String method, BaseController controller, String path) {
        return getMockRESTresponce(method, controller, path, null, null);
    }

    private String getMockRESTresponce(String method, BaseController controller, String path, String content,
            String contentType) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        if (content != null) {
            request.setContent(content.getBytes());
        }
        if (contentType != null) {
            request.addHeader("Content-Type", contentType + ";charset=UTF-8");
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerAdapter handlerAdapter = applicationContext.getBean(HandlerAdapter.class);
        try {
            handlerAdapter.handle(request, response, controller);
            return response.getContentAsString();
        } catch (Exception e) {
            logger.error("", e);
            Assert.fail(e.getMessage());
        }
        return null;
    }

    /**
     * REST test for Boards of User sequence
     */
    @Test
    public void listBoardsTest() {
        String response = getMockRESTresponce(GET, boardServiceController, "/ext/Board/liferayadmin");
        logger.debug(response);
        Gson gson = new Gson();

        Type typeOfT = new TypeToken<RestResponseWrapper<Group>>() {
        }.getType();

        try {
            RestResponseWrapper<Group> resp = gson.fromJson(response, typeOfT);

            Collection<Group> boards = resp.getProperties();
            Assert.assertFalse("Test list of user boards is empty", boards.isEmpty());
            for (Group board : boards) {
                Assert.assertNotNull("Board name is not reacheable", board.getName());
                Assert.assertTrue("Board name wrong format: " + board.getName(), board.getName().startsWith("site_"));
            }
        } catch (Exception e) {
            logger.error("", e);
            Assert.fail(e.getMessage());
        }

    }

    /**
     * REST test for Users of Board sequence
     */
    @Test
    public void listTypedUsersTest() {
        String response = getMockRESTresponce(GET, userServiceController, "/ext/user/boards/" + testBoard);
        logger.debug(response);
        Gson gson = new Gson();

        Type typeOfT = new TypeToken<RestResponseWrapper<User>>() {
        }.getType();

        try {
            RestResponseWrapper<User> resp = gson.fromJson(response, typeOfT);
            Collection<User> users = resp.getProperties();
            for (User user : users) {
                Assert.assertTrue("User Id is not reacheable: " + gson.toJson(user),
                    user.getUserId() != null || user.getUid() != null);
            }
        } catch (Exception e) {
            logger.error("", e);
            Assert.fail(e.getMessage());
        }

    }

    /**
     * REST test for Users of Board sequence
     */
    @Test
    public void listGetUserTest() {
        String response = getMockRESTresponce(GET, userServiceController, "/ext/user/" + testUserCryptocontainer);
        logger.debug(response);
        Gson gson = new Gson();
        TypeReference<RestResponseWrapper<User>> typeR = new TypeReference<RestResponseWrapper<User>>() {
        };

        try {
            ObjectMapper mapper = new ObjectMapper();
            RestResponseWrapper<User> resp = mapper.readValue(response, typeR);
            Collection<User> users = resp.getProperties();
            for (User user : users) {
                Assert.assertTrue("User Id is not reacheable: " + gson.toJson(user),
                    user.getUserId() != null || user.getUid() != null);
                Assert.assertNotNull("test user must have cryptocontainer", user.getCryptoContainer());
            }
        } catch (Exception e) {
            logger.error("", e);
            Assert.fail(e.getMessage());
        }

    }

    /**
     * REST test for cryptoUpdate
     */
    @Test
    public void cryptoUpdateTest() {

        String userJson = "{\"uid\":\""
                + testUserCryptocontainer
                + "\",\"cryptoContainer\":" +
                "\"/9j/4QAYRXhpZgAASUkqAAgAAAAAAAAAAAAAAP/sABFEdWNreQABAAQAAABbAAD/4QMpaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wLwA8P3hwYWNrZXQgYmVnaW49Iu+7vyIgaWQ9Ilc1TTBNcENlaGlIenJlU3pOVGN6a2M5ZCI/PiA8eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4OnhtcHRrPSJBZG9iZSBYTVAgQ29yZSA1LjAtYzA2MCA2MS4xMzQ3NzcsIDIwMTAvMDIvMTItMTc6MzI6MDAgICAgICAgICI+IDxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+IDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiIHhtbG5zOnhtcE1NPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvbW0vIiB4bWxuczpzdFJlZj0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL3NUeXBlL1Jlc291cmNlUmVmIyIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOkU0QkFENTQxQTY5MzExRTFBQTE4OTc4QzJEQTgzNDFDIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOkU0QkFENTQwQTY5MzExRTFBQTE4OTc4QzJEQTgzNDFDIiB4bXA6Q3JlYXRvclRvb2w9IkFkb2JlIFBob3Rvc2hvcCBDUzUgV2luZG93cyI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuZGlkOkU4NkY1MENFOTNBNkUxMTFCQkYzOEFERUZGMjM0MTFFIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOkU4NkY1MENFOTNBNkUxMTFCQkYzOEFERUZGMjM0MTFFIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+/+4ADkFkb2JlAGTAAAAAAf/bAIQAAQEBAQEBAQEBAQIBAQECAgEBAQEC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\"}";
        String response = getMockRESTresponce("PUT", userServiceController, "/ext/user/cryprocontainer", userJson,
            "application/json");
        logger.debug(response);
        Gson gson = new Gson();

        try {
            Type typeOfT = new TypeToken<RestResponseWrapper<Object>>() {
            }.getType();
            RestResponseWrapper<Object> errorMessage = gson.fromJson(response, typeOfT);
            Assert.assertEquals("status is wrong", "OK", errorMessage.getStatus());
        } catch (Exception e) {
            logger.error("", e);
            Assert.fail(e.getMessage());
        }

    }
    
    /**
     * REST test for certificateUpdate
     */
    @Test
    public void certificateUpdateTest() {

        String userJson = "{\"uid\":\""
                + testUserCryptocontainer
                + "\",\"userCertificate\":" 
                +"\""
                +"MIIFAjCCA+qgAwIBAgIEPERcgjANBgkqhkiG9w0BAQUFADBdMRgwFgYJKoZIhvcNAQkBFglwa2lAc2suZWUxCzAJBgNVBAYTAkVFMSIwIAYDVQQKExlBUyBTZXJ0aWZpdHNlZXJpbWlza2Vza3VzMRAwDgYDVQQDEwdKdXVyLVNLMB4XDTAyMDExNTE2NDQ1MFoXDTEyMDExMzE2NDQ1MFowfDEYMBYGCSqGSIb3DQEJARYJcGtpQHNrLmVlMQswCQYDVQQGEwJFRTEiMCAGA1UEChMZQVMgU2VydGlmaXRzZWVyaW1pc2tlc2t1czEPMA0GA1UECxMGRVNURUlEMQowCAYDVQQEEwExMRIwEAYDVQQDEwlFU1RFSUQtU0swggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCLeZO5NVo3zbwA8eFVCrrbeZQKvPDB7LUDPvzCqw7U2sC+IwEOdjjpJRF4lxFs+f8yC1bP+rqtWzrKhhJ2owfSAlIZMbly/OFjfLqOcyyi7qdfA/66u+69u/DY9tW5fqW93D73v5WNcNoIemCTydh9IFkQvMihWKH7LblBzCHa4W6qUcBZ7QsBgYpQS9n9fGJt5D2wCDeq0pF1Zy72G3CQFrpuR/aPG28tv9r+C7oqncapbiJ7xIOa77Fm3o07M/9aarq/m1oHEp9CxYiH9nmD3kyMe8yxw5v02MTMmAcxOm83z5O4oXSDTALG5gDfZNPjJaNPno7J8FuGrI3vV8z3AgMBAAGjggGpMIIBpTAMBgNVHRMEBTADAQH/MA4GA1UdDwEB/wQEAwIB5jCCARYGA1UdIASCAQ0wggEJMIIBBQYKKwYBBAHOHwEBATCB9jCB0AYIKwYBBQUHAgIwgcMegcAAUwBlAGUAIABzAGUAcgB0AGkAZgBpAGsAYQBhAHQAIABvAG4AIAB2AOQAbABqAGEAcwB0AGEAdAB1AGQAIABBAFMALQBpAHMAIABTAGUAcgB0AGkAZgBpAHQAcwBlAGUAcgBpAG0AaQBzAGsAZQBzAGsAdQBzACAAYQBsAGEAbQAtAFMASwAgAHMAZQByAHQAaQBmAGkAawBhAGEAdABpAGQAZQAgAGsAaQBuAG4AaQB0AGEAbQBpAHMAZQBrAHMwIQYIKwYBBQUHAgEWFWh0dHA6Ly93d3cuc2suZWUvY3BzLzArBgNVHR8EJDAiMCCgHqAchhpodHRwOi8vd3d3LnNrLmVlL2p1dXIvY3JsLzAfBgNVHSMEGDAWgBQEqnpHo+SJrxrPCkCnGD9v7+l9vjAdBgNVHQ4EFgQUeBe1BfmzWM1ZjN5nXkQGTHWGaV0wDQYJKoZIhvcNAQEFBQADggEBAFIsMHaq4Ffkrxmzw38rHYh5Ia5JGxjtWfPpag9pBtQNZHzY8j97xfPI15haE9Ah3u1WC+bsU2SndVSUGaZ0gKafMxDOy2DUw3B84ymbNRiAFSWty+aKrMCjtdlPktbSQmxNSJAX9vVtM4Y2ory+dtAQ7g11GKHJ+l8BDUpOJA+l8hvS2l4K5whWDHCSqlplMiHPIKgBVArFRNzAq6dquMY+kS3e2PL+PM4GdDW5lRHR/6KUy0BHP2gX/BO4mYQ3BH2BHImUclNras0HISnV/pt6hIkgd1PsFt3rtEolAWP4DWBmc4zAYQJ5t0cEwFM329zCXSGIQIm3a1cMugF5Q/k="
                +"\"}";
        String response = getMockRESTresponce("PUT", userServiceController, "/ext/user/certificate", userJson,
            "application/json");
        logger.debug(response);
        Gson gson = new Gson();

        try {
            Type typeOfT = new TypeToken<RestResponseWrapper<Object>>() {
            }.getType();
            RestResponseWrapper<Object> errorMessage = gson.fromJson(response, typeOfT);
            Assert.assertEquals("status is wrong", "OK", errorMessage.getStatus());
        } catch (Exception e) {
            logger.error("", e);
            Assert.fail(e.getMessage());
        }

    }
    
    /**
     * REST test for pkcs12Update
     */
    @Test
    public void pkcs12UpdateTest() {

        String userJson = "{\"uid\":\""
                + testUserCryptocontainer
                + "\",\"userPKCS12\":" 
                +"\""
                +"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"
                +"\"}";
        String response = getMockRESTresponce("PUT", userServiceController, "/ext/user/pkcs12", userJson,
            "application/json");
        logger.debug(response);
        Gson gson = new Gson();

        try {
            Type typeOfT = new TypeToken<RestResponseWrapper<Object>>() {
            }.getType();
            RestResponseWrapper<Object> errorMessage = gson.fromJson(response, typeOfT);
            Assert.assertEquals("status is wrong", "OK", errorMessage.getStatus());
        } catch (Exception e) {
            logger.error("", e);
            Assert.fail(e.getMessage());
        }

    }

    /**
     * REST test for error
     */
    @Test
    public void errorTest() {
        String response = userServiceController.handleException(new Exception("Wrong user"));
        logger.debug(response);
        Gson gson = new Gson();

        try {
            Type typeOfT = new TypeToken<RestResponseWrapper<String>>() {
            }.getType();
            RestResponseWrapper<String> errorMessage = gson.fromJson(response, typeOfT);
            Assert.assertEquals("status is wrong", "ERROR", errorMessage.getStatus());
        } catch (Exception e) {
            logger.error("", e);
            Assert.fail(e.getMessage());
        }

    }

}
