package vc.thinker.cabbage.user.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.sinco.data.core.Page;
import com.sinco.dic.client.DicNameMappingHandle;

import vc.thinker.cabbage.user.CommonConstants;
import vc.thinker.cabbage.user.CouponConstants;
import vc.thinker.cabbage.user.bo.UserCouponBO;
import vc.thinker.cabbage.user.mapper.UserCouponMapper;
import vc.thinker.cabbage.user.model.UserCoupon;
import vc.thinker.cabbage.user.model.UserCouponExample;

@Repository
public class UserCouponDao {

	@Autowired
	private UserCouponMapper mapper;
	
	@Autowired
	private DicNameMappingHandle mappingHandle;
	
	/**
	 * 根据用户查询
	 * @param uid
	 * @param page
	 * @return
	 */
	public List<UserCouponBO> findPageListByUid(Long uid,Page<UserCouponBO> page){
		UserCouponExample example=new UserCouponExample();
		example.setPage(page);
		
		example.createCriteria().andUidEqualTo(uid).andStatusEqualTo(CouponConstants.USER_COUPON_STATUS_1)
		.andExpireDateGreaterThanOrEqualTo(new Date());
		
		return mappingHandle.mappinHandle(mapper.selectByExample(example));
	}
	
	/**
	 * 根据用户和地区查找
	 * @param uid
	 * @param cityId
	 * @return
	 */
	public List<UserCouponBO> findByUserAndCity(Long uid,String cityId){
		UserCouponExample example=new UserCouponExample();
		UserCouponExample.Criteria c = example.createCriteria().andUidEqualTo(uid)
				.andStatusEqualTo(CouponConstants.USER_COUPON_STATUS_1)
				.andExpireDateGreaterThanOrEqualTo(new Date());
		List<String> areas=Lists.newArrayList(CommonConstants.AREA_1,cityId);
		c.andCityIdIn(areas);
		example.setOrderByClause("expire_date");
		return mappingHandle.mappinHandle(mapper.selectByExample(example));
	}
	
	public List<UserCouponBO> findByUserAndCityAndCurrency(Long uid,String cityId,String currency){
		UserCouponExample example=new UserCouponExample();
		UserCouponExample.Criteria c = example.createCriteria().andUidEqualTo(uid)
				.andStatusEqualTo(CouponConstants.USER_COUPON_STATUS_1)
				.andExpireDateGreaterThanOrEqualTo(new Date())
				.andCurrencyEqualTo(currency);
		List<String> areas=Lists.newArrayList(CommonConstants.AREA_1,cityId);
		c.andCityIdIn(areas);
		example.setOrderByClause("expire_date");
		return mappingHandle.mappinHandle(mapper.selectByExample(example));
	}
	

	/**
	 * 查找大于该金额的最小优惠劵
	 * @param amount
	 * @param cityId
	 * @return
	 */
	public UserCouponBO findOneByGteAmount(Long uid,BigDecimal amount,String currency,String cityId){
		UserCouponExample example=new UserCouponExample();
		
		UserCouponExample.Criteria c = example.createCriteria();
		c.andUidEqualTo(uid).andAmountGreaterThanOrEqualTo(amount)
		.andStatusEqualTo(CouponConstants.USER_COUPON_STATUS_1)
		.andExpireDateGreaterThanOrEqualTo(new Date())
		.andCurrencyEqualTo(currency);
		
		List<String> areas=new ArrayList<>();
		areas.add(CommonConstants.AREA_1);
		if(cityId != null){
			areas.add(cityId);
		}
		c.andCityIdIn(areas);
		
		example.setOrderByClause("amount");
		example.setLimit(1);
		
		List<UserCouponBO> list=mapper.selectByExample(example);
		return list.isEmpty() ? null : list.get(0);
	}
	
	/**
	 * 查找小于该金额的最大优惠劵
	 * @param amount
	 * @param cityId
	 * @return
	 */
	public UserCouponBO findOneByLteAmount(Long uid,BigDecimal amount,String currency,String cityId){
		UserCouponExample example=new UserCouponExample();
		
		UserCouponExample.Criteria c = example.createCriteria();
		c.andUidEqualTo(uid).andAmountLessThanOrEqualTo(amount)
		.andStatusEqualTo(CouponConstants.USER_COUPON_STATUS_1)
		.andCurrencyEqualTo(currency)
		.andExpireDateGreaterThanOrEqualTo(new Date());
		
		List<String> areas=new ArrayList<>();
		areas.add(CommonConstants.AREA_1);
		if(cityId != null){
			areas.add(cityId);
		}
		c.andCityIdIn(areas);
		
		example.setOrderByClause("amount desc");
		example.setLimit(1);
		
		List<UserCouponBO> list=mapper.selectByExample(example);
		return list.isEmpty() ? null : list.get(0);
	}

   /** generate code begin**/
	public List<UserCouponBO> findAll(){
		UserCouponExample example=new UserCouponExample();
		return mapper.selectByExample(example);
	}
	List<UserCouponBO> findAll(Iterable<java.lang.Long> ids){
		UserCouponExample example=new UserCouponExample();
		example.createCriteria().andIdIn(Lists.newArrayList(ids));
		return mapper.selectByExample(example);
	}
	
	public long count(){
		UserCouponExample example=new UserCouponExample();
		return mapper.countByExample(example);
	}

	public List<UserCoupon> save(Iterable<UserCoupon> entities){
		List<UserCoupon> list=new ArrayList<UserCoupon>();
		for (UserCoupon UserCoupon : entities) {
			list.add(save(UserCoupon));
		}
		return list;
	}
	
	public UserCoupon save(UserCoupon record){
		if(record.getId() == null){
			record.setCreateTime(new Date());
			mapper.insertSelective(record);
		}else{
			mapper.updateByPrimaryKeySelective(record);
		}
		return record;
	}
	

	public void update(UserCoupon record) {
		mapper.updateByPrimaryKeySelective(record);
	}
	
	public UserCouponBO findOne(java.lang.Long id){
		return mapper.selectByPrimaryKey(id);
	}

	public boolean exists(java.lang.Long id){
		if(id == null){
			return false;
		}
		UserCouponExample example=new UserCouponExample();
		example.createCriteria().andIdEqualTo(id);
		return mapper.countByExample(example) > 0;
	}
	
	 public void delete(java.lang.Long id){
		 mapper.deleteByPrimaryKey(id);
	 }
	 
	 public void remove(java.lang.Long id){
		 mapper.deleteByPrimaryKey(id);
	 }

	public void delete(UserCoupon entity){
		 mapper.deleteByPrimaryKey(entity.getId());
	}

	public void delete(Iterable<UserCoupon> entities){
		List<java.lang.Long> ids=Lists.newArrayList();
		for (UserCoupon  entity: entities) {
			ids.add(entity.getId());
		}
		deleteByIds(ids);
	}
	
	public void deleteByIds(Iterable<java.lang.Long> ids){
		UserCouponExample example=new UserCouponExample();
		example.createCriteria().andIdIn(Lists.newArrayList(ids));
		 mapper.deleteByExample(example);
	}

	public void deleteAll(){
		UserCouponExample example=new UserCouponExample();
		mapper.deleteByExample(example);
	}
	/** generate code end**/
}
