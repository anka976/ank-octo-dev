package de.wps.usermanagement.persistence.model.web;

import java.util.List;

import org.codehaus.jackson.annotate.JsonPropertyOrder;

/**
 * Rest Response Wrapper
 * @author anna
 *
 */
@JsonPropertyOrder({"type","message","status","properties"})
public class RestResponseWrapper<T> {

    /**
     *  message
     */
    private String message;
    private String status;
    private List<T> properties;
    private String type;

    /**
     * Constructs RestResponseWrapper
     */
    public RestResponseWrapper() {
    }
    
    /**
     * Constructs RestResponseWrapper with fields
     * @param message technical message
     * @param status status code
     * @param properties List of found object
     * @param type type of response objects
     */
    public RestResponseWrapper(String message, String status, List<T> properties, String type) {
        this.message = message;
        this.status = status;
        this.properties = properties;
        this.type = type;
    }


    /**technical message
     * gets  message
     * @return error message
     */
    public String getMessage() {
        return message;
    }


    /**status code
     * @return the status
     */
    public String getStatus() {
        return status;
    }


    /**List of found object
     * @return the properties
     */
    public List<T> getProperties() {
        return properties;
    }


    /**type of response objects
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(List<T> properties) {
        this.properties = properties;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
}