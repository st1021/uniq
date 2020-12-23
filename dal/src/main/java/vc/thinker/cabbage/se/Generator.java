package vc.thinker.cabbage.se;


import java.util.List;

import com.google.common.collect.Lists;
import com.sinco.mybatis.generator.GeneratorTable;
import com.sinco.mybatis.generator.MyBatisGeneratorTool2;
import com.sinco.mybatis.generator.config.JdbcConfig;

/**
 * 代码自动产生
 * @author james
 *
 */
public class Generator {

	public static void main(String[] args) {
		
		//自动产生代码生成改造 by james
		JdbcConfig jdbc = new JdbcConfig(
				"jdbc:mysql://192.168.1.250:3306/nomo-saas-1.0.x?useUnicode=true&characterEncoding=utf8",
				"root", "admin123",
				"com.mysql.jdbc.Driver");
		
//		String itemPath = "/Users/james/git/nomo/nomo/dal/src/main/java";
//		String itemPath = "D:/workspace/nomo1.1/nomo/dal/src/main/java";
		
		String itemPath = "E:/radish-3/nomo/dal/src/main/java";
		
		
		String rootPackage = "vc.thinker.cabbage.se";

		MyBatisGeneratorTool2 tool = new MyBatisGeneratorTool2(jdbc, itemPath, rootPackage);
		tool.setGeneratorBO(false);
		tool.setGeneratorDao(false);
		tool.setGeneratorMapperJava(false);
		
		List<GeneratorTable> tableList=Lists.newArrayList(
//				new GeneratorTable("cabbage-2.1.0", "se_cabinet_type", "CabinetType")
//				new GeneratorTable("cabbage-2.1.0", "se_cabinet", "Cabinet")
//				new GeneratorTable("cabbage-2.1.0", "se_cabinet_charge_rule", "CabinetChargeRule"),
//				new GeneratorTable("cabbage-2.1.0", "se_portable_battery_type", "PortableBatteryType")
//				new GeneratorTable("cabbage-2.1.0", "se_order_pb_buy", "OrderPbBuy")
//				new GeneratorTable("cabbage-2.1.0", "se_portable_battery", "PortableBattery")
//				new GeneratorTable("cabbage-2.1.0", "se_sys_code", "SysCode")
//				new GeneratorTable("cabbage-2.1.0", "se_feedback", "Feedback")
				new GeneratorTable("cabbage-2.1.0", "se_feedback_type", "FeedbackType")
//				new GeneratorTable("cabbage-2.1.0", "se_order", "Order")
//				new GeneratorTable("cabbage-2.1.0", "se_order_rebate", "OrderRebate"),
//				new GeneratorTable("cabbage-2.1.0", "se_user_rebate_log", "UserRebateLog")
//				new GeneratorTable("cabbage-2.1.0", "se_feedback_message", "FeedbackMessage")
//				new GeneratorTable("cabbage-2.1.0", "se_cabinet_firmware", "CabinetFirmware")
		);

		tool.generator(tableList);
		
		System.out.println("恭喜生成完成！！！");
	}
	
}
