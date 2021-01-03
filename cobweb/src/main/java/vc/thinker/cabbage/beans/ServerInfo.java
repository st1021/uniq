package vc.thinker.cabbage.beans;
/**
 * 
 * @description MyWebConfig.java
 *
 * @author ZhangGaoXiang
 * @date 2021年1月3日 下午9:19:23
 */
public class ServerInfo {

	private String ip;
	private String port;
	private Integer hearbet;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public Integer getHearbet() {
		return hearbet;
	}
	public void setHearbet(Integer hearbet) {
		this.hearbet = hearbet;
	}
}
