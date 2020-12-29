package vc.thinker.cabbage.beans;

import java.util.List;

import vc.thinker.cabbage.se.model.PortableBattery;

/**
 * 
 * @description MyWebConfig.java
 *
 * @author ZhangGaoXiang
 * @date 2020年12月28日 上午10:37:40
 */

public class ShareCmd {

	private String boxId;
	private String cmd;
	private byte vsn;
	private byte[] token;
	
	private String slot;
	private Boolean result;
	private String pbId;
	
	private Integer remainNum;
	List<PortableBattery> pbList;
	
	public String getBoxId() {
		return boxId;
	}
	public void setBoxId(String boxId) {
		this.boxId = boxId;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public byte getVsn() {
		return vsn;
	}
	public void setVsn(byte vsn) {
		this.vsn = vsn;
	}
	public byte[] getToken() {
		return token;
	}
	public void setToken(byte[] token) {
		this.token = token;
	}
	public String getSlot() {
		return slot;
	}
	public void setSlot(String slot) {
		this.slot = slot;
	}
	public Boolean getResult() {
		return result;
	}
	public void setResult(Boolean result) {
		this.result = result;
	}
	public String getPbId() {
		return pbId;
	}
	public void setPbId(String pbId) {
		this.pbId = pbId;
	}
	public Integer getRemainNum() {
		return remainNum;
	}
	public void setRemainNum(Integer remainNum) {
		this.remainNum = remainNum;
	}
	public List<PortableBattery> getPbList() {
		return pbList;
	}
	public void setPbList(List<PortableBattery> pbList) {
		this.pbList = pbList;
	}
	
}
