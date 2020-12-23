/**
f * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package vc.thinker.cabbage.web.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sinco.common.area.CountryUtil;
import com.sinco.common.security.PasswordUtil;
import com.sinco.common.utils.DateUtils;
import com.sinco.common.utils.StringUtils;
import vc.thinker.cabbage.stats.StatsService;
import vc.thinker.cabbage.stats.bo.CountStatsBO;
import vc.thinker.cabbage.stats.vo.StatsVO;
import vc.thinker.cabbage.user.CommonConstants;
import vc.thinker.cabbage.user.bo.RefereeBO;
import vc.thinker.cabbage.user.dao.RefereeDao;
import vc.thinker.cabbage.web.security.RefereePrincipal;
import vc.thinker.core.web.BaseController;
import vc.thinker.sys.bo.UserAccountBO;
import vc.thinker.sys.model.User;
import vc.thinker.sys.service.UserAccountService;
import vc.thinker.web.utils.UserUtils;

/**
 * 用户Controller
 * @author ThinkGem
 * @version 2013-5-31
 */
@Controller
@RequestMapping(value = "${adminPath}")
public class IndexController extends BaseController {
	
	@Autowired
	private UserAccountService accountService;
	
	@Autowired
	private StatsService statsService;
	
	@Autowired
	private  RefereeDao refereeDao;
	
	@Value("${adminPath}")
	private String adminPath;
	

	
	@RequestMapping("userInfo")
	public String info(RefereeBO user, Model model) {
		User currentUser=UserUtils.getUser();
		user = refereeDao.findOne(currentUser.getId());
		model.addAttribute("user", user);
		return "modules/userInfo";
	}
	
	@RequestMapping("saveInfo")
	public String saveInfo(RefereeBO user, Model model) {
		User currentUser = UserUtils.getUser(true);
		user.setUid(currentUser.getId());
		refereeDao.save(user);
		model.addAttribute("message", "Saved successfully");
		
		return "redirect:"+adminPath+"/userInfo";
	}

	@RequiresUser
	@RequestMapping("modifyPwd")
	public String modifyPwd(String oldPassword, String newPassword, Model model) {
		User user = UserUtils.getUser();
		UserAccountBO account=accountService.findByUid(user.getId(), CommonConstants.ACCOUNT_TYPE_9);
		if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)){
			if (PasswordUtil.validatePassword(oldPassword, account.getPassword())){
				accountService.passwordUpdate(user.getId(), newPassword);
				model.addAttribute("message", "Saved successfully");
			}else{
				model.addAttribute("message", "Failed to change password, wrong password");
			}
		}
		model.addAttribute("user", user);
		return "modules/userModifyPwd";
	}
	
	
	@RequestMapping("index")
	public String index( Model model, @ModelAttribute("vo") StatsVO vo) {
		RefereePrincipal user = (RefereePrincipal)UserUtils.getPrincipal();
		vo.setSellerId(user.getReferee().getUid());
		String userCurrency= CountryUtil.get(user.getReferee().getCountry()).getCurrencyCode();
		
		  Integer timeNums;
        String unit = "";
        if (StringUtils.isEmpty(vo.getStatsType())) {
            vo.setStatsType("2");
        }
        if (vo.getStatsType().equals("1")) {
            vo.setTime(vo.getYear());
        } else {
            vo.setTime(vo.getMonth());
        }
        if (vo.getTime() == null || vo.getTime().equals("")) {
            if (vo.getStatsType().equals("1")) {
                Calendar a = Calendar.getInstance();
                vo.setTime(a.get(Calendar.YEAR) + "");
                vo.setYear(vo.getTime());
            }
            if (vo.getStatsType().equals("2")) {
                Date date = new Date();
                vo.setTime(DateUtils.formatDate(date, "yyyy-MM"));
                vo.setMonth(vo.getTime());
            }
        }
        if (vo.getStatsType().equals("1")) {
            timeNums = 12;
            unit = "";
        } else {
            timeNums = getDaysOfMonth(vo.getTime());
            unit = "";
        }


        List<CountStatsBO> list = statsService.findRebateStats(vo);
        String timeStr[] = new String[timeNums];
        String numStr[] = new String[timeNums];
        for (int i = 0; i < timeNums; i++) {
            numStr[i] = "0";
            timeStr[i] = "'" + (i + 1) + unit + "'";
        }

        if (list != null && list.size() > 0) {
            list.forEach(e -> {
                for (int i = 1; i <= timeNums; i++) {
                    if (Integer.parseInt(e.getStatsTime()) == i) {
                        numStr[i - 1] = e.getConsume().toString();
                    }
                }
            });
        }
        model.addAttribute("timeStr", Arrays.toString(timeStr));
        model.addAttribute("numStr", Arrays.toString(numStr));
        
        //总收益
        BigDecimal total = statsService.sumRebate(user.getReferee().getUid(),null);
        //今日收益
        BigDecimal today = statsService.sumRebate(user.getReferee().getUid(),DateUtils.formatDate(new Date()));
        //昨日收益
        BigDecimal yestoday = statsService.sumRebate(user.getReferee().getUid(),DateUtils.formatDate(DateUtils.addDays(new Date(), -1)));
        
        model.addAttribute("total", total);
        model.addAttribute("today", today);
        model.addAttribute("yestoday", yestoday);
        model.addAttribute("userCurrency", userCurrency);
        
		return "modules/sellerIndex";
	}
	
	 private int getDaysOfMonth(String month) {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        Date date = new Date();
	        try {
	            date = sdf.parse(month + "-1");
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(date);
	        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	  }
	
}
