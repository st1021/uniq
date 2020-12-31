package vc.thinker.cabbage.cmd;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import vc.thinker.cabbage.beans.ShareCmd;
import vc.thinker.cabbage.tcp.SessionStoreManager;
import vc.thinker.cabbage.util.HexUtils;

/**
 * 
 * @description
 *
 * @author ZhangGaoXiang
 * @date 2020年12月23日 上午11:34:46
 */
public class ShareTcpCommonPush implements TcpCommandPush {

	public static final Logger LOGGER = LoggerFactory.getLogger(ShareTcpCommonPush.class);

	@Autowired
	private SessionStoreManager sessionStoreManager;

	public static final byte vsn = 0x01;
	
	// 设备未做校验，默认 0x00
	public  static final byte checkSum = 0x00;

	public static final byte[] token = new byte[] { 0x11, 0x22, 0x33, 0x44 };

	@Override
	public void sendQuery(String cabinetId, String channle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendLock(String cabinetId, String channle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendUnlock(String cabinetId, String channle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendSysOut(String cabinetId, String channle) {
		// TODO Auto-generated method stub

	}

	
	/**
	 * 登陆响应
	 * 
	 * @param session
	 * @param cmd
	 */
	public void sendLoginResp(IoSession session, ShareCmd cmd) {
		// 组装返回报文
		byte[] resp = new byte[10];
		resp[0] = 0x00;
		resp[1] = 0x08;
		resp[2] = 0x60;
		resp[3] = vsn;
		resp[4] = 0x01;
		System.arraycopy(cmd.getToken(), 0, resp, 5, 4);
		resp[9] = 0x01;
		LOGGER.info("login resp:{}", HexUtils.toHexString(resp));
		session.write(resp);
	}

	/**
	 * 心跳响应
	 * 
	 * @param session
	 * @param msg
	 */
	public void sendHeartResp(IoSession session, byte[] msg) {
		LOGGER.info("login resp:{}", HexUtils.toHexString(msg));
		session.write(msg);
	}
	
	
	/**
	 * 设置服务器地址
	 */
	@Override
	public void sendSetServer(String boxId, String ip, Integer port, Integer interval) {
		IoSession session = sessionStoreManager.getSession(boxId);
		if (null == session || !session.isActive()) {
			LOGGER.info("boxId:{} offline. ", boxId);
			return;
		}
		byte[] resp = new byte[20];
		resp[0] = 0x00;
		resp[1] = 0x12;
		resp[2] = 0x63;
		resp[3] = vsn;
		resp[4] = checkSum;
		System.arraycopy(token, 0, resp, 5, 4);
		resp[9] = 0x00;
		resp[10] = 0x04;
		String[] split = ip.split("\\.");
		resp[11] = int2byte(Integer.parseInt(split[0]))[1];
		resp[12] = int2byte(Integer.parseInt(split[1]))[1];
		resp[13] = int2byte(Integer.parseInt(split[2]))[1];
		resp[14] = int2byte(Integer.parseInt(split[3]))[1];
		resp[15] = 0x00;
		resp[16] = 0x02;
		resp[17] = int2byte(port)[0];
		resp[18] = int2byte(port)[1];
		resp[19] = int2byte(interval)[1];

		session.write(resp);
	}

	@Override
	public void sendSync(String boxId) {
		IoSession session = sessionStoreManager.getSession(boxId);
		if (null == session || !session.isActive()) {
			LOGGER.info("boxId:{} offline. ", boxId);
			return;
		}
		byte[] resp = new byte[9];
		resp[0] = 0x00;
		resp[1] = 0x08;
		resp[2] = 0x64;
		resp[3] = vsn;
		resp[4] = checkSum;
		System.arraycopy(token, 0, resp, 5, 4);
		session.write(resp);
	}

	@Override
	public void sendOut(String boxId, String orderCode, String cable) {
		IoSession session = sessionStoreManager.getSession(boxId);
		if (null == session || !session.isActive()) {
			LOGGER.info("boxId:{} offline. ", boxId);
			return;
		}
		// 组装请求
		byte[] resp = new byte[10];
		resp[0] = 0x00;
		resp[1] = 0x08;
		resp[2] = 0x65;
		resp[3] = vsn;
		resp[4] = checkSum;
		System.arraycopy(token, 0, resp, 5, 4);
		resp[9] = (byte) (Integer.parseInt(cable) & 0xFF);
		session.write(resp);
	}
	
	
	@Override
	public void sendQueryServer(String boxId) {
		IoSession session = sessionStoreManager.getSession(boxId);
		if (null == session || !session.isActive()) {
			LOGGER.info("boxId:{} offline. ", boxId);
			return;
		}
		// 组装请求
		byte[] resp = new byte[9];
		resp[0] = 0x00;
		resp[1] = 0x07;
		resp[2] = 0x6A;
		resp[3] = vsn;
		resp[4] = checkSum;
		System.arraycopy(token, 0, resp, 5, 4);
		LOGGER.info("boxId: {} ,send get server:{}", HexUtils.toHexString(resp));
		session.write(resp);
	}
	
	public void sendReturnBackResp(IoSession session, String slot) {
		byte[] resp = new byte[11];
		resp[0] = 0x00;
		resp[1] = 0x08;
		resp[2] = 0x66;
		resp[3] = vsn;
		byte bSlot = (byte) (0xff & Integer.parseInt(slot));
		resp[4] = bSlot;
		System.arraycopy(token, 0, resp, 5, 4);
		resp[9] = bSlot;
		resp[10] = 0x01;
		LOGGER.info("returnBack resp :{}", HexUtils.toHexString(resp));
		session.write(resp);
	}

	/**
	 * 获取CheckSum
	 * 
	 * @param str
	 * @return
	 */
	public static Integer getCheckSumByHexStr(String str) {
		int sum1 = 0;
		for (int i = 0; i < str.length(); i += 2) {
			String substring = str.substring(i, i + 2);
			int parseInt = Integer.parseInt(substring, 16);
			if (i == 0) {
				sum1 = parseInt;
			} else {
				sum1 = sum1 ^ parseInt;
			}
		}
		return sum1;
	}

	/**
	 * 整数转16进制byte
	 * 
	 * @param num
	 * @return
	 */
	public static byte[] int2byte(Integer num) {
		byte[] result = new byte[2];
		String hexStr = repairerZero(Integer.toHexString(num));
		if (hexStr.length() == 2) {
			result[0] = 0x00;
			result[1] = (byte) Integer.parseInt(hexStr, 16);
			return result;
		}
		result[0] = (byte) Integer.parseInt(hexStr.substring(0, 2), 16);
		result[1] = (byte) Integer.parseInt(hexStr.substring(2, 4), 16);
		return result;
	}

	/**
	 * 高位补0
	 * 
	 * @param str
	 * @return
	 */
	public static String repairerZero(String str) {
		return str.length() == 2 || str.length() == 4 ? str : new StringBuffer().append("0").append(str).toString();
	}

	public static void main(String[] args) {

//		String hexString = Integer.toHexString(255);
//		System.out.println(hexString);
//
//		if(hexString.length() == 1 || hexString.length() == 3) {
//			hexString = "0"+hexString;
//		}
//		
//		int j = 0;
//		byte[] result = new byte[hexString.length() / 2];
//		for (int i = 0; i < hexString.length(); i += 2) {
//			result[j++] = (byte) Integer.parseInt(hexString.substring(i, i + 2), 16);
//		}
//		System.out.println(result.length);
//		System.out.println(Arrays.toString(result));
//		System.out.println(HexUtils.toHexString(result));

//		String str = "556677880233001148594341303932303132303030303031";
//		Integer checkSumByHexStr = getCheckSumByHexStr(str);
//		System.out.println(checkSumByHexStr);
//
//		byte[] commonData = new byte[] { 0x00, 0x08, 0x60, 0x01 };
//		// 返回报文
//		byte[] resp = new byte[10];
//		// 拷贝
//		System.arraycopy(commonData, 0, resp, 0, 4);
//		// checkSum
//		resp[4] = 0x01;
//		// token
//		byte[] token = new byte[] { 0x11, 0x22, 0x33, 0x44 };
//		System.arraycopy(token, 0, resp, 5, 4);
//		// 结果
//		resp[9] = 0x01;
//
//		System.out.println(Arrays.toString(resp));
//		System.out.println(HexUtils.toHexString(resp));
//		
//		String a = "12";
//		byte ab = (byte) (Integer.parseInt(a) & 0xFF);
//		System.out.println("ab:"+ ab);

//		byte[] resp = new byte[1];
//		resp[0] = 0x00;
//		resp[1] = 0x08;
//		resp[2] = 0x65;
//		resp[3] = vsn;
//		resp[4] = 0x00;
//		System.arraycopy(token, 0, resp, 5, 4);
//		resp[0] = (byte) (Integer.parseInt("10001") & 0xFFFF);
//		System.out.println(HexUtils.toHexString(resp));

	}

}
