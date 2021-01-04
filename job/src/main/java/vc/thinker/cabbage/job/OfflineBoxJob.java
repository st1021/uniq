package vc.thinker.cabbage.job;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vc.thinker.cabbage.se.CabinetStatusService;
import vc.thinker.cabbage.se.model.CabinetStatus;
import vc.thinker.cabbage.sys.dao.SysSettingDao;
import vc.thinker.cabbage.sys.model.SysSetting;
import vc.thinker.cabbage.util.DateTimeUtils;

/**
 * 
 * @description 标记掉线
 *
 * @author ZhangGaoXiang
 * @date 2020年12月28日 下午2:05:08
 */

@Component
public class OfflineBoxJob {	
	
	@Autowired
	private CabinetStatusService cabinetStatusService;
	
	@Autowired
	private SysSettingDao sysSettingDao;
	
	@Scheduled(cron="0 0/1 * * * ? ")
	public void execute() {
		List<CabinetStatus> onlines = cabinetStatusService.listOnline();
		if(!onlines.isEmpty()) {
			SysSetting setting = sysSettingDao.findOne();
			Date date = DateTimeUtils.getSomeMinuteBeforeDate(Long.parseLong(String.valueOf(setting.getIsOnlineTime())));
			onlines.parallelStream().forEach(o->{
				if(o.getLastHeartbeat().before(date)) {
					//offline
					cabinetStatusService.offline(o.getId());
				}
			});
		}
	}
	
}
