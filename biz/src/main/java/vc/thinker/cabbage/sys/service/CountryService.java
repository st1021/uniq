package vc.thinker.cabbage.sys.service;


import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.sinco.dic.client.DicContent;
import vc.thinker.cabbage.sys.ResourceContants;
import vc.thinker.cabbage.sys.bo.CountryBO;
import vc.thinker.cabbage.sys.bo.ResourceBO;
import vc.thinker.cabbage.sys.dao.CountryDao;
import vc.thinker.cabbage.sys.dao.ResourceDao;
import vc.thinker.cabbage.sys.dao.SysSettingDao;
import vc.thinker.cabbage.sys.model.Country;
import vc.thinker.cabbage.sys.model.SysSetting;
import vc.thinker.cabbage.sys.vo.CountryVO;

@Service
@Transactional
public class CountryService extends XService<CountryBO, CountryVO, CountryDao> {

//	private static Logger logger = LoggerFactory.getLogger(CountryService.class);
	
    @Autowired
    private CountryDao countryDao;

    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private DicContent dicContent;
    
    @Autowired
    private SysSettingDao sysSettingDao;
    
    /**
     * 
     * @return
     */
    public Set<String> findCurrencies() {
    	List<CountryBO> countries = countryDao.findAll();
		Set<String> currencies = new HashSet<String>();
		for (CountryBO country : countries) {
			if (StringUtils.isNotBlank(country.getCurrency())) {
				currencies.add(country.getCurrency());
			}
		}
		
		return currencies;
    }
    
    public Set<String> findDefaultCurrency(){
    	Set<String> currencies = new HashSet<String>();
    	SysSetting findOne = sysSettingDao.findOne();
    	currencies.add(findOne.getPlayformDefaultCurrency());
    	return currencies;
    }
    
    /**
     * 
     * @return
     */
    public Map<String, CountryBO> findCountriesMap() {
    	Map<String, CountryBO> map = new HashMap<String, CountryBO>();
        List<CountryBO> countryList = countryDao.findAll();
        for (CountryBO country : countryList) {
        	map.put(country.getNationCode(), country);
		}
        
        return map;
    }
    
    public List<CountryBO> findAll() {
        return countryDao.findAll();
    }

    public CountryBO findDefault() {
        return countryDao.findDefault();
    }
    
    public List<CountryBO> groupByLanguage(){
    	return countryDao.groupByLanguage();
    }

    /**
     * 根据语言获取对应的语言包
     * @param language
     * @return
     */
    public String queryLanguagePack(String language) {

//    	if(language.contains("-")){
//    		language = language.split("-")[0];
//    	}
    	
        //校验国家
//        List<CountryBO> c_list = countryDao.findByLanguage(language);
//        
//        if (c_list.isEmpty()) {
//            throw new CountryNotExitException();
//        }

        return findByOneLevel(language);
        
    }


    /**
     * 资源数据处理
     * @param param
     * @return
     */
    public String handlerText(String param) {
        String[] array = param.split("\\.");
        return array[array.length - 1];
    }

  
    /**
     * 查询语言包的最后更新时间
     * @param playType
     * @param language
     * @return
     */
    public Date queryUpdateTimeByLanguage(String playType,String language) {
    	language = language.equals("zh") ? "zh-cn" : language;
		return resourceDao.queryUpdateTimeByLanguage(playType,language);
	}

    /**
     * 设置默认国家
     * @param id
     * @return
     */
    public void setDefault(Long id) {
    	
    	// 取消默认设置
    	countryDao.updateFalseDefault();
    	
    	//设置为默认
    	Country country = new Country();
    	country.setId(id);
    	country.setIsDefault(true);;
    	
    	countryDao.save(country);
    	
    	//刷新缓存
    	dicContent.refreshDicCache(CountryBO.class);
    	
    }

    /**
     * 从缓存中获取默认的语言
     * @return
     */
	public String findCashDat() {
		
		List<CountryBO> list = dicContent.getDics(CountryBO.class);
		
		CountryBO country = list.isEmpty()?null:list.get(0);

		if(null == country){
			country = countryDao.findDefault();
		}
		
		return country.getDefaultLanguage();
	}
	
