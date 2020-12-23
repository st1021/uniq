package vc.thinker.cabbage.user.exception;

import vc.thinker.biz.exception.ServiceException;

/**
 *
 * @author ZhangGaoXiang
 * @time Jan 8, 20204:57:41 PM
 */
public class InvalidEmailException extends ServiceException{

	private static final long serialVersionUID = -6666612965940754913L;

	public InvalidEmailException() {
		super();
	}

	public InvalidEmailException(String message) {
		super(message);
	}

	public InvalidEmailException(Throwable cause) {
		super(cause);
	}

	public InvalidEmailException(String message, Throwable cause) {
		super(message, cause);
	}
}
