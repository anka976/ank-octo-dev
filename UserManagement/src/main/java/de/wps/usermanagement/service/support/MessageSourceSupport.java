package de.wps.usermanagement.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Helper class for easy access to messages from a MessageSource, providing various overloaded getMessage methods.
 * 
 * @author Deniss Afonin
 */
public class MessageSourceSupport {

	@Autowired
	private MessageSourceAccessor messageSourceAccessor;

	public String getMessage(String code) {
		return messageSourceAccessor.getMessage(code);
	}

	public String getMessage(String code, Object... args) {
		return messageSourceAccessor.getMessage(code, args);
	}

	public String getMessage(MessageSourceResolvable resolvable) {
		return messageSourceAccessor.getMessage(resolvable);
	}
}
