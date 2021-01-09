package vc.thinker.cabbage.se;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vc.thinker.biz.exception.ServiceException;
import vc.thinker.cabbage.money.exception.ExchangeRateNotFindException;
import vc.thinker.cabbage.money.service.MoneyService;
import vc.thinker.cabbage.se.bo.CabinetBO;
import vc.thinker.cabbage.se.bo.OrderBO;
import vc.thinker.cabbage.se.dao.CabinetDao;
import vc.thinker.cabbage.se.dao.OrderRebateDao;
import vc.thinker.cabbage.se.dao.UserRebateLogDao;
import vc.thinker.cabbage.se.model.Order;
import vc.thinker.cabbage.se.model.OrderRebate;
import vc.thinker.cabbage.se.model.UserRebateLog;
import vc.thinker.cabbage.sys.bo.ExchangeRateBO;
import vc.thinker.cabbage.sys.bo.PlatformRevenueBO;
import vc.thinker.cabbage.sys.dao.ExchangeRateDao;
import vc.thinker.cabbage.sys.dao.PlatformRevenueDao;
import vc.thinker.cabbage.user.CommonConstants;
import vc.thinker.cabbage.user.PlatformRevenueConstants;
import vc.thinker.cabbage.user.UserMoneyConstants;
import vc.thinker.cabbage.user.bo.AgentBO;
import vc.thinker.cabbage.user.bo.RefereeBO;
import vc.thinker.cabbage.user.bo.SellerBO;
import vc.thinker.cabbage.user.bo.UserMoneyBO;
import vc.thinker.cabbage.user.bo.UserMoneyLogBO;
import vc.thinker.cabbage.user.dao.AgentDao;
import vc.thinker.cabbage.user.dao.RefereeDao;
import vc.thinker.cabbage.user.dao.SellerDao;
import vc.thinker.cabbage.user.dao.UserMoneyDao;
import vc.thinker.cabbage.user.dao.UserMoneyLogDao;
import vc.thinker.cabbage.user.dao.UserRebateMoneyLogDao;
import vc.thinker.cabbage.user.model.UserMoney;
import vc.thinker.cabbage.user.model.UserRebateMoneyLog;

@Service
@Transactional
public class OrderRebateService {

	private static final Logger log = LoggerFactory.getLogger(OrderRebateService.class);

	@Autowired
	private OrderRebateDao orderRebateDao;

	@Autowired
	private SellerDao sellerDao;

	@Autowired
	private RefereeDao refereeDao;

	@Autowired
	private UserRebateLogDao userRebateLogDao;

	@Autowired
	private CabinetDao cabinetDao;

	@Autowired
	private AgentDao agentDao;
 
	@Autowired
	private MoneyService moneyService;

	@Autowired
	private ExchangeRateDao exchangeRateDao;

	@Autowired
	private UserRebateMoneyLogDao userRebateMoneyLogDao;

	@Autowired
	private UserMoneyDao userMoneyDao;

	@Autowired
	private UserMoneyLogDao userMoneyLogDao;

	@Autowired
	private PlatformRevenueDao platformRevenueDao;

	/**
	 * 创建订单反润
	 * 
	 * @param umOrder
	 * @return
	 */
	public OrderRebate createRebate(Order order) {
		OrderRebate orderRebate = new OrderRebate();
//		SysSetting setting=settingDao.findOne();
		orderRebate.setOrderId(order.getId());

		SellerBO seller = sellerDao.findOne(order.getBorrowSellerId());
		//
		orderRebate.setBorrowSellerModel(OrderRebateConstants.REBATE_MODEL_P);
		orderRebate.setBorrowSellerRebate(seller.getRebateRate());

		// 代理商返利
		CabinetBO cabinet = cabinetDao.findBySysCode(order.getBorrowSysCode());
		if (cabinet.getAgentId() != null) {
			AgentBO agent = agentDao.findOne(cabinet.getAgentId());
			orderRebate.setAgentModel(OrderRebateConstants.REBATE_MODEL_P);
			orderRebate.setAgentRabate(agent.getRebateRate());
		}

		// 推荐人返利
		if (seller.getRefereeUid() != null) {
			RefereeBO referee = refereeDao.findOne(seller.getRefereeUid());
			orderRebate.setRecommendedModel(OrderRebateConstants.REBATE_MODEL_P);
			orderRebate.setRecommendedRabate(referee.getRebateRate());
		}
		orderRebateDao.save(orderRebate);

		return orderRebate;
	}

