package vc.thinker.cabbage.actors;


import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import akka.actor.UntypedActor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import vc.thinker.cabbage.CobwebApplication;
import vc.thinker.cabbage.beans.ServerInfo;
import vc.thinker.cabbage.beans.ShareCmd;
import vc.thinker.cabbage.cmd.ShareCmdConstatns;
import vc.thinker.cabbage.cmd.ShareTcpCommonPush;
import vc.thinker.cabbage.se.CabinetStatusService;
import vc.thinker.cabbage.se.OrderService;
import vc.thinker.cabbage.se.PortableBatteryConstatns;
import vc.thinker.cabbage.se.exception.CabinetNotFindException;
import vc.thinker.cabbage.se.exception.CabinetStatusNotFindException;
import vc.thinker.cabbage.se.exception.OrderNotFindException;
import vc.thinker.cabbage.se.model.PortableBattery;
import vc.thinker.cabbage.tcp.SessionStoreManager;
import vc.thinker.cabbage.util.HexUtils;

/**
 * 
 * @description MyWebConfig.java
 *
 * @author ZhangGaoXiang
 * @date 2020年12月23日 上午11:31:18
 */
@Component
@Scope("prototype")
public class ShareTcpActors extends UntypedActor {

	
	private static final String SHARE_TCP_MARK = "share_tcp_";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShareTcpActors.class);

	
	@Autowired
	private JedisPool jedisPool;
	
	public ShareTcpActors(IoSession session){
		this.session = session;
	}
	
	private IoSession session;

	@Autowired
	private CabinetStatusService cabinetStatusService;

	@Autowired
	private ShareTcpCommonPush shareTcpCommonPush;
	
	@Autowired
    private SessionStoreManager sessionStoreManager;
	
	@Autowired
	private OrderService orderService;
	
	@Override
	public void onReceive(Object message) throws Exception {
		byte[] msg = (byte[]) message;
		
		ShareCmd cmd = getCmd(msg);
		
		switch (cmd.getCmd()) {
		case ShareCmdConstatns.login:
			LOGGER.info("boxId:{}, login", cmd.getBoxId());
			login(cmd);
			break;
		case ShareCmdConstatns.heart:
			LOGGER.info("boxId:{}, heart", cmd.getBoxId());
			heart(cmd.getBoxId(), msg);
			break;
		case ShareCmdConstatns.sync_server:
			LOGGER.info("boxId:{}, sync_server", cmd.getBoxId());
			break;
		case ShareCmdConstatns.sync_battery:
			LOGGER.info("boxId:{}, sync_battery", cmd.getBoxId());
			syncBattery(cmd);
			break;
		case ShareCmdConstatns.rent_confirm:
			LOGGER.info("boxId:{}, rent_confirm", cmd.getBoxId());
			rentConfirm(cmd);
			break;
		case ShareCmdConstatns.return_back:
			LOGGER.info("boxId:{}, return_back", cmd.getBoxId());
			returnBack(cmd);
			break;
		case ShareCmdConstatns.get_server:
			LOGGER.info("boxId:{} 上报服务器地址：{},{},{}", cmd.getBoxId(), cmd.getServer().getIp(), cmd.getServer().getPort(), cmd.getServer().getHearbet());
			break;
		default:
			break;
		}
		//更新内存会话
		if(StringUtils.isNotBlank(cmd.getBoxId())){
			sessionStoreManager.setSession(cmd.getBoxId(), session);
		}
	}
	
	/**
	 * 同步电池
	 * 
	 * @param cmd
	 */
	private void syncBattery(ShareCmd cmd) {
		try {
			cabinetStatusService.syncBattery(cmd.getBoxId(), cmd.getRemainNum(), cmd.getPbList());
		} catch (CabinetNotFindException e) {
			LOGGER.info("boxId:" + getBoxId() + "not found.");
		}
	}

	/**
	 * 归还确认
	 * 
	 * @param cmd
	 */
	private void returnBack(ShareCmd cmd) {
		try {
			// 结束订单
			orderService.cabinetEndOrder(cmd.getBoxId(), cmd.getPbId(), cmd.getSlot());
		} catch (CabinetNotFindException e) {
			LOGGER.info("returnBack error:{}", e.getMessage());
		} catch (OrderNotFindException e) {
			LOGGER.info("returnBack error:{}", e.getMessage());
		} finally {
			shareTcpCommonPush.sendReturnBackResp(session, cmd.getSlot());
		}
	}

	/**
	 * 借出确认
	 * 
	 * @param cmd
	 */
	private void rentConfirm(ShareCmd cmd) {
		LOGGER.info("rentConfirm, result;{}, slot:{}, pbId:{}", cmd.getResult(), cmd.getSlot(), cmd.getPbId());
		if (cmd.getResult()) {
			// 更新充电宝
			PortableBattery pb = new PortableBattery();
			pb.setPortableBatteryCode(cmd.getPbId());
			pb.setLocationType(PortableBatteryConstatns.LOCATION_TYPE_IN_USE);
			cabinetStatusService.updateBattery(pb);
			// 开启订单
			orderService.beginOrder(cmd.getBoxId(), cmd.getSlot(), cmd.getPbId());
		}
	}
	

	private ShareCmd getCmd(byte[] msg) {
		// 获取token
		byte[] token = new byte[4];
		System.arraycopy(msg, 5, token, 0, 4);
		// 转成十六进制字符串
		String orgmsg = HexUtils.toHexString(msg);
//		LOGGER.info("message from {}, {}", session.getRemoteAddress(), orgmsg);
		
		ShareCmd shareCmd = new ShareCmd();
		
		shareCmd.setToken(token);
		shareCmd.setCmd(orgmsg.substring(4, 6));
		shareCmd.setVsn(msg[4]);
		switch (shareCmd.getCmd()) {
		case ShareCmdConstatns.login:
			shareCmd.setBoxId(hex2Ascii(orgmsg.substring(34, 66)));
			break;
		case ShareCmdConstatns.rent_confirm:
			shareCmd.setSlot(orgmsg.substring(18, 20));
			shareCmd.setResult(orgmsg.substring(20, 22).equals("01"));
			shareCmd.setPbId(orgmsg.substring(22));
			break;
		case ShareCmdConstatns.return_back:
			shareCmd.setSlot(orgmsg.substring(18, 20));
			shareCmd.setPbId(orgmsg.substring(20));
			break;
		case ShareCmdConstatns.sync_battery:
			int parseInt = Integer.parseInt(orgmsg.substring(18, 20), 16);
			shareCmd.setRemainNum(parseInt);
			if(shareCmd.getRemainNum()>0) {
				orgmsg = orgmsg.substring(20);
				List<PortableBattery> pbList=Lists.newArrayList();
				for(int i=0;i<parseInt;i++) {
					String substring = orgmsg.substring(i*20, i*20+20);
					String slot = substring.substring(0, 2);
					String pbId = substring.substring(2, 18);
					String level = substring.substring(18);
					PortableBattery pb = new PortableBattery();
					pb.setCabinetChannel(Integer.parseInt(slot));
					pb.setPortableBatteryCode(pbId);
					pb.setVoltage(Integer.parseInt(level));
					pb.setElectricity(getElectricityByLevel(level));
					pb.setCreateTime(new Date());
					pbList.add(pb);
				}
				LOGGER.info("pbList:{}", JSON.toJSONString(pbList));
				shareCmd.setPbList(pbList);
			}
			break;
		case ShareCmdConstatns.get_server:
			shareCmd.setServer(getServerInfo(orgmsg));
			break;
		default:
			break;
		}
		if(StringUtils.isBlank(shareCmd.getBoxId())) {
			shareCmd.setBoxId(getBoxId());
		}
		return shareCmd;
	}
	
	public static void main(String[] args) {
//		String orgmsg = "001f6a013f11223344000e3132302e32362e3234312e393800000537303032001e";
//		ServerInfo serverInfo = getServerInfo(orgmsg);
//		System.out.println(serverInfo.getIp());
//		System.out.println(serverInfo.getPort());
//		System.out.println(serverInfo.getHearbet());
		
		String string = "485943420b030071";
		String hex2Ascii = hex2Ascii(string);
		System.out.println("1111"+ hex2Ascii+"22222");
	}

	private static ServerInfo getServerInfo(String orgmsg) {

		ServerInfo serverInfo = new ServerInfo();

		int ipByte = Integer.parseInt(orgmsg.substring(18, 22), 16);
		int ipEnd = 22 + ipByte * 2;

		String ipHexStr = orgmsg.substring(22, ipEnd);

		String ip = hex2Ascii(ipHexStr.substring(0, ipHexStr.length() - 2));
		serverInfo.setIp(ip);

		int portByte = Integer.parseInt(orgmsg.substring(ipEnd, ipEnd + 4), 16);
		String portHexStr = orgmsg.substring(ipEnd + 4, ipEnd + 4 + portByte * 2);
		String port = hex2Ascii(portHexStr.substring(0, portHexStr.length() - 2));
		serverInfo.setPort(port);

		int hearbet = Integer.parseInt(orgmsg.substring(orgmsg.length() - 2, orgmsg.length()), 16);
		serverInfo.setHearbet(hearbet);

		return serverInfo;
	}
	
	private Integer getElectricityByLevel(String level) {
		switch (level) {
		case "00":
			return 20;
		case "01":
			return 40;
		case "02":
			return 60;
		case "03":
			return 80;
		case "04":
			return 100;
		default:
			return 0;
		}
	}
	
	/**
	 * str to ASCII
	 * 
	 * @param str
	 * @return
	 */
	public static String hex2Ascii(String str) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < str.length(); i += 2) {
			String substring = str.substring(i, i + 2);
			int parseInt2 = Integer.parseInt(substring, 16);
			sBuffer.append((char) parseInt2);
		}
		return sBuffer.toString();
	}

	/**
	 * 心跳
	 * 
	 * @param boxId
	 */
	private void heart(String boxId, byte[] respBype) {
		try {
			// 更新充电贵的心跳时间
			cabinetStatusService.heartbeat(boxId);
		} catch (CabinetStatusNotFindException e) {
			LOGGER.info("heart error:{}", e.getMessage());
		}finally {
			// 相应充电柜
			shareTcpCommonPush.sendHeartResp(session, respBype);
		}
	}

	/**
	 * 登陆
	 * 
	 * @param boxId
	 */
	public void login(ShareCmd cmd) {
		try {
			setBoxId(cmd.getBoxId());
			// 入库
			cabinetStatusService.login(cmd.getBoxId(), CobwebApplication.serviceCode);
		} catch (CabinetNotFindException e) {
			LOGGER.error("login handler error:{}", e.getMessage());
		} finally {
			// 响应设备
			shareTcpCommonPush.sendLoginResp(session, cmd);
		}
	}
	
	
	public String getBoxId() {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.get(getRedisKey(session.getId()));
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}
	public void setBoxId(String boxId) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.set(getRedisKey(session.getId()), boxId);
		} finally {
			if (jedis != null)
				jedis.close();
		}
	}
	
	public String getRedisKey(Long sessionId) {
		StringBuffer sb = new StringBuffer();
		sb.append(SHARE_TCP_MARK);
		sb.append(sessionId);
		return sb.toString();
	}
	
}
