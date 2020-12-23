package vc.thinker.cabbage.web.action;


import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import cn.jiguang.common.utils.StringUtils;
import vc.thinker.cabbage.sys.dao.SysSettingDao;
import vc.thinker.cabbage.sys.model.SysSetting;
import vc.thinker.cabbage.sys.service.CountryService;
import vc.thinker.cabbage.user.CommonConstants;
import vc.thinker.cabbage.user.bo.IntegralLogBO;
import vc.thinker.cabbage.user.bo.MemberBO;
import vc.thinker.cabbage.user.bo.UserMoneyBO;
import vc.thinker.cabbage.user.dao.UserMoneyDao;
import vc.thinker.cabbage.user.service.IntegralLogService;
import vc.thinker.cabbage.user.service.MemberService;
import vc.thinker.cabbage.user.vo.IntegralLogVO;
import vc.thinker.cabbage.user.vo.MemberVO;
import vc.thinker.cabbage.common.MyPage;
import vc.thinker.core.security.SecurityMapping;
import vc.thinker.sys.bo.UserAccountBO;
import vc.thinker.sys.contants.SysUserContant;
import vc.thinker.sys.model.User;
import vc.thinker.sys.service.UserAccountService;
import vc.thinker.web.utils.UserUtils;

/**
 * 客户端用户
 * @author thinker
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/customer")
public class MemberController {

	@Value("${adminPath}")
	private String adminPath;
	
	@Autowired
	private SysSettingDao sysSettingDao;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private UserMoneyDao userMoneyDao;
	
	@Autowired
	private IntegralLogService integralLogService;
	
	@Autowired
	private UserAccountService userAccountService;
	
	@Autowired
	private CountryService countryService;
	
	@RequiresPermissions("sys:customer:view")
	@SecurityMapping(name="role.list",permGroup="role.member",userType=SysUserContant.USER_TYPE_1)
	@RequestMapping({"list"})
	public String list(@ModelAttribute("page") MyPage<MemberBO> page,Model model,MemberVO vo) {
		
		memberService.findUnionInfoByPage(page,vo);
		SysSetting sysSet = sysSettingDao.findOne();
		model.addAttribute("sysSet",sysSet);
		model.addAttribute("page", page);
		model.addAttribute("vo", vo);
		model.addAttribute("countries",countryService.findAll());
		
		return "modules/cus/cus_list";
	}
	
	@RequiresPermissions("sys:customer:modify")
	@SecurityMapping(name="role.edit",permGroup="role.member",userType=SysUserContant.USER_TYPE_1)
	@RequestMapping({"modify"})
	@ResponseBody
	public String modify(Long uid ,Model model,String mark) {
		
		memberService.enableOrDisable(mark,uid);
		
		return "1";
	}
	
	
	@RequiresPermissions("sys:customer:modify")
	@SecurityMapping(name="role.edit",permGroup="role.member",userType=SysUserContant.USER_TYPE_1)
	@RequestMapping({"edit"})
	public String edit(Model model,MemberBO bo){
		
		MemberBO info = null;
		
		if(null != bo.getUid()){
			info = memberService.findOne(bo.getUid());
		}else {
			info = new MemberBO();
		}
		
		model.addAttribute("info",info);
		
		return "modules/cus/memberEdit";
	}
	
	
	@RequiresPermissions("sys:customer:modify")
	@SecurityMapping(name="role.edit",permGroup="role.member",userType=SysUserContant.USER_TYPE_1)
	@RequestMapping({"save"})
	public String save(MemberBO bo){
		
		User user = UserUtils.getUser();
		memberService.adminCreateUser(user,bo);
		
		return "redirect:"+ adminPath + "/sys/customer/list";
	}
	
	@RequestMapping("checkIdCard")
	@ResponseBody
	public Boolean checkIdCard(Long uid,String idCard) {
		
		Boolean result = memberService.checkIdCard(uid,idCard);
		return result;
	}
	
	@RequestMapping("checkNickname")
	@ResponseBody
	public Boolean checkNickname(Long uid,String nickname){
		
		Boolean result = memberService.checkNickname(uid, nickname);
		return result;
	}
	
	@RequestMapping("checkMobile")
	@ResponseBody
	public Boolean checkMobile(Long uid,String mobile){
	
		Boolean result = memberService.checkMobile(uid, mobile);
		return result;
	}
	
	@RequestMapping("checkJobNumber")
	@ResponseBody
	public Boolean checkJobNumber(Long uid,String jobNumber){
		Boolean result = memberService.checkJobNumber(uid, jobNumber);
		return result;
	}
	
//	@RequiresPermissions("sys:customer:view")
//	@SecurityMapping(name="list",permGroup="role.member",userType=SysUserContant.USER_TYPE_1)
//	@RequestMapping({"sendMsgForm"})
//	public String sendMsgForm(Long uid,Model model){
//		
//		MemberBO info = memberService.findByUid(uid);
//		model.addAttribute("info",info);
//		
//		return "modules/sys/selectMsgType";
//	}
	
	
	@RequiresPermissions("sys:customer:detail")
	@SecurityMapping(name="role.detail",permGroup="role.member",userType=SysUserContant.USER_TYPE_1)
	@RequestMapping({"detail"})
	public String detail(Long uid,Model model){
		
		MemberBO info = memberService.findOne(uid);
		model.addAttribute("info",info);
		
		//是否进行第三方账号的绑定
		UserAccountBO wxAccount = userAccountService.findByUid(uid, CommonConstants.ACCOUNT_TYPE_4);
		if(null != wxAccount){
			model.addAttribute("isBingdingWx",true);
		}
		
		UserAccountBO feedAccount = userAccountService.findByUid(uid,CommonConstants.ACCOUNT_TYPE_8);
		if(null != feedAccount){
			model.addAttribute("isBingdingFB",true);
		}
		//查询是否自主注册
		SysSetting setting = sysSettingDao.findOne();
		
		if(null != setting && setting.getIsUserRegister()){
			//用户注册
			model.addAttribute("u_regist",true);
		}
		
		if(null != setting && setting.getIsOpenBalance()){
			UserMoneyBO userMoney = userMoneyDao.findOne(info.getUid());
			if(null != userMoney){
				info.setBalance(userMoney.getAvailableBalance());
			}
			model.addAttribute("is_open_balance",true);
		}
		
		if(null != setting && setting.getIsOpenMemberCard()){
			model.addAttribute("is_open_vip",true);
		}
		
		if(null != setting.getIsNeedAuthen() && setting.getIsNeedAuthen()){
			
			if(!StringUtils.isEmpty(info.getCredentialsImages())){
				JSONArray array = (JSONArray) JSON.parse(info.getCredentialsImages());
				
				List<String> img_list = new ArrayList<String>();
				for(int i=0;i<array.size();i++){
					img_list.add(array.getString(i));
				}
				model.addAttribute("img_list",img_list);
			}
		}
		
		model.addAttribute("setting",setting);
		return "modules/cus/cus_detail";
	}
	
//	@RequiresPermissions("sys:customer:view")
//	@SecurityMapping(name="role.list",permGroup="role.member",userType=SysUserContant.USER_TYPE_1)
//	@RequestMapping({"applyCheck"})
//	public String applyCheck(Long uid,Model model){
//		
//		MemberBO info = memberService.findOne(uid);
//		model.addAttribute("info",info);
//		
//		SysSetting setting = sysSettingDao.findOne();
//		model.addAttribute("setting",setting);
//		
//		//处理认证图片
//		if(!StringUtils.isEmpty(info.getCredentialsImages())){
//			
//			JSONArray array = (JSONArray) JSON.parse(info.getCredentialsImages());
//			
//			List<String> img_list = new ArrayList<String>();
//			for(int i=0;i<array.size();i++){
//				img_list.add(array.getString(i));
//			}
//			model.addAttribute("img_list",img_list);
//		}
//		
//		return "modules/cus/cus_check";
//	}
//	
//	@RequiresPermissions("sys:customer:view")
//	@SecurityMapping(name="role.list",permGroup="role.member",userType=SysUserContant.USER_TYPE_1)
//	@RequestMapping({"applyCheckSave"})
//	public String applyChcekSave(MemberBO bo) {
//		
//		memberService.adminCheckMemer(bo);
//		return "redirect:"+ adminPath + "/sys/customer/check_list";
//	}
//	
//	@RequiresPermissions("sys:customer:view")
//	@SecurityMapping(name="role.list",permGroup="role.member",userType=SysUserContant.USER_TYPE_1)
//	@RequestMapping({"check_list"})
//	public String check_list(Model model,MyPage<MemberBO> page,MemberVO vo) {
//		
//		SysSetting sysSet = sysSettingDao.findOne();
//		
//		memberService.findCheckListByPageAndVo(page,vo);
//		
//		model.addAttribute("sysSet",sysSet);
//		model.addAttribute("page",page);
//		model.addAttribute("vo",vo);
//		
//		return "modules/cus/cus_check_list";
//		
//	}
	
}
