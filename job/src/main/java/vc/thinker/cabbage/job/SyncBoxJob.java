package vc.thinker.cabbage.job;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vc.thinker.cabbage.se.CabinetRemoteHandle;
import vc.thinker.cabbage.se.CabinetStatusService;
import vc.thinker.cabbage.se.model.CabinetStatus;

/**
 * 
 * @description share 同步充电柜信息
 *
 * @author ZhangGaoXiang
 * @date 2020年12月28日 上午11:46:38
 */
@Component
public class SyncBoxJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(SyncBoxJob.class);
	
	@Autowired
	private CabinetStatusService cabinetStatusService;
	
	@Autowired
	private CabinetRemoteHandle cabinetRemoteHandle;
	
	@Scheduled(cron="0 0/1 * * * ? ")
	public void execute() {
		LOGGER.info("开始执行充电柜数据同步");
		List<CabinetStatus> onlines = cabinetStatusService.listOnline();
		onlines.forEach(o->{
			cabinetRemoteHandle.synchronizePb(o.getCabinetCode(),o.getServiceCode());
		});
	}
	
}
