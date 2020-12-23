package vc.thinker.cabbage.web.filter;

import java.util.Date;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.sinco.common.utils.IPUtil;

import vc.thinker.core.web.security.CaptchaException;
import vc.thinker.core.web.security.FormAuthenticationFilter;
import vc.thinker.sys.model.LoginLog;
import vc.thinker.sys.service.LogService;
import vc.thinker.sys.service.SystemService;
import vc.thinker.web.security.SysPrincipal;

public class EnSysFormAuthenticationFilter extends FormAuthenticationFilter {
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private LogService logService;

	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws Exception {
		SysPrincipal principal = (SysPrincipal) subject.getPrincipal();

		// 更新登录IP和时间
		systemService.updateUserLoginInfo(principal.getLoginName(), principal.getHost());

		// 插入登入日志
		LoginLog loginLog = new LoginLog();
		loginLog.setUserId(Long.parseLong(principal.getId()));

		loginLog.setLoginIp(IPUtil.getIpAddr(request));
		loginLog.setLoginTime(new Date());
		logService.insertLoginLog(loginLog);
		
		return super.onLoginSuccess(token, subject, request, response);
	}
	
	/**
	 * 跳转处理
	 */
    protected void issueSuccessRedirect(ServletRequest request, ServletResponse response) throws Exception {
    	SysWebUtils.redirectToSavedRequest(request, response, getSuccessUrl());
    }
	
	
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		if(e instanceof CaptchaException){
			   request.setAttribute("message", "Verification code error");
		   }else if(e instanceof UnknownAccountException && StringUtils.isNotEmpty(e.getMessage())){
			   request.setAttribute("message", e.getMessage());
		   }else{
			   e.getMessage();
			   request.setAttribute("message","The username or password does not exist");
		   }
		 return  super.onLoginFailure(token, e, request, response);
	}

	public SystemService getSystemService() {
		return systemService;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}
	
	
}