	/**
	 * 完成反润
	 * 
	 * @param umOrder
	 */
	public void rebate(Order order) {
		// TODO 扣除返利金
		OrderRebate orderRebate = orderRebateDao.findByOrderId(order.getId());
		if (orderRebate == null) {
			throw new ServiceException("找不到订单的反润记录");
		}
		Date time = new Date();

		// 根据支付金额进行反润
		BigDecimal price = BigDecimal.ZERO;
		if (!order.getPayType().equals(CommonConstants.PAY_TYPE_VIP)) {// vip
			if (order.getPayPrice() == null || order.getPayPrice().doubleValue() <= 0) {
				log.info("订单[{}]支付金额小于0，无法进行反润", order.getOrderCode());
				return;
			}
			price = order.getPayPrice();
		} else {
			price = order.getPrice();
		}
		UserMoney userMoney = moneyService.isExistAndCreate(order.getUid());
		// 计算有没有超过最大返利值
		BigDecimal totalRate = BigDecimal.ZERO;
		if (orderRebate.getBorrowSellerModel() != null && orderRebate.getBorrowSellerRebate() != null
				&& orderRebate.getBorrowSellerRebate().doubleValue() > 0) {
			totalRate = totalRate.add(orderRebate.getBorrowSellerRebate());
		}
		if (orderRebate.getAgentModel() != null && orderRebate.getAgentRabate() != null
				&& orderRebate.getAgentRabate().doubleValue() > 0) {
			totalRate = totalRate.add(orderRebate.getAgentRabate());
		}
		if (orderRebate.getRecommendedModel() != null && orderRebate.getRecommendedRabate() != null
				&& orderRebate.getRecommendedRabate().doubleValue() > 0) {
			totalRate = totalRate.add(orderRebate.getRecommendedRabate());
		}

		BigDecimal oldRebatePrice = price.multiply(totalRate);
		if (oldRebatePrice.compareTo(BigDecimal.ZERO) <= 0) {
			log.info("没有设置任何反润比例，无法进行反润");
			return;
		}
		// 实际用于返利的金额 不能超过最大值
		BigDecimal rebatePrice = getRebateAmount(oldRebatePrice, order.getCurrency());
		BigDecimal pricerate = new BigDecimal(1);
		BigDecimal subRebatePrice = rebatePrice;
		// 币种转换
		if (!order.getCurrency().equals(userMoney.getCurrency())) {
			ExchangeRateBO exchangeRate = exchangeRateDao.findOneByCurrency(order.getCurrency(),
					userMoney.getCurrency());
			if (exchangeRate == null) {
				throw new ExchangeRateNotFindException(order.getCurrency(), userMoney.getCurrency());
			}
			pricerate = exchangeRate.getExchangeRate();
			subRebatePrice = rebatePrice.multiply(pricerate).setScale(2, BigDecimal.ROUND_DOWN);
		}

		log.info("需要返利：" + subRebatePrice);
		log.info("返利金剩余：" + userMoney.getRebateMoney());
		if (subRebatePrice.compareTo(userMoney.getRebateMoney()) < 0) {// 如果总的返利金不够，那么就一笔都不返

			// 计算比例
			BigDecimal priceRate = rebatePrice.divide(oldRebatePrice, 8, RoundingMode.HALF_UP);

			BigDecimal sellerR = BigDecimal.ZERO;
			BigDecimal agentR = BigDecimal.ZERO;

			// 商户返利
			if (orderRebate.getBorrowSellerModel() != null && orderRebate.getBorrowSellerRebate() != null
					&& orderRebate.getBorrowSellerRebate().doubleValue() > 0) {

				BigDecimal rebateAmount = new BigDecimal(0);
				rebateAmount = price.multiply(orderRebate.getBorrowSellerRebate()).multiply(priceRate);
				rebateAmount = rebateAmount.setScale(2, BigDecimal.ROUND_DOWN);
				sellerR = rebateAmount;
				// 进行币种转换
				BigDecimal rate = new BigDecimal(1);
				BigDecimal subAmount = rebateAmount;
				if (!order.getCurrency().equals(userMoney.getCurrency())) {
					ExchangeRateBO exchangeRate = exchangeRateDao.findOneByCurrency(order.getCurrency(),
							userMoney.getCurrency());
					if (exchangeRate == null) {
						throw new ExchangeRateNotFindException(order.getCurrency(), userMoney.getCurrency());
					}
					rate = exchangeRate.getExchangeRate();
					subAmount = rebateAmount.multiply(rate).setScale(2, BigDecimal.ROUND_DOWN);
				}
				// 如果返利金不够就不返利
				if (subAmount.compareTo(userMoney.getRebateMoney()) < 0) {

					UserRebateLog rebateLog = new UserRebateLog();
					rebateLog.setUid(order.getBorrowSellerId());
					rebateLog.setCreateTime(time);
					rebateLog.setOrderId(order.getId());
					rebateLog.setOrderCode(order.getOrderCode());
					rebateLog.setRebateRate(orderRebate.getBorrowSellerRebate());
					rebateLog.setRebateAmount(rebateAmount);
					rebateLog.setRebateModel(orderRebate.getBorrowSellerModel());
					rebateLog.setSendStatus(true);
					rebateLog.setSendTime(time);
					rebateLog.setCurrency(order.getCurrency());
					userRebateLogDao.save(rebateLog);

					// 增加代理点余额
					moneyService.addMoney(order.getBorrowSellerId(), order.getCurrency(), rebateAmount,
							rebateLog.getId(), null, CommonConstants.MONEY_LOG_REBATE,
							order.getOrderCode() + " Order rebate");
					// 减少平台收益 不需要减少平台收益
					// platformRevenueService.addPlatformRevenue(rebateLog.getId(),
					// CommonConstants.PLATFORM_REVENUE_LOG_REBATE,
					// rebateLog.getCurrency(), rebateAmount.negate(), order.getOrderCode()+ " Order
					// merchants rebate");

					// 扣除返利金
					moneyService.substractRebateMoney(order.getUid(), subAmount);
					UserRebateMoneyLog rlog = new UserRebateMoneyLog();
					rlog.setUserId(order.getUid());
					rlog.setToUserId(order.getBorrowSellerId());
					rlog.setOrderId(order.getId());
					rlog.setLogAmount(subAmount.negate());
					rlog.setLogCurrency(userMoney.getCurrency());
					rlog.setExchangeRate(rate);
					rlog.setOldLogAmount(rebateAmount.negate());
					rlog.setOldLogCurrency(order.getCurrency());
					rlog.setLogInfo("seller rebate");
					rlog.setCreateTime(new Date());
					rlog.setUserType(Integer.parseInt(UserMoneyConstants.CASH_USER_TYPE_SELLER));
					userRebateMoneyLogDao.save(rlog);

				}

			}
			// 代理返利
			if (orderRebate.getAgentModel() != null && orderRebate.getAgentRabate() != null
					&& orderRebate.getAgentRabate().doubleValue() > 0) {
				CabinetBO cabinet = cabinetDao.findBySysCode(order.getBorrowSysCode());

				BigDecimal rebateAmount = new BigDecimal(0);
				rebateAmount = price.multiply(orderRebate.getAgentRabate()).multiply(priceRate);
				rebateAmount = rebateAmount.setScale(2, BigDecimal.ROUND_DOWN);
				agentR = rebateAmount;
				// 进行币种转换
				BigDecimal rate = new BigDecimal(1);
				BigDecimal subAmount = rebateAmount;
				if (!order.getCurrency().equals(userMoney.getCurrency())) {
					ExchangeRateBO exchangeRate = exchangeRateDao.findOneByCurrency(order.getCurrency(),
							userMoney.getCurrency());
					if (exchangeRate == null) {
						throw new ExchangeRateNotFindException(order.getCurrency(), userMoney.getCurrency());
					}
					rate = exchangeRate.getExchangeRate();
					subAmount = rebateAmount.multiply(rate).setScale(2, BigDecimal.ROUND_DOWN);
				}
				// 如果返利金不够就不返利
				if (subAmount.compareTo(userMoney.getRebateMoney()) < 0) {
					UserRebateLog rebateLog = new UserRebateLog();
					rebateLog.setUid(cabinet.getAgentId());
					rebateLog.setCreateTime(time);
					rebateLog.setOrderId(order.getId());
					rebateLog.setOrderCode(order.getOrderCode());
					rebateLog.setRebateRate(orderRebate.getAgentRabate());

					rebateLog.setRebateAmount(rebateAmount);
					rebateLog.setRebateModel(orderRebate.getAgentModel());
					rebateLog.setSendStatus(true);
					rebateLog.setSendTime(time);
					rebateLog.setCurrency(order.getCurrency());
					userRebateLogDao.save(rebateLog);

					// 增加代理点余额
					moneyService.addMoney(cabinet.getAgentId(), order.getCurrency(), rebateAmount, rebateLog.getId(),
							null, CommonConstants.MONEY_LOG_REBATE, order.getOrderCode() + " Order rebate");

					// 减少平台收益
					// platformRevenueService.addPlatformRevenue(rebateLog.getId(),
					// CommonConstants.PLATFORM_REVENUE_LOG_REBATE,
					// rebateLog.getCurrency(), rebateAmount.negate(), order.getOrderCode()+" Order
					// agent rebate");
					// 扣除返利金
					moneyService.substractRebateMoney(order.getUid(), subAmount);
					UserRebateMoneyLog rlog = new UserRebateMoneyLog();
					rlog.setUserId(order.getUid());
					rlog.setToUserId(cabinet.getAgentId());
					rlog.setOrderId(order.getId());
					rlog.setLogAmount(subAmount.negate());
					rlog.setLogCurrency(userMoney.getCurrency());
					rlog.setExchangeRate(rate);
					rlog.setOldLogAmount(rebateAmount.negate());
					rlog.setOldLogCurrency(order.getCurrency());
					rlog.setLogInfo("agent rebate");
					rlog.setCreateTime(new Date());
					rlog.setUserType(Integer.parseInt(UserMoneyConstants.CASH_USER_TYPE_AGENT));
					userRebateMoneyLogDao.save(rlog);
				}
			}
			// 推荐人返利
			if (orderRebate.getRecommendedModel() != null && orderRebate.getRecommendedRabate() != null
					&& orderRebate.getRecommendedRabate().doubleValue() > 0) {
				SellerBO seller = sellerDao.findOne(order.getBorrowSellerId());

				BigDecimal rebateAmount = new BigDecimal(0);
				// rebateAmount
				// =price.multiply(orderRebate.getRecommendedRabate()).multiply(priceRate);
				rebateAmount = rebatePrice.subtract(sellerR).subtract(agentR);
				rebateAmount = rebateAmount.setScale(2, BigDecimal.ROUND_DOWN);

				// 进行币种转换
				BigDecimal rate = new BigDecimal(1);
				BigDecimal subAmount = rebateAmount;
				if (!order.getCurrency().equals(userMoney.getCurrency())) {
					ExchangeRateBO exchangeRate = exchangeRateDao.findOneByCurrency(order.getCurrency(),
							userMoney.getCurrency());
					if (exchangeRate == null) {
						throw new ExchangeRateNotFindException(order.getCurrency(), userMoney.getCurrency());
					}
					rate = exchangeRate.getExchangeRate();
					subAmount = rebateAmount.multiply(rate).setScale(2, BigDecimal.ROUND_DOWN);
				}
				// 如果返利金不够就不返利
				if (subAmount.compareTo(userMoney.getRebateMoney()) < 0) {
					UserRebateLog rebateLog = new UserRebateLog();
					rebateLog.setUid(seller.getRefereeUid());
					rebateLog.setCreateTime(time);
					rebateLog.setOrderId(order.getId());
					rebateLog.setOrderCode(order.getOrderCode());
					rebateLog.setRebateRate(orderRebate.getAgentRabate());

					rebateLog.setRebateAmount(rebateAmount);
					rebateLog.setRebateModel(orderRebate.getAgentModel());
					rebateLog.setSendStatus(true);
					rebateLog.setSendTime(time);
					rebateLog.setCurrency(order.getCurrency());
					userRebateLogDao.save(rebateLog);

					// 增加代理点余额
					moneyService.addMoney(seller.getRefereeUid(), order.getCurrency(), rebateAmount, rebateLog.getId(),
							null, CommonConstants.MONEY_LOG_REBATE, order.getOrderCode() + " Order rebate");

					// 减少平台收益
					// platformRevenueService.addPlatformRevenue(rebateLog.getId(),
					// CommonConstants.PLATFORM_REVENUE_LOG_REBATE,
					// rebateLog.getCurrency(), rebateAmount.negate(), order.getOrderCode()+" Order
					// recommend rebate");

					// 扣除返利金
					moneyService.substractRebateMoney(order.getUid(), subAmount);
					UserRebateMoneyLog rlog = new UserRebateMoneyLog();
					rlog.setUserId(order.getUid());
					rlog.setToUserId(seller.getRefereeUid());
					rlog.setOrderId(order.getId());
					rlog.setLogAmount(subAmount.negate());
					rlog.setLogCurrency(userMoney.getCurrency());
					rlog.setExchangeRate(rate);
					rlog.setOldLogAmount(rebateAmount.negate());
					rlog.setOldLogCurrency(order.getCurrency());
					rlog.setLogInfo("referee rebate");
					rlog.setCreateTime(new Date());
					rlog.setUserType(Integer.parseInt(UserMoneyConstants.CASH_USER_TYPE_REFEREE));
					userRebateMoneyLogDao.save(rlog);
				}
			}
		}
	}

