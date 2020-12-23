package vc.thinker.cabbage.user.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vc.thinker.cabbage.common.PaymentConstants;
import vc.thinker.cabbage.user.CommonConstants;
import vc.thinker.pay.PayException;
import vc.thinker.pay.PayHandler;
import vc.thinker.pay.PayHandlerFactory;
import vc.thinker.pay.fondy.FondyPayHandler;
import vc.thinker.pay.request.DirectPayRequest;
import vc.thinker.pay.response.DirectPayResponse;
import vc.thinker.sys.bo.UserAccountBO;
import vc.thinker.sys.dao.UserAccountDao;

/**
 *
 * @author ZhangGaoXiang
 * @time Dec 16, 20192:29:14 PM
 */
@Service
@Transactional
public class PayHelpService {

	@Autowired
	private UserAccountDao accountDao;

	@Autowired
	@Lazy(true)
	private PayHandlerFactory payHandlerFactory;

	@SuppressWarnings("rawtypes")
	public DirectPayResponse getPayResponse(String paymentMark, Long uid, 
			DirectPayRequest payRequest, String rectoken, Date rectokenLifetime)
			throws PayException {
		PayHandler payHandler = payHandlerFactory.getPayHandler(paymentMark);
		switch (paymentMark) {
		case PaymentConstants.PAYMENT_MARK_WX_JS_PAY:
			UserAccountBO account = accountDao.findByUid(uid, CommonConstants.ACCOUNT_TYPE_3);
			return payHandler.jsPay(payRequest, account.getLoginName());
		case PaymentConstants.PAYMENT_MARK_FONDY:
			if(StringUtils.isNotBlank(rectoken) && null != rectokenLifetime && new Date().before(rectokenLifetime)) {
				FondyPayHandler fondyPayHandler = (FondyPayHandler) payHandler;
				payRequest.setToken(rectoken);
				return fondyPayHandler.rectokenPay(payRequest);
			}
			return payHandler.appPay(payRequest);
		default:
			return payHandler.appPay(payRequest);
		}
	}

	public String getPayOrderCode(String type, Long uid) {
		StringBuffer sb = new StringBuffer();
		sb.append(type).append("-").append(uid).append("-").append(getDateTimeMils()).append(getSixRandom());
		return sb.toString();
	}

	/**
	 * 获取回调地址
	 * 
	 * @param paymentMark
	 * @param payCallback
	 * @return
	 */
	public String getNotifyUrl(String paymentMark, String payCallback) {
		switch (paymentMark) {
		case PaymentConstants.PAYMENT_MARK_FONDY_APPLE_PAY:
			return payCallback + "/applepay/notify"; 
		case PaymentConstants.PAYMENT_MARK_FONDY:
			return payCallback + "/fondy/notify";
		case PaymentConstants.PAYMENT_MARK_CASHFREE:
			return payCallback + "/cashfree/notify";
		case PaymentConstants.PAYMENT_MARK_ALIPAY:
			return payCallback + "/alipay/notify";
		default:
			return payCallback + "/weixin/notify";
		}
	}
 
	public String getDateTimeMils() {
		return DateFormatUtils.format(new Date(), "yyyyMMddHHmmsss");
	}

	public String getSixRandom() {
		String valueOf = String.valueOf(Math.random());
		return valueOf.substring(valueOf.length() - 6);
	}

	public static void main(String[] args) throws ParseException {
		String s = "02.16.2020 00:00:00";
		SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
		Date parse = sdf.parse(s);
		System.out.println(new Date().before(parse));
	}
	
	public Date formatDate(String rectoken_lifetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
		try {
			return sdf.parse(rectoken_lifetime);
		} catch (ParseException e) {
			return null;
		}
	}

	
}
