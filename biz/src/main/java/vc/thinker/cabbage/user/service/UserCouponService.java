package vc.thinker.cabbage.user.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinco.data.core.Page;

import vc.thinker.cabbage.se.bo.OrderBO;
import vc.thinker.cabbage.sys.dao.CouponDao;
import vc.thinker.cabbage.user.bo.UserCouponBO;
import vc.thinker.cabbage.user.dao.UserCouponDao;

/**
 * 用户优惠卷
 * @author james
 *
 */
@Service
@Transactional(readOnly=false)
public class UserCouponService {
	
	@Autowired
	private CouponDao couponDao;
	
	@Autowired
	private UserCouponDao userCouponDao;
	
	/**
	 * 翻页查询 
	 * @param uid
	 * @param page
	 * @return
	 */
	public List<UserCouponBO> findPageListByUid(Long uid,Page<UserCouponBO> page){
		return userCouponDao.findPageListByUid(uid, page);
	}
	
	/**
	 * 查找最适合的优惠劵
	 * @param amount
	 * @param cityId
	 * @return
	 */
	public UserCouponBO findFitCoupon(Long uid,BigDecimal amount,String currency,String cityId){
		UserCouponBO coupon=userCouponDao.findOneByGteAmount(uid,amount,currency,cityId);
		if(coupon == null){
			coupon=userCouponDao.findOneByLteAmount(uid,amount,currency,cityId);
		}
		return coupon;
	}
	
	/**
	 * 查找用户可用优惠劵
	 * @param trip
	 * @return
	 */
	public List<UserCouponBO> findByUser(Long uid,String cityId){
		return userCouponDao.findByUserAndCity(uid,cityId);
	}
	
	public List<UserCouponBO> findByOrder(OrderBO order){
		return userCouponDao.findByUserAndCityAndCurrency(order.getUid(), "1", order.getCurrency());
	}

	public UserCouponBO findOne(Long id) {
		return userCouponDao.findOne(id);
	}
}