	private BigDecimal getRebateAmount(BigDecimal amount, String currency) {
//		SysSetting setting = settingDao.findOne();
//		if(currency.equals(CommonConstants.CURRENCY_CNY)){
//			if(setting.getEachMaximumRegurgitantRmb()!=null&&amount.compareTo(setting.getEachMaximumRegurgitantRmb())>0){
//				return setting.getEachMaximumRegurgitantRmb();
//			}
//		}
//		if(currency.equals(CommonConstants.CURRENCY_MYR)){
//			if(setting.getEachMaximumRegurgitantMalaysia()!=null&&amount.compareTo(setting.getEachMaximumRegurgitantMalaysia())>0){
//				return setting.getEachMaximumRegurgitantMalaysia();
//			}	
//		}
//		if(currency.equals(CommonConstants.CURRENCY_SGD)){
//			if(setting.getEachMaximumRegurgitantSingapore()!=null&&amount.compareTo(setting.getEachMaximumRegurgitantSingapore())>0){
//				return setting.getEachMaximumRegurgitantSingapore();
//			}
//		}
		return amount;
	}

	public void orderRebate(OrderBO order) {

		log.info("订单:[{}],实际支付金额:[{}]", order.getOrderCode(), order.getPayPrice());
		
		if (null == order.getPayPrice() || order.getPayPrice().compareTo(BigDecimal.ZERO) <= 0) {
			log.info("实际支付金额小于等于0, 不予处理");
			return;
		}

		BigDecimal amount = order.getPayPrice();

		CabinetBO cabinet = cabinetDao.findOne(order.getBorrowCabinetId());
		// 商户返润
		if (null != cabinet.getSellerId()) {
			sellerRebate(cabinet.getSellerId(), amount, order.getCurrency(), order.getId(), order.getOrderCode());
		}
		// 代理商返润
		if (null != cabinet.getAgentId()) {
			agentRebate(cabinet.getAgentId(), amount, order.getCurrency(), order.getId(), order.getOrderCode());
		}
	}

