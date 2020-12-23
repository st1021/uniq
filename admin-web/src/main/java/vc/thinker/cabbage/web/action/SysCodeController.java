package vc.thinker.cabbage.web.action;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import vc.thinker.cabbage.se.SysCodeService;
import vc.thinker.cabbage.se.bo.SysCodeBO;
import vc.thinker.cabbage.se.vo.SysCodeVO;
import vc.thinker.cabbage.common.MyPage;
import vc.thinker.core.security.SecurityMapping;
import vc.thinker.sys.contants.SysUserContant;

@Controller
@RequestMapping("${adminPath}/sys/sysCode")
public class SysCodeController {

	@Value("${adminPath}")
	private String adminPath;
	
	@Autowired
	private SysCodeService sysCodeService;
	
	@Value("${applet_redirect_url}")
	private String applet_redirect_url;
	
	@RequiresPermissions("sys:sysCode:list")
	@SecurityMapping(name="role.list",permGroup="role.qrCode",userType=SysUserContant.USER_TYPE_1)
	@RequestMapping({"list"})
	public String list(Model model,MyPage<SysCodeBO> page,SysCodeVO vo){
		
		page.setPageSize(50);
 
		if(null == vo.getIsBinding()) {
			vo.setIsBinding(false);
		}
		 
		sysCodeService.findPageByVo(applet_redirect_url,page,vo);
		
		model.addAttribute("page",page);
		model.addAttribute("vo",vo);
		
		return "modules/se/codeList";
		
	}
	
	@RequiresPermissions("sys:sysCode:print")
	@SecurityMapping(name="role.print",permGroup="role.qrCode",userType=SysUserContant.USER_TYPE_1)
	@RequestMapping({"print"})
	@ResponseBody
	public String print(MyPage<SysCodeBO> page,SysCodeVO vo) {
		
		sysCodeService.print(page,vo);
		
		
		return "success";
	}
	
	
	@RequiresPermissions("sys:sysCode:create")
	@SecurityMapping(name="role.create",permGroup="role.qrCode",userType=SysUserContant.USER_TYPE_1)
	@RequestMapping({"save"})
	@ResponseBody
	public String save(String belongType) {
		//生成 100 个 二维码
		sysCodeService.createSysCode(belongType);
		
		return "success";
	}
	
	@RequiresPermissions("sys:sysCode:create")
	@SecurityMapping(name="role.create",permGroup="role.qrCode",userType=SysUserContant.USER_TYPE_1)
	@RequestMapping({"modify"})
	public String modify(Model model) {
		
		SysCodeBO info = new SysCodeBO();
		model.addAttribute("info",info);
		
		return "modules/se/syscode_modify";
	}
	
	@RequestMapping({"checkCode"})
	@ResponseBody
	public Boolean checkCode(String sysCode) {
		
		SysCodeBO bySyscode = sysCodeService.getBySyscode(sysCode);
		if(null != bySyscode) {
			return false;
		}
		return true;
	}
	
	
	@RequiresPermissions("sys:sysCode:create")
	@SecurityMapping(name="role.create",permGroup="role.qrCode",userType=SysUserContant.USER_TYPE_1)
	@RequestMapping({"saveOne"})
	public String saveOne(String sysCode) {
		
		sysCodeService.createOne(sysCode);
		
		return "redirect:" + adminPath + "/sys/sysCode/list";
	}
	
}
