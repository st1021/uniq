package vc.thinker.cabbage.stats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import vc.thinker.cabbage.se.OrderRebateService;
import vc.thinker.cabbage.se.OrderService;
import vc.thinker.cabbage.se.bo.OrderBO;
import vc.thinker.cabbage.stats.dao.OrderStatsDao;
import vc.thinker.cabbage.stats.model.OrderStats;



@Service
public class UpdateOrderStatsService {

    private final static Logger log = LoggerFactory.getLogger(UpdateOrderStatsService.class);

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderStatsDao orderStatsDao;
    
    @Autowired
    private OrderRebateService orderRebateService;

    @Value("${lbs.baidu.ak}")
    private String baiduAK;

    /**
     * 更新使用统计
     *
     * @param event
     */
    @TransactionalEventListener
    @Async
    public void updateTripStats(EndOrderEvent event) {
    	
    	OrderBO order = orderService.findOne(event.getOrderId());

        if (order == null) {
            log.error("trip  [{}] not find", event.getOrderId());
            return;
        }
        
       	OrderStats in_stats = new OrderStats();
       	in_stats.setUid(order.getUid());
       	in_stats.setStatsDate(order.getPayTime());
       	in_stats.setDuration(order.getRideTime());
       	in_stats.setOrderStatus(order.getStatus());
       	in_stats.setOrderId(order.getId());
    	in_stats.setTripAmount(order.getPrice());
       	in_stats.setPayType(order.getPayType());
       	in_stats.setCurrency(order.getCurrency());
       	if(org.apache.commons.lang3.StringUtils.isNotBlank(order.getClientSource())) {
       		in_stats.setClientType(order.getClientSource().split("/")[0].toLowerCase().replaceAll("ios", "iphone"));
       	}
       	in_stats.setActualConsume(order.getPayPrice());
       	orderStatsDao.save(in_stats);

       	//订单返润
       	orderRebateService.orderRebate(order);
    }
    
    /**
     * 结束使用事件
     */
    public static class EndOrderEvent {
        //行程id
        private Long orderId;

		public Long getOrderId() {
			return orderId;
		}

		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}

    }


}
