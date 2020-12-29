package vc.thinker.cabbage.actors;


import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import akka.actor.UntypedActor;
import vc.thinker.cabbage.CobwebApplication;
import vc.thinker.cabbage.beans.ShareCmd;
import vc.thinker.cabbage.cmd.ShareCmdConstatns;
import vc.thinker.cabbage.cmd.ShareTcpCommonPush;
import vc.thinker.cabbage.se.CabinetStatusService;
import vc.thinker.cabbage.se.OrderService;
import vc.thinker.cabbage.se.PortableBatteryConstatns;
import vc.thinker.cabbage.se.exception.CabinetNotFindException;
import vc.thinker.cabbage.se.exception.CabinetStatusNotFindException;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(ShareTcpActors.class);

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
		cabinetStatusService.syncBattery(cmd.getBoxId(), cmd.getRemainNum(), cmd.getPbList());
	}

	/**
	 * 归还确认
	 * 
	 * @param cmd
	 */
	private void returnBack(ShareCmd cmd) {
		// 结束订单
		orderService.cabinetEndOrder(cmd.getBoxId(), cmd.getPbId(), cmd.getSlot());
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
		LOGGER.info("message from {}, {}", session.getRemoteAddress(), orgmsg);
		
		int bodyLength = Integer.parseInt(orgmsg.substring(0,4), 16);
		
		ShareCmd shareCmd = new ShareCmd();
		shareCmd.setBoxId(orgmsg.substring(34, 56));
		shareCmd.setToken(token);
		shareCmd.setCmd(orgmsg.substring(4, 6));
		shareCmd.setVsn(msg[4]);
		switch (shareCmd.getCmd()) {
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
				shareCmd.setPbList(pbList);
			}
			break;
		default:
			break;
		}
		return shareCmd;
	}
	
	private Integer getElectricityByLevel(String level) {
		switch (level) {
		case "0":
			return 20;
		case "1":
			return 40;
		case "2":
			return 60;
		case "3":
			return 80;
		case "4":
			return 100;
		default:
			return 0;
		}
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
			// 入库
			cabinetStatusService.login(cmd.getBoxId(), CobwebApplication.serviceCode);
		} catch (CabinetNotFindException e) {
			LOGGER.error("login handler error:{}", e.getMessage());
		} finally {
			// 响应设备
			shareTcpCommonPush.sendLoginResp(session, cmd);
		}
	}
	
}
