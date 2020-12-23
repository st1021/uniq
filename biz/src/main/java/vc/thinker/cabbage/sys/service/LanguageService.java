package vc.thinker.cabbage.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vc.thinker.cabbage.sys.bo.LanguageBO;
import vc.thinker.cabbage.sys.dao.LanguageDao;

/**
 *
 * @author ZhangGaoXiang
 * @time Jan 7, 20205:43:04 PM
 */
@Service
@Transactional
public class LanguageService {

	@Autowired
	private LanguageDao languageDao;

	public List<LanguageBO> findAll() {
		return languageDao.findAll();
	}
	
	
	
}
