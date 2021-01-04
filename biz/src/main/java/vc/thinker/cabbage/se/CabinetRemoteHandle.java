package vc.thinker.cabbage.se;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.sinco.messager.MessageDelegate;
import com.sinco.messager.MessageHandler;

import vc.thinker.cabbage.se.dao.CabinetStatusDao;
import vc.thinker.cabbage.se.exception.CabinetStatusNotFindException;
import vc.thinker.cabbage.se.model.CabinetStatus;

/**
 * 车锁远程处理
 * @author james
 *
 */
public class CabinetRemoteHandle {
	
	private static final Logger log=LoggerFactory.getLogger(CabinetRemoteHandle.class);

	private MessageHandler messageHandler;
	
	@Autowired
	private CabinetStatusDao cabinetStatusDao;
	
	/**
	 * 出电池
	 * @param umMachineCode
	 */
	
	public void out(String boxId,String orderCode,String cable){
//		CabinetStatus cs = cabinetStatusDao.findBySysCode(sysCode);
//		if(cs == null){
//			throw new CabinetStatusNotFindException("sysCode: " + sysCode + " not found.");
//		}
		CabinetNoticeContent lockNoticeContent=new CabinetNoticeContent();
		lockNoticeContent.setNoticeType(NoticeType.out);
		lockNoticeContent.setCabinetId(boxId);
		lockNoticeContent.setOrderCode(orderCode);
		lockNoticeContent.setCable(cable);
		messageHandler.sendMessage(orderCode,JSON.toJSONString(lockNoticeContent));
	}
	
	
	
	/**
	 * 系统借出
	 * @param umMachineCode
	 */
	public void sysOut(String sysCode, Integer slot){
		CabinetStatus cs = cabinetStatusDao.findBySysCode(sysCode);
		if(cs == null){
			throw new CabinetStatusNotFindException("boxId: " + sysCode + " not found.");
		}
		CabinetNoticeContent lockNoticeContent=new CabinetNoticeContent();
		lockNoticeContent.setNoticeType(NoticeType.sys_out);
		lockNoticeContent.setCabinetId(cs.getCabinetCode());
		lockNoticeContent.setChannel(slot);
		messageHandler.sendMessage(cs.getServiceCode(),JSON.toJSONString(lockNoticeContent));
	}
	
	/**
	 * 同步
	 * @param umMachineCode
	 */
	public void sync(String sysCode){
		CabinetStatus cs = cabinetStatusDao.findBySysCode(sysCode);
		if(cs == null){
			throw new CabinetStatusNotFindException("boxId: " + sysCode + " not found.");
		}
		CabinetNoticeContent lockNoticeContent=new CabinetNoticeContent();
		lockNoticeContent.setNoticeType(NoticeType.sync);
		lockNoticeContent.setCabinetId(cs.getCabinetCode());
		messageHandler.sendMessage(cs.getServiceCode(),JSON.toJSONString(lockNoticeContent));
	}
	
	public void synchronizePb(String cabinetCode, String serviceCode) {
		CabinetNoticeContent lockNoticeContent = new CabinetNoticeContent();
		lockNoticeContent.setNoticeType(NoticeType.sync);
		lockNoticeContent.setCabinetId(cabinetCode);
		messageHandler.sendMessage(serviceCode, JSON.toJSONString(lockNoticeContent));
	}

	/**
	 * 查询
	 * @param sysCode
	 * @param channel
	 */
	public void query(String sysCode,Integer channel){
		CabinetStatus cs=cabinetStatusDao.findBySysCode(sysCode);
		if(cs == null){
			throw new CabinetStatusNotFindException("sysCode: " + sysCode + " not found.");
		}
		CabinetNoticeContent lockNoticeContent=new CabinetNoticeContent();
		lockNoticeContent.setNoticeType(NoticeType.query);
		lockNoticeContent.setCabinetId(String.valueOf(cs.getCid()));
		lockNoticeContent.setChannel(channel);
		messageHandler.sendMessage(cs.getServiceCode(),JSON.toJSONString(lockNoticeContent));
	}
	
	/**
	 * 锁住
	 * @param sysCode
	 * @param channel
	 */
	public void lock(String sysCode,Integer channel){
		CabinetStatus cs=cabinetStatusDao.findBySysCode(sysCode);
		if(cs == null){
			throw new CabinetStatusNotFindException();
		}
		CabinetNoticeContent lockNoticeContent=new CabinetNoticeContent();
		lockNoticeContent.setNoticeType(NoticeType.lock);
		lockNoticeContent.setCabinetId(String.valueOf(cs.getCid()));
		lockNoticeContent.setChannel(channel);
		messageHandler.sendMessage(cs.getServiceCode(),JSON.toJSONString(lockNoticeContent));
	}
	
