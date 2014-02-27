package de.wps.usermanagement.web.ext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.wps.usermanagement.persistence.model.web.RestResponseWrapper;
import de.wps.usermanagement.persistence.model.web.User;
import de.wps.usermanagement.service.UserExtService;
import de.wps.usermanagement.web.BaseController;
/**
 * User REST Service Controller
 * @author anna
 */
@Controller
public class UserServiceController extends BaseController {
    @Autowired
    private UserExtService userExtService;

    /**
     * Retrieves all users of the requested board
     * @param boardName name of the board
     * @return list of end users
     * @throws NamingException in case of non-existent user
     */
    @RequestMapping(value = "/ext/user/boards/{boardName}", method = RequestMethod.GET)
    @ResponseBody
    public RestResponseWrapper<User> getBoardsByUserId(@PathVariable String boardName) throws NamingException {
        return new RestResponseWrapper<User>("OK", "OK", userExtService.getUsersByBoardName(boardName), "User");
    }

    /**
     * Retrieves users by id
     * @param userId user id
     * @return end user
     * @throws NamingException in case of non-existent user
     */
    @RequestMapping(value = "/ext/user/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public RestResponseWrapper<User> getUser(@PathVariable String userId) throws NamingException {
        List<User> list = new ArrayList<User>();
        list.add(userExtService.getUser(userId));
        return new RestResponseWrapper<User>("OK", "OK", list, "User");
    }

    /**
     * Updates cryptoContainer for user
     * @param userJson json string
     * @return {@link RestResponseWrapper} with empty properties and status line OK or FAIL
     * @throws IOException streaming error
     * @throws NamingException in case of non-existent user
     */
    @RequestMapping(value = "/ext/user/cryprocontainer", method = RequestMethod.PUT)
    @ResponseBody
    public RestResponseWrapper<Object> updateCryptocontainer(@RequestBody String userJson) 
            throws IOException, NamingException {
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(userJson, User.class);
        boolean res = userExtService.updateCryptocontainer(user);
        return new RestResponseWrapper<Object>(res ? "OK" : "Update failed", res ? "OK" : "FAIL", null, "User.cryptocontainer");
    }
    
    /**
     * Updates userCertificate for user
     * @param userJson json string
     * @return {@link RestResponseWrapper} with empty properties and status line OK or FAIL
     * @throws IOException streaming error
     * @throws NamingException in case of non-existent user
     */
    @RequestMapping(value = "/ext/user/certificate", method = RequestMethod.PUT)
    @ResponseBody
    public RestResponseWrapper<Object> updateUserCertificate(@RequestBody String userJson) 
            throws IOException, NamingException {
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(userJson, User.class);
        boolean res = userExtService.updateUserCertificate(user);
        return new RestResponseWrapper<Object>(res ? "OK" : "Update failed", res ? "OK" : "FAIL", null, "User.userCertificate");
    }
    
    /**
     * Updates userPKCS12 for user
     * @param userJson json string
     * @return {@link RestResponseWrapper} with empty properties and status line OK or FAIL
     * @throws IOException streaming error
     * @throws NamingException in case of non-existent user
     */
    @RequestMapping(value = "/ext/user/pkcs12", method = RequestMethod.PUT)
    @ResponseBody
    public RestResponseWrapper<Object> updateUserPKCS12(@RequestBody String userJson) 
            throws IOException, NamingException {
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(userJson, User.class);
        boolean res = userExtService.updateUserPKCS12(user);
        return new RestResponseWrapper<Object>(res ? "OK" : "Update failed", res ? "OK" : "FAIL", null, "User.userPKCS12");
    }

}
