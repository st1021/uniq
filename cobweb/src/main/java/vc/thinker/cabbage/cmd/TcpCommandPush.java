package vc.thinker.cabbage.cmd;

public interface TcpCommandPush {
	
	
	/**
	 * 出电池
	 * @param stationid
	 * @param orderCode
	 * @param cable
	 */
	public void sendOut(String cabinetId,String orderCode,String cable);
	
	/**
	 * 同步
	 * @param stationid
	 * @param orderCode
	 * @param cable
	 */
	public void sendSync(String cabinetId);
	
	/**	
	 * 查询消息
	 * @param cabinetId
	 * @param channle
	 */
	public void sendQuery(String cabinetId,String channle);
	/**	
	 * 锁住槽位
	 * @param cabinetId
	 * @param channle
	 */
	public void sendLock(String cabinetId,String channle);
	/**	
	 * 解锁槽位
	 * @param cabinetId
	 * @param channle
	 */
	public void sendUnlock(String cabinetId,String channle);
	
	/**	
	 * 系统行为弹出
	 * @param cabinetId
	 * @param channle
	 */
	public void sendSysOut(String cabinetId,String channle);
	
	/**
	 * 设置服务器地址
	 * 
	 * @param cabinetid
	 */
	void sendSetServer(String cabinetId, String id, Integer port, Integer interval);
	
	/**
	 * 查询服务器地址
	 * 
	 * @param cabientId
	 */
	void sendQueryServer(String cabientId);

	/**
	 * 同步服务器信息
	 * 
	 * @param cabinetId
	 * @param ip
	 * @param port
	 * @param heartbeat
	 */
	public void synServer(String cabinetId, String ip, String port, Integer heartbeat);
	
}