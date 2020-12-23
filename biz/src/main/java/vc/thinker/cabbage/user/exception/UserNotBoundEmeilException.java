package vc.thinker.cabbage.user.exception;

import vc.thinker.biz.exception.ServiceException;

/**
 *
 * @author ZhangGaoXiang
 * @time Jan 8, 20204:44:15 PM
 */
public class UserNotBoundEmeilException extends ServiceException{

	private static final long serialVersionUID = -6666612965940754913L;

	public UserNotBoundEmeilException() {
		super();
	}

	public UserNotBoundEmeilException(String message) {
		super(message);
	}

	public UserNotBoundEmeilException(Throwable cause) {
		super(cause);
	}

	public UserNotBoundEmeilException(String message, Throwable cause) {
		super(message, cause);
	}
}