	/**
	 * 语言包查询(在只有一级菜单的情况下）
	 * @param language
	 * @return
	 */
	public String findByOneLevel(String language){
		
		//用于组装返回数据
		JSONObject respObject = new JSONObject();
		
		//分组查询
		List<ResourceBO> g_moudle = 
        		resourceDao.groupByModule(language,
        				ResourceContants.RESOURCE_PLAY_TYEP_APP);
		
		for (ResourceBO e : g_moudle) {
			
			JSONObject object = new JSONObject();
			
			List<ResourceBO> list = resourceDao.findResource(
							ResourceContants.RESOURCE_PLAY_TYEP_APP,
							language,  
							e.getFunctionModule(), 
							null);
			
			for (ResourceBO r : list) {
				object.put(handlerText(r.getName()), r.getText());
			}
			
			respObject.put(e.getFunctionModule(), object);
		}
		
		return respObject.toString();
	}
	
	public String findByTwoLevel(String language){ 
		//用于组装返回数据
		JSONObject respObject = new JSONObject();
		
		//分组查询
		List<ResourceBO> g_moudle = 
        		resourceDao.groupByModule(language,
        				ResourceContants.RESOURCE_PLAY_TYEP_APP);
		
		for (ResourceBO g : g_moudle) {
			
			JSONObject firstObject = new JSONObject();
			
			List<ResourceBO> g_second = 
            		resourceDao.groupBySecondMenu( language, 
            				ResourceContants.RESOURCE_PLAY_TYEP_APP, 
            				g.getFunctionModule());
			
			for (ResourceBO s : g_second) {
				
				List<ResourceBO> list = 
						resourceDao.findResource(ResourceContants.RESOURCE_PLAY_TYEP_APP, 
						language,s.getFunctionModule(), s.getSecondMenu());
				
				if(s.getFunctionModule().equals(s.getSecondMenu())){
					list.forEach(e->{
						firstObject.put(handlerText(e.getName()), e.getText());
					});
				}else {
					
					JSONObject secondObject = new JSONObject();
					list.forEach(e->{
						secondObject.put(handlerText(e.getName()), e.getText());
					});
					
					firstObject.put(s.getSecondMenu(), secondObject);
				}
			}
			
			respObject.put(g.getFunctionModule(), firstObject);
		}
		return respObject.toString();
	}
	
	
	public static void createSql() {
		String string = "{\n" + 
				"    \"seller\": {\n" + 
				"        \"availableCount\": \"Автоматы\",\n" + 
				"        \"address\": \"Адрес\",\n" + 
				"        \"select\": \"Выбрать\",\n" + 
				"        \"price\": \"Цена: \",\n" + 
				"        \"buttonTitle\": \"Сканировать\",\n" + 
				"        \"borrow\": \"Получить\",\n" + 
				"        \"phoneNum\": \"Телефон\",\n" + 
				"        \"selectType\": \"Выберите тип разъёма\",\n" + 
				"        \"businessTime\": \"График роботы\",\n" + 
				"        \"noAvailableDevice\": \"нет доступных устройств\",\n" + 
				"        \"title\": \"Телефон:\",\n" + 
				"        \"firstSelectType\": \"Пожалуйста, сначала выберите тип разъёма.\"\n" + 
				"    },\n" + 
				"    \"personalInfo\": {\n" + 
				"        \"password\": \"Пароль\",\n" + 
				"        \"bankCard\": \"Банковская карта\",\n" + 
				"        \"phone\": \"Телефон\",\n" + 
				"        \"unbind\": \"Выход\",\n" + 
				"        \"upgradevip\": \"VIP (Активировать)\",\n" + 
				"        \"nickname\": \"Пользователь\",\n" + 
				"        \"binding\": \"Вход\",\n" + 
				"        \"joinvip\": \"Подключить VIP\",\n" + 
				"        \"avatar\": \"Фото\",\n" + 
				"        \"memberUntil\": \"Действителен до:\",\n" + 
				"        \"title\": \"Персональные данные\",\n" + 
				"        \"linked\": \"Подключено\"\n" + 
				"    },\n" + 
				"    \"wallet\": {\n" + 
				"        \"refundSuccess\": \"Средства возвращены\",\n" + 
				"        \"amount\": \"Баланс\",\n" + 
				"        \"bankCard\": \"Банковская карта\",\n" + 
				"        \"refunding\": \"Возврат средств\",\n" + 
				"        \"tipsTitle\": \"Выполните следующие действия:\",\n" + 
				"        \"tipsContent\": \"Выполните действия: \\n1. Создайте учетную запись и подключитесь к приложению Power Now App.\\n2. Затем пополните баланс .\\n3. Этот баланс можно вернуть в любое время по Вашему запросу!.\\n4. Вернитесь в главное меню, Нажмите сканировать!\\n5. Наведите на QR код на автомате, выберите тип разъёма, берите и пользуйтесь!\\n6.Помните,возврат средств проводится по запросу\",\n" + 
				"        \"agreementTipsContent\": \"Просмотреть условия\",\n" + 
				"        \"depositNotPaid\": \"Баланс не пополнен\",\n" + 
				"        \"buttonTitle\": \"Условия договора\",\n" + 
				"        \"pay\": \"Пополнить баланс\",\n" + 
				"        \"listHeader\": \"Сумма пополнения\",\n" + 
				"        \"refundConfirmation\": \"Вы действительно хотите вернуть средства?\",\n" + 
				"        \"title\": \"Баланс\",\n" + 
				"        \"depositPaid\": \"Баланс пополнен\",\n" + 
				"        \"paySuccess\": \"Платёж проведён\",\n" + 
				"        \"purchased\": \"Батарея приобретена\",\n" + 
				"        \"titleHead\": \"Баланс\",\n" + 
				"        \"deposit\": \"Баланс\",\n" + 
				"        \"buttonTitlet\": \"Top Up\",\n" + 
				"        \"successTips\": \"Возврат средств проведён\",\n" + 
				"        \"category\": \"Категория\",\n" + 
				"        \"agreementTipsTitle\": \"Нажмите Условия договора , что бы прочитать договор\",\n" + 
				"        \"refund\": \"Пополнение баланса\"\n" + 
				"    },\n" + 
				"    \"index\": {\n" + 
				"        \"cancel\": \"Отменить\",\n" + 
				"        \"turnOffLight\": \"Выключить подсветку\",\n" + 
				"        \"feeTitle\": \"Комиссия\",\n" + 
				"        \"gotIt\": \"Получил\",\n" + 
				"        \"skip\": \"Пропустить\",\n" + 
				"        \"2in1deviceName\": \"IOS/microUSB\",\n" + 
				"        \"title\": \"Автоматы рядом\",\n" + 
				"        \"content\": \"1.Вы можете приобрести батарею используя баланс.2.Ваш баланс будет уменьшен, при приобретении.3.Текущее использование будет бесплатным.4.Вы можете арендовать другую батарею после пополнения баланса.\",\n" + 
				"        \"3in1deviceName\": \"IOS/microUSB/Type-C\",\n" + 
				"        \"subTitle\": \"Если Вы не можете вернуть батарею, Вы можете её купить.\",\n" + 
				"        \"scanBtnTitle\": \"Сканировать\",\n" + 
				"        \"retry\": \"Попробуйте ещё?\",\n" + 
				"        \"unkonwPayType\": \"Получен неизвестный вид платежа. Пожалуйста обновите приложение.\",\n" + 
				"        \"onQRCode\": \"С QR кода\",\n" + 
				"        \"purchaseSuccess\": \"Покупка успешно выполнена\",\n" + 
				"        \"cameraAuth\": \"%s используйте камеру для сканирования кода.\",\n" + 
				"        \"avaibleDesc\": \"доступно\",\n" + 
				"        \"durationTitle\": \"Длительность(мин)\",\n" + 
				"        \"timeOut\": \"Время истекло\",\n" + 
				"        \"confirm\": \"Оплатить\",\n" + 
				"        \"proceedBtnTitle\": \"Супить сейчас\",\n" + 
				"        \"typecDeviceName\": \"Type-C\",\n" + 
				"        \"turnOnLight\": \"включить подсветку\",\n" + 
				"        \"sellerIdleTail\": \"Возможно\",\n" + 
				"        \"feedbackTips\": \"вернуться но не выходить?\"\n" + 
				"    },\n" + 
				"    \"global\": {\n" + 
				"        \"cancel\": \"Отменить\",\n" + 
				"        \"globalLogOut\": \"Нажмите повторно для выхода\",\n" + 
				"        \"submit\": \"Подписать\",\n" + 
				"        \"distance\": \"Расстояние\",\n" + 
				"        \"upload\": \"Подписаться\",\n" + 
				"        \"loadpath\": \"Не найден путь загрузки, пожалуйста, попробуйте позже.\",\n" + 
				"        \"minitues\": \"минуты\",\n" + 
				"        \"save\": \"Сохранить\",\n" + 
				"        \"title\": \"Продлить\",\n" + 
				"        \"tips\": \"Подсказки\",\n" + 
				"        \"qrcodeError\": \"Невозможно считать QR код\",\n" + 
				"        \"bind\": \"Привязать\",\n" + 
				"        \"OK\": \"OK\",\n" + 
				"        \"unknowType\": \"Неизвестный тип\",\n" + 
				"        \"boxUnit\": \"Доступно\",\n" + 
				"        \"app\": \"Выберите Ваше приложение с навигацией\",\n" + 
				"        \"sure\": \"Конечно\",\n" + 
				"        \"nopath\": \"Извините, нет доступа\",\n" + 
				"        \"maps\": \"Amap Maps\",\n" + 
				"        \"facebook\": \"Facebook\",\n" + 
				"        \"wechat\": \"Wechat\",\n" + 
				"        \"nodata\": \"Данные отсутствуют\",\n" + 
				"        \"message\": \"Пожалуйста подключитесь повторно.\",\n" + 
				"        \"doFailed\": \"Ошибка\",\n" + 
				"        \"error_tips_2\": \"Ошибка связи, пожалуйста попробуйте снова\",\n" + 
				"        \"error_tips_3\": \"Связь с сервером отсутствует\",\n" + 
				"        \"unit\": \"мин\",\n" + 
				"        \"doSuccess\": \"Успешно\",\n" + 
				"        \"error_tips_1\": \"системная ошибка\",\n" + 
				"        \"unkonwError\": \"Неизвестная ошибка\",\n" + 
				"        \"error_tips_6\": \"Ошибка сервера\",\n" + 
				"        \"error_tips_7\": \"Сервер обновляется, подождите...,\",\n" + 
				"        \"error_tips_4\": \"Время соединения закончилось\",\n" + 
				"        \"error_tips_5\": \"Ошибка верификации\",\n" + 
				"        \"error_tips_8\": \"Время запроса исчерпано, попробуйте позже\",\n" + 
				"        \"detail\": \"Детали\",\n" + 
				"        \"error_tips_9\": \"У Вас недостаточно средств\",\n" + 
				"        \"loadingTitle\": \"Запись\\\"\",\n" + 
				"        \"complete\": \"Завершено\",\n" + 
				"        \"denied\": \"адаптер отклонён\",\n" + 
				"        \"walk\": \"Идти\"\n" + 
				"    },\n" + 
				"    \"menu\": {\n" + 
				"        \"contactUs\": \"Обслуживание клиентов\",\n" + 
				"        \"VIPEnabled\": \"VIP\",\n" + 
				"        \"VIPDisabled\": \"VIP (недоступен)\",\n" + 
				"        \"wallet\": \"Кошелёк\",\n" + 
				"        \"helpTitle\": \"Помощь\",\n" + 
				"        \"coupons\": \"Купоны\",\n" + 
				"        \"settingTitle\": \"настройки\",\n" + 
				"        \"orderHistory\": \"Мои заказы\",\n" + 
				"        \"redeem\": \"Выкупить\",\n" + 
				"        \"promoCode\": \"Промокод\",\n" + 
				"        \"title\": \"Помощь\"\n" + 
				"    },\n" + 
				"    \"signup\": {\n" + 
				"        \"submitButtonTitle\": \"Подписаться\",\n" + 
				"        \"country\": \"Страна\",\n" + 
				"        \"phoneTitle\": \"Телефон\",\n" + 
				"        \"promoCodePlaceHolder\": \"Введите промокод здесь\",\n" + 
				"        \"getIdCodeBtnTitle\": \"Запрос\",\n" + 
				"        \"additional\": \"Дополнительная информация\",\n" + 
				"        \"idCodeTitle\": \"SMS код\",\n" + 
				"        \"idCodePlaceHolder\": \"Введите SMS код\",\n" + 
				"        \"getIdCodeAgainBtnTitle\": \"Reacquire\",\n" + 
				"        \"agreementHead\": \"Я прочитал и согласен\",\n" + 
				"        \"passwordTitle\": \"Новый пароль\",\n" + 
				"        \"promoCodeTitle\": \"Промокод\",\n" + 
				"        \"selectCountryRegion\": \"Выберите страну/регион\",\n" + 
				"        \"passwordPlaceHolder\": \"Введите новый пароль\",\n" + 
				"        \"phonePlaceHolder\": \"Введите правильный номер телефона\",\n" + 
				"        \"agreementContent\": \"<Регистрационный договор>\"\n" + 
				"    },\n" + 
				"    \"points\": {\n" + 
				"        \"currentPoints\": \"Текущие баллы\",\n" + 
				"        \"pointsUnit\": \"баллы\",\n" + 
				"        \"pointsRules\": \"Условия баллов\",\n" + 
				"        \"title\": \"Баллы\",\n" + 
				"        \"pointsDetails\": \"Детали баллов\"\n" + 
				"    },\n" + 
				"    \"setting\": {\n" + 
				"        \"logout\": \"Выйти\",\n" + 
				"        \"language\": \"Язык\",\n" + 
				"        \"title\": \"Очистить кеш\"\n" + 
				"    },\n" + 
				"    \"feedback\": {\n" + 
				"        \"issueType\": \"Выберите Вашу проблему\",\n" + 
				"        \"feedbackDescriptionPlaceholder\": \"Описание не может быть пустым.\",\n" + 
				"        \"feedbackNumberPlaceholder\": \"Введите номер\",\n" + 
				"        \"remarkPlaceHolder\": \"Оставьте Ваш отзыв здесь…\",\n" + 
				"        \"submit\": \"Подписаться\",\n" + 
				"        \"more\": \"Больше\",\n" + 
				"        \"submitSuccessTips\": \"Отзыв добавлен успешно\",\n" + 
				"        \"IDPlaceHolder\": \"Введите ID Автомата\",\n" + 
				"        \"title\": \"Отзыв\",\n" + 
				"        \"photos\": \"Обновить фото\",\n" + 
				"        \"tips\": \"Если при возвращении батарея испорчена, верните ее в автомат и оставьте отзыв на странице оплаты. Спасибо.\"\n" + 
				"    },\n" + 
				"    \"messageCenter\": {\n" + 
				"        \"detail\": \"Детали сообщения\",\n" + 
				"        \"title\": \"Центр сообщений\"\n" + 
				"    },\n" + 
				"    \"signin\": {\n" + 
				"        \"submitButtonTitle\": \"Войти\",\n" + 
				"        \"thirdPart\": \"Войти с помощью\",\n" + 
				"        \"signupButtonTitle\": \"Зарегистрироваться\",\n" + 
				"        \"phoneTitle\": \"Телефон\",\n" + 
				"        \"agreement\": \"Договор\",\n" + 
				"        \"forgetPassword\": \"Забыли?\",\n" + 
				"        \"passwordTitle\": \"Пароль\",\n" + 
				"        \"passwordPlaceHolder\": \"Введите пароль\",\n" + 
				"        \"phonePlaceHolder\": \"Введите корректный номер телефона\"\n" + 
				"    },\n" + 
				"    \"member\": {\n" + 
				"        \"memberShipDesc\": \"У Вас безлимитное использование\",\n" + 
				"        \"notPayDepositTips\": \"Вы ещё не пополнили баланс\",\n" + 
				"        \"buttonTitle\": \"Top Up\",\n" + 
				"        \"enableDescHead\": \"Время истечет\",\n" + 
				"        \"couponInfo1\": \"Может быть использовано везде и когда угодно.\",\n" + 
				"        \"couponInfo2\": \"Может быть использовано через %s ,где угодно.\",\n" + 
				"        \"title\": \"Мой VIP\",\n" + 
				"        \"membershipPeriod\": \"Период членства\",\n" + 
				"        \"tips\": \"Купить карту участника\\n наслаждайтесь пользованием VIP  и БОЛЬШЕ\",\n" + 
				"        \"vaildTill\": \"Действителен до\",\n" + 
				"        \"joinMembership\": \"Стать участником\",\n" + 
				"        \"timeDescHead\": \"До\",\n" + 
				"        \"disableDesc\": \"Вы ещё не имеете VIP статуса\",\n" + 
				"        \"buyMembershipCard\": \"Купить карту участника\\n \"\n" + 
				"    },\n" + 
				"    \"payment\": {\n" + 
				"        \"paymentSuccess\": \"Платёж успешно проведён\",\n" + 
				"        \"buttonTitle\": \"Оплатить\",\n" + 
				"        \"discount\": \"Скидка\",\n" + 
				"        \"payDeposit\": \" Пополнить баланс %s %s \",\n" + 
				"        \"title\": \"Возвращено успешно\",\n" + 
				"        \"freeForVIP\": \"Бесплатно для VIP\",\n" + 
				"        \"noticeTitle\": \"Запомните\",\n" + 
				"        \"tips\": \"Не возможно получить информацию по заказу.\\n Попробуйте позже.\",\n" + 
				"        \"balance\": \"Мой кошелёк\",\n" + 
				"        \"coupons\": \"Купоны \\n\",\n" + 
				"        \"noCoupons\": \"Предложения отсутствуют\",\n" + 
				"        \"borrowTime\": \"Время получения:\",\n" + 
				"        \"returnedPlace\": \"Место возврата:\",\n" + 
				"        \"noticeDesc\": \"1.Если Вы не желаете более пользоваться Нашими услугами, Вы можете .2.Зайдите в кошелёк, выберите баланс и нажмите кнопку возврат средств. Средства будут возвращены на протяжении 1-4 дней.\",\n" + 
				"        \"free\": \"Бесплатно\",\n" + 
				"        \"chargingFor\": \"Заряжено до\",\n" + 
				"        \"insufficientBalance\": \"Вашего баланса недостаточно, перейдите в Top Up\",\n" + 
				"        \"chekOrder\": \"Просмотреть\",\n" + 
				"        \"pay\": \"Оплатить %s\",\n" + 
				"        \"check\": \"Проверить\",\n" + 
				"        \"paymentFailed\": \"Оплата отклонена\",\n" + 
				"        \"returnTime\": \"Время возврата:\",\n" + 
				"        \"borrowedPlace\": \"Место получения:\",\n" + 
				"        \"purchased\": \"Покупка батареи\",\n" + 
				"        \"totalFee\": \"Общая сумма:\",\n" + 
				"        \"orderDetail\": \"Детали заказа\",\n" + 
				"        \"orderCode\": \"Код заказа:\",\n" + 
				"        \"discountHead\": \"Купоны \\n\",\n" + 
				"        \"feedbackTips\": \"Encounter проблемы\"\n" + 
				"    },\n" + 
				"    \"redeem\": {\n" + 
				"        \"redemptionCode\": \"Мой код выкупа: %s\",\n" + 
				"        \"details\": \"Детали выкупа\",\n" + 
				"        \"title\": \"Вознаграждение\",\n" + 
				"        \"placeHolder\": \"Введите код выкупа\"\n" + 
				"    }\n" + 
				"}\n" + 
				"";
		JSONObject parseObject = JSONObject.parseObject(string);
		
		System.out.println(parseObject.size());
		StringBuffer sb = new StringBuffer();
		
		parseObject.entrySet().forEach(e->{
			JSONObject value = (JSONObject) e.getValue();
			value.entrySet().forEach(e1->{
				sb.append("insert into sys_resource values (null");
				sb.append(", 'app.") .append(e.getKey()) .append(".").append(e1.getKey()).append("'");
				sb.append(", '").append(e1.getValue()).append("'");
				sb.append(", 'russion'");
				sb.append(" ,'app'");
				sb.append(", '").append(e.getKey()).append("'");
				sb.append(" ,'").append(e.getKey()).append("'");
				sb.append(" ,NOW()").append(" ,NOW())").append(";");
				System.out.println(sb.toString());
				sb.setLength(0);
			});
		});
	}
	
	public static void main(String[] args) {
		createSql();
	}
}
