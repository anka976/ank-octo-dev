package de.wps.usermanagement.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Basic controller for REST
 */
public class BaseController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler
    /**
     * Handles all exceptions for REST
     * @param ex Exception
     * @return ErrorMessage object
     */
    public String handleException(Exception ex) {
        logger.error(null, ex);
        return "{\"type\":\"error\",\"status\":\"ERROR\",\"message\":\"" + ex.getMessage() + "\",\"properties\":null}";
    }
}