	/**
	 * 解锁
	 * @param sysCode
	 * @param channel
	 */
	public void unlock(String sysCode,Integer channel){
		CabinetStatus cs=cabinetStatusDao.findBySysCode(sysCode);
		if(cs == null){
			throw new CabinetStatusNotFindException();
		}
		CabinetNoticeContent lockNoticeContent=new CabinetNoticeContent();
		lockNoticeContent.setNoticeType(NoticeType.unlock);
		lockNoticeContent.setCabinetId(String.valueOf(cs.getCid()));
		lockNoticeContent.setChannel(channel);
		messageHandler.sendMessage(cs.getServiceCode(),JSON.toJSONString(lockNoticeContent));
	}
	
	/**
	 * 查询服务器地址
	 * 
	 * @param boxId
	 */
	public void getServerIp(String sysCode) {
		CabinetStatus cs = cabinetStatusDao.findBySysCode(sysCode);
		if (cs == null) {
			throw new CabinetStatusNotFindException("boxId: "+sysCode+" not found.");
		}
		CabinetNoticeContent lockNoticeContent = new CabinetNoticeContent();
		lockNoticeContent.setNoticeType(NoticeType.get_server);
		lockNoticeContent.setCabinetId(cs.getCabinetCode());
		messageHandler.sendMessage(cs.getServiceCode(), JSON.toJSONString(lockNoticeContent));
	}
	
	public void synServer(String sysCode, String ip, String port, Integer heartbeat) {
		CabinetStatus cs = cabinetStatusDao.findBySysCode(sysCode);
		if (cs == null) {
			throw new CabinetStatusNotFindException("sysCode: "+sysCode+" not found.");
		}
		
		CabinetNoticeContent lockNoticeContent = new CabinetNoticeContent();
		lockNoticeContent.setNoticeType(NoticeType.syn_server);
		lockNoticeContent.setCabinetId(cs.getCabinetCode());
		lockNoticeContent.setIp(ip);
		lockNoticeContent.setPort(port);
		lockNoticeContent.setHeartbeat(heartbeat);
		messageHandler.sendMessage(cs.getServiceCode(), JSON.toJSONString(lockNoticeContent));
	}

	/**
	 * 远程监听
	 * @param channl
	 * @param poolNumber
	 * @param delegate
	 */
	public void listener(String channl,int poolNumber,NoticeDelegate delegate){
    	messageHandler.listenerMessage(new MessageDelegate() {
			@Override
			public void handleMessage(String message, String channel) {
				log.info("listener receive message = {}",message);
				CabinetNoticeContent content=JSON.parseObject(message, CabinetNoticeContent.class);
				delegate.handle(content);
			}
		}, channl, poolNumber);
	}
	

	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}
	
	public interface NoticeDelegate {

		void handle(CabinetNoticeContent content);
	}
	
	public static enum NoticeType{
		out,upgrade,sync,query,lock,unlock,sys_out, syn_server, get_server
	}
	
	public static class CabinetNoticeContent{
		private NoticeType noticeType;
		
		private Integer channel;
		
		private String cabinetId;
		
		private String orderCode;
		
		private String cable;
		
		//命令时间
		private Date noticeTime;
		
		private Integer second;
		
		private String ip;
		
		private String port;
		
		private Integer heartbeat;
		
		public String getCabinetId() {
			return cabinetId;
		}

		public void setCabinetId(String cabinetId) {
			this.cabinetId = cabinetId;
		}

		public Integer getChannel() {
			return channel;
		}

		public void setChannel(Integer channel) {
			this.channel = channel;
		}

		public NoticeType getNoticeType() {
			return noticeType;
		}

		public void setNoticeType(NoticeType noticeType) {
			this.noticeType = noticeType;
		}

		public String getOrderCode() {
			return orderCode;
		}

		public void setOrderCode(String orderCode) {
			this.orderCode = orderCode;
		}

		public String getCable() {
			return cable;
		}

		public void setCable(String cable) {
			this.cable = cable;
		}

		public Date getNoticeTime() {
			return noticeTime;
		}

		public void setNoticeTime(Date noticeTime) {
			this.noticeTime = noticeTime;
		}

		public Integer getSecond() {
			return second;
		}

		public void setSecond(Integer second) {
			this.second = second;
		}

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

		public Integer getHeartbeat() {
			return heartbeat;
		}

		public void setHeartbeat(Integer heartbeat) {
			this.heartbeat = heartbeat;
		}

	}

}
