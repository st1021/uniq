package vc.thinker.cabbage.stats.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

import vc.thinker.cabbage.stats.bo.CountStatsBO;
import vc.thinker.cabbage.stats.bo.DepositStatsBO;
import vc.thinker.cabbage.stats.mapper.DepositStatsMapper;
import vc.thinker.cabbage.stats.model.DepositStats;
import vc.thinker.cabbage.stats.model.DepositStatsExample;
import vc.thinker.cabbage.stats.vo.StatsVO;

@Repository
public class DepositStatsDao {

	@Autowired
	private DepositStatsMapper mapper;


   /** generate code begin**/
	public List<DepositStatsBO> findAll(){
		DepositStatsExample example=new DepositStatsExample();
		return mapper.selectByExample(example);
	}
	List<DepositStatsBO> findAll(Iterable<java.lang.Long> ids){
		DepositStatsExample example=new DepositStatsExample();
		example.createCriteria().andIdIn(Lists.newArrayList(ids));
		return mapper.selectByExample(example);
	}
	
	public long count(){
		DepositStatsExample example=new DepositStatsExample();
		return mapper.countByExample(example);
	}

	public List<DepositStats> save(Iterable<DepositStats> entities){
		List<DepositStats> list=new ArrayList<DepositStats>();
		for (DepositStats DepositStats : entities) {
			list.add(save(DepositStats));
		}
		return list;
	}
	
	public DepositStats save(DepositStats record){
		if(record.getId() == null){
			mapper.insertSelective(record);
		}else{
			mapper.updateByPrimaryKeySelective(record);
		}
		return record;
	}
	

	public void update(DepositStats record) {
		mapper.updateByPrimaryKeySelective(record);
	}
	
	public DepositStatsBO findOne(java.lang.Long id){
		return mapper.selectByPrimaryKey(id);
	}

	public boolean exists(java.lang.Long id){
		if(id == null){
			return false;
		}
		DepositStatsExample example=new DepositStatsExample();
		example.createCriteria().andIdEqualTo(id);
		return mapper.countByExample(example) > 0;
	}
	
	 public void delete(java.lang.Long id){
		 mapper.deleteByPrimaryKey(id);
	 }
	 
	 public void remove(java.lang.Long id){
		 mapper.deleteByPrimaryKey(id);
	 }

	public void delete(DepositStats entity){
		 mapper.deleteByPrimaryKey(entity.getId());
	}

	public void delete(Iterable<DepositStats> entities){
		List<java.lang.Long> ids=Lists.newArrayList();
		for (DepositStats  entity: entities) {
			ids.add(entity.getId());
		}
		deleteByIds(ids);
	}
	
	public void deleteByIds(Iterable<java.lang.Long> ids){
		DepositStatsExample example=new DepositStatsExample();
		example.createCriteria().andIdIn(Lists.newArrayList(ids));
		 mapper.deleteByExample(example);
	}

	public void deleteAll(){
		DepositStatsExample example=new DepositStatsExample();
		mapper.deleteByExample(example);
	}
	/** generate code end**/
	public BigDecimal sumByDate(String date) {
		return mapper.sumByDate(date);
	}
	public List<DepositStatsBO> sumByDateGouupByCurrency(String date){
		return mapper.sumByDateGouupByCurrency(date);
	}
	public BigDecimal sumAvgByDate(String beginDate, String endDate) {
		return mapper.sumAvgByDate(beginDate,endDate);
	}
	public DepositStatsBO findbyCurrentDay(String dateMonth,String currency) {
		DepositStatsExample example=new DepositStatsExample();
		example.createCriteria().andCurrentDayEqualTo(dateMonth).andCurrencyEqualTo(currency);
		List<DepositStatsBO> list = mapper.selectByExample(example);
		return null != list && list.size()>0?list.get(0):null;
	}
	public List<CountStatsBO> statisticsCurrentDeposit(StatsVO vo) {
		return mapper.statisticsCurrentDeposit(vo);
	}
	public List<DepositStatsBO> sumAvgByDateGroupByCurrency(String beginDate, String endDate) {
		return mapper.sumAvgByDateGroupByCurrency(beginDate,endDate);
	}
}
