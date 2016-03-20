

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

public class MypointInterface {
	//积分充值接口			
	
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public static void mypointRecharge() {
		//造测试数据
		TreeMap param=new TreeMap();
		String companyCode="finnance";  //公司code,anyo提供
		String sourceType = "1";        //普通积分:1,奖励积分:2	
		String batchNum = "2016021701"; //批次号	
		//提交日期
		String subDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		//充值时间
		String transDate = new SimpleDateFormat("yyyyMM").format(new Date());
		//该批次充值总积分
		String pointsSum = "3000";
		//该批次充值人数
		String pointListSize = "3";		
	

		List zpointList=new ArrayList();
		Map user1=new HashMap();
		user1.put("sn","2016021701001");
		user1.put("userName","test1");
		user1.put("idCard","430529198809293065");
		user1.put("transDate",transDate);
		user1.put("points","1000");
		user1.put("mobile","13053250684");
		user1.put("dealer","经销商名称1");
		user1.put("position","融资经理");
		user1.put("province","湖南省");
		user1.put("city","长沙市");
		zpointList.add(user1);

		Map user2=new HashMap();
		user1.put("sn","2016021701002");
		user1.put("userName","test2");
		user1.put("idCard","430529198809293078");
		user1.put("transDate",transDate);
		user1.put("points","1000");
		user1.put("mobile","13053250333");
		user1.put("dealer","经销商名称2");
		user1.put("position","融资经理");
		user1.put("province","江苏省");
		user1.put("city","南京市");
		zpointList.add(user2);
		
		Map user3=new HashMap();
		user1.put("sn","2016021701003");
		user1.put("userName","test3");
		user1.put("idCard","430529198809294567");
		user1.put("transDate",transDate);
		user1.put("points","1000");
		user1.put("mobile","13053250444");
		user1.put("dealer","上汽凯迪拉克商城");
		user1.put("position","经理");
		user1.put("province","安徽");
		user1.put("city","合肥");
		zpointList.add(user3);
		//参数塞入值
		//封装数字签名的param
		param.put("companyCode", companyCode);
		param.put("sourceType", sourceType);
		param.put("batchNum", batchNum);
		param.put("subDate", subDate);
		param.put("pointsSum", pointsSum);
		param.put("pointListSize", pointListSize);
		//param.put("zpointList", zpointList);//zpointList 不参与数字签名
		String secret_key="pa$wlt!52897"; //商城提供
		 //获取数字签名
		String signValue=getSignkey(secret_key,param);
		param.put("zpointList", zpointList);//zpointList 参与Base64加密
		//转化为json对象
		JSONObject jsonobj=JSONObject.fromObject(param);
		//传入参数组成字串
		String strJson = jsonobj.toString();
		System.out.println("参数转化为json：strJson="+strJson);		
        
		//Base64加密参数
		Map<String, String> parameterMap = new HashMap<String, String>();
		//传入参数从字串变成byte
		byte[] arg=strJson.toString().getBytes();
		//传入参数进行编码
		String params = Base64.encode(arg);		
		
		System.out.println("加密后的参数params="+params);
		System.out.println("数字签名参数signValue="+signValue);

		//后台提交链接		
		System.out.println("后台提交链接url=http://url?params="+params+"&signValue="+signValue);
		
		//System.out.println("参数解密：unparams="+new String(Base64.decode(params)));
		
	}
	
	
	 public static String getSignkey(String secret_key,TreeMap<String, String> params) {
		  String sign;
		  StringBuffer signValues=new StringBuffer();
		  System.out.println("=============secret_key="+secret_key);
		  String md5secret_key=Md5Encrypt.md5(secret_key);
		  for(String signKey:params.keySet()){     
		    signValues.append(signKey).append("=").append(null==params.get(signKey)?params.get(signKey):params.get(signKey).trim()).append("&");
		   }
		   signValues.append(md5secret_key);
		   System.out.println("=============input param="+signValues.toString());
		   sign=Md5Encrypt.md5(signValues.toString());         
		   System.out.println("=============sign="+sign);
		  return sign;
		 }
	
	
		public static void main(String[] args) {
			mypointRecharge();
		}

}
