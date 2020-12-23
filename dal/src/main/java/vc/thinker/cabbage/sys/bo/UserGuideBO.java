package vc.thinker.cabbage.sys.bo;


import vc.thinker.cabbage.sys.model.UserGuide;
/**
 * 
 * BO 用于返回数据
 *
 */
public class UserGuideBO extends UserGuide{

	private String languageDesc;
	private String languageName;
	public String getLanguageDesc() {
		return languageDesc;
	}
	public void setLanguageDesc(String languageDesc) {
		this.languageDesc = languageDesc;
	}
	public String getLanguageName() {
		return languageName;
	}
	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}
	
}