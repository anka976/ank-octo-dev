package de.wps.usermanagement.web.ext;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.wps.usermanagement.persistence.model.web.Group;
import de.wps.usermanagement.persistence.model.web.RestResponseWrapper;
import de.wps.usermanagement.service.BoardExtService;
import de.wps.usermanagement.web.BaseController;
/**
 * Board REST Service Controller
 * @author anna
 *
 */
@Controller
public class BoardServiceController extends BaseController {
    @Autowired
    private BoardExtService boardExtService;

    /**
     * gets all boards of the provided users
     * @param userId iniqueIdentifier of user
     * @return list of boards
     * @throws NamingException in case of invalid user
     */
    @RequestMapping(value = "/ext/Board/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public RestResponseWrapper<Group> getBoardsByUserId(@PathVariable String userId) throws NamingException {
        return new RestResponseWrapper<Group>("OK", "OK", boardExtService.getBoardsByUserId(userId), "Board");
    }

}