	/**
	 * 代理商返润
	 * 
	 * @param uid
	 * @param amount
	 * @param currency
	 * @param orderId
	 * @param orderCode
	 */
	public void agentRebate(Long uid, BigDecimal amount, String currency, Long orderId, String orderCode) {

		if (null == uid) {
			return;
		}
		AgentBO agent = agentDao.findOne(uid);
		if (null == agent || agent.getRebateRate().compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}
		BigDecimal multiply = amount.multiply(agent.getRebateRate());

		log.info("订单:[{}] ,代理商:[{}] ,返润比例:[{}], 金额:[{}]", orderCode, agent.getAgentName(), agent.getRebateRate(),  multiply);
		
		// 加钱
		addMoney(uid, currency, multiply);

		// 添加收益明细
		addMoneyLog(uid, multiply, currency, orderCode);

		// 添加平台收益支出
		addPlatformRevenue(multiply, currency, orderId, orderCode, PlatformRevenueConstants.LOG_TYPE_AGENT_REBATE);

	}

	/**
	 * 商户返润
	 * 
	 * @param uid
	 * @param amount
	 * @param currency
	 * @param orderId
	 * @param orderCode
	 */
	public void sellerRebate(Long uid, BigDecimal amount, String currency, Long orderId, String orderCode) {

		if (null == uid) {
			return;
		}

		SellerBO seller = sellerDao.findOne(uid);
		if (null == seller || seller.getRebateRate().compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		BigDecimal multiply = amount.multiply(seller.getRebateRate());
		
		log.info("订单:[{}],商户:[{}] ,返润比例: [{}], 金额:[{}]", orderCode,  seller.getSellerName(), seller.getRebateRate(), multiply);
		
		// 加钱
		addMoney(uid, currency, multiply);

		// 添加收益明细
		addMoneyLog(uid, multiply, currency, orderCode);

		// 添加平台收益支出
		addPlatformRevenue(multiply, currency, orderId, orderCode, PlatformRevenueConstants.LOG_TYPE_SELLER_REBATE);

	}

	/**
	 * 添加收入
	 * @param uid
	 * @param currency
	 * @param amount
	 */
	public void addMoney(Long uid, String currency, BigDecimal amount) {
		UserMoneyBO userMoney = new UserMoneyBO();
		userMoney.setUid(uid);
		userMoney.setCurrency(currency);
		userMoney.setAvailableBalance(amount);
		userMoneyDao.insertOrUpdate(userMoney);
	}

	/**
	 * 添加收益明细
	 * 
	 * @param uid
	 * @param amount
	 * @param currency
	 * @param orderCode
	 */
	public void addMoneyLog(Long uid, BigDecimal amount, String currency, String orderCode) {

		UserMoneyLogBO moneyLog = new UserMoneyLogBO();
		moneyLog.setLogUserId(uid);
		moneyLog.setLogAmount(amount);
		moneyLog.setLogCurrency(currency);
		moneyLog.setOldLogAmount(amount);
		moneyLog.setOldLogCurrency(currency);
		moneyLog.setExchangeRate(BigDecimal.ONE);
		moneyLog.setLogInfo("Order:" + orderCode + "Rebate");
		moneyLog.setCreateTime(new Date());
		moneyLog.setLogType(UserMoneyConstants.LOG_TYPE_ORDER_REBATE);
		userMoneyLogDao.save(moneyLog);
	}

	/**
	 * 添加平台支出
	 * 
	 * @param amount
	 * @param currency
	 * @param orderId
	 * @param orderCode
	 * @param logType
	 */
	public void addPlatformRevenue(BigDecimal amount, String currency, Long orderId, String orderCode, String logType) {
		PlatformRevenueBO revenue = new PlatformRevenueBO();
		revenue.setExchangeRate(BigDecimal.ONE);
		revenue.setLogAmount(amount.multiply(new BigDecimal("-1")));
		revenue.setLogCurrency(currency);
		revenue.setSourceId(orderId);
		revenue.setCreateTime(new Date());
		revenue.setLogInfo("Order:" + orderCode + "Rebate");
		revenue.setLogType(logType);
		platformRevenueDao.save(revenue);
	}
}
