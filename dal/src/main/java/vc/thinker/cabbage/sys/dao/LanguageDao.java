package vc.thinker.cabbage.sys.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.common.collect.Lists;

import vc.thinker.cabbage.sys.mapper.LanguageMapper;
import vc.thinker.cabbage.sys.model.Language;
import vc.thinker.cabbage.sys.bo.LanguageBO;
import vc.thinker.cabbage.sys.model.LanguageExample;

@Repository
public class LanguageDao {

	@Autowired
	private LanguageMapper mapper;


   /** generate code begin**/
	public List<LanguageBO> findAll(){
		LanguageExample example=new LanguageExample();
		example.setOrderByClause("sort");
		return mapper.selectByExample(example);
	}
	List<LanguageBO> findAll(Iterable<java.lang.Long> ids){
		LanguageExample example=new LanguageExample();
		example.createCriteria().andIdIn(Lists.newArrayList(ids));
		return mapper.selectByExample(example);
	}
	
	public long count(){
		LanguageExample example=new LanguageExample();
		return mapper.countByExample(example);
	}

	public List<Language> save(Iterable<Language> entities){
		List<Language> list=new ArrayList<Language>();
		for (Language Language : entities) {
			list.add(save(Language));
		}
		return list;
	}
	
	public Language save(Language record){
		if(record.getId() == null){
			mapper.insertSelective(record);
		}else{
			mapper.updateByPrimaryKeySelective(record);
		}
		return record;
	}
	

	public void update(Language record) {
		mapper.updateByPrimaryKeySelective(record);
	}
	
	public LanguageBO findOne(java.lang.Long id){
		return mapper.selectByPrimaryKey(id);
	}

	public boolean exists(java.lang.Long id){
		if(id == null){
			return false;
		}
		LanguageExample example=new LanguageExample();
		example.createCriteria().andIdEqualTo(id);
		return mapper.countByExample(example) > 0;
	}
	
	 public void delete(java.lang.Long id){
		 mapper.deleteByPrimaryKey(id);
	 }
	 
	 public void remove(java.lang.Long id){
		 mapper.deleteByPrimaryKey(id);
	 }

	public void delete(Language entity){
		 mapper.deleteByPrimaryKey(entity.getId());
	}

	public void delete(Iterable<Language> entities){
		List<java.lang.Long> ids=Lists.newArrayList();
		for (Language  entity: entities) {
			ids.add(entity.getId());
		}
		deleteByIds(ids);
	}
	
	public void deleteByIds(Iterable<java.lang.Long> ids){
		LanguageExample example=new LanguageExample();
		example.createCriteria().andIdIn(Lists.newArrayList(ids));
		 mapper.deleteByExample(example);
	}

	public void deleteAll(){
		LanguageExample example=new LanguageExample();
		mapper.deleteByExample(example);
	}
	/** generate code end**/
}
