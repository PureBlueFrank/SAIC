/**
 *__________________________________________________________
 * Licensed Materials - Property of IBM
 *
 * (C) Copyright IBM Corp. 2013  All Rights Reserved.
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 * _________________________________________________________
 */


package org.ofbiz.party.tool;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * Mlink下行请求java示例 <br>
 * <Ul>
 * <Li>本示例定义几种下行请求消息的使用方法</Li>
 * <Li>本示例支持 jre1.5 或以上版本</Li>
 * <Li>本示例依赖于 commons-codec，commons-httpclient，commons-logging等几个jar包</Li>
 * </Ul>
 * @author carlee
 * @since 1.6
 */
public class SmsSimpleClient {

    public static final String module = SmsSimpleClient.class.getName();
	
    private static String shUrl="http://121.101.221.34:8888/sms.aspx";
    private static String shAction="send";
    private static String shUserid="1972";
    private static String shAccount="jkcs89";
    private static String shPassword="saic59161000";
    private static String shSign="【上汽通用汽车】";
	
	static{
		shUrl=UtilProperties.getPropertyValue("general", "sys.sms.shUrl");
		shAction=UtilProperties.getPropertyValue("general", "sys.sms.shAction");
		shUserid=UtilProperties.getPropertyValue("general", "sys.sms.shUserid");
		shAccount=UtilProperties.getPropertyValue("general", "sys.sms.shAccount");
		shPassword=UtilProperties.getPropertyValue("general", "sys.sms.shPassword");
		shSign=UtilProperties.getPropertyValue("general", "sys.sms.shSign");
	}
   
	/**
	 * 发送短信，先检查发送短信开发是否打开
	 * @param delegator
	 * @param mobileNo 手机号码
	 * @param content 短信内容
	 */
	public static void sendSms(Delegator delegator, Map<String, Object> context){
		String jobName = (String) context.get("jobName");
		String mobileNo = (String) context.get("mobileNo");
		String content = (String) context.get("content");
		String logContent = (String) context.get("logContent");
		
		if(UtilValidate.isNotEmpty(logContent)){
		 logContent=logContent+" "+shSign;//添加签名   
		}
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = "system";
		if(UtilValidate.isNotEmpty(userLogin)){
			userLoginId = userLogin.getString("userLoginId");
		}
		try {
			GenericValue jobMsg = delegator.makeValue("JobMsg");

			jobMsg.put("jobName", jobName);
			jobMsg.put("phoneNumber", mobileNo);
			jobMsg.put("content", logContent);
			jobMsg.put("oriContent", content);
			jobMsg.put("createTime", UtilDateTime.nowTimestamp());
			jobMsg.put("createdBy", userLoginId);
			
			GenericValue configValue = delegator.findOne("WebSiteConfig", true, "webSiteConfigId","1000");
		    if(!"N".equals(configValue.getString("smsEnable"))){
				Map<String, String> result = sendSingleMt(mobileNo, content);
				jobMsg.put("result", "已调用短信服务");
				jobMsg.put("mtmsgid", result.get("errMsg"));
				jobMsg.put("mtstat", result.get("errTag"));
				jobMsg.put("mterrcode", result.get("errCode"));
			} else {
				Debug.logInfo("系统配置短信不发送", module);				
				jobMsg.put("result", "系统关闭短信功能");
			}
			try {
				jobMsg = delegator.createSetNextSeqId(jobMsg);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
		} catch (Exception e) {
			Debug.logError(e, "短信发送失败！", module);
		}
	}


	 /**
     * 单条下行实例
     * @throws Exception
     */
    public static Map<String, String> sendSingleMt(String mobileNo,String content) throws Exception {
    	String scheduleDate=UtilDateTime.getDateTimeToString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss"); //计划发送时间（可以传以前日期如2010-1-1,立即发送)；    	
        //组成url字符串
        StringBuilder smsUrl = new StringBuilder(); 
        content=content+" "+shSign;//添加签名        
        smsUrl.append(shUrl); 
        //ID:1972 用户名：jkcs89 密码：saic59161000
        //ID:1979 用户名：chev 密码：59161000  
        Debug.logInfo(smsUrl.toString(), module);
        HashMap<String,String> contentMap=new HashMap<String,String>();
        contentMap.put("action", shAction);
        contentMap.put("userid", shUserid);
        contentMap.put("account", shAccount);
        contentMap.put("password", shPassword);
        contentMap.put("mobile", mobileNo);  
        contentMap.put("content", content);
        contentMap.put("sendTime", scheduleDate);
        
        String resStr = doPostRequest(smsUrl.toString(),contentMap);
        Debug.logInfo("sms:xmlrst::"+resStr, module);        
        //解析响应字符串
        HashMap<String,String> pp = parseResStr(resStr);       
        Debug.logInfo("sms:errTag::"+pp.get("errTag"), module);
        Debug.logInfo("sms:errCode::"+pp.get("errCode"), module);
        Debug.logInfo("sms:errMsg::"+pp.get("errMsg"), module);
        return pp;
    }
    
    /**
     * 单条下行实例
     * @throws Exception
     */
    public static Map<String, String> sendSingleMt2(String mobileNo,String content) throws Exception {
    	String scheduleDate=UtilDateTime.getDateTimeToString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd HH:mm:ss"); //计划发送时间（可以传以前日期如2010-1-1,立即发送)；    	
        //组成url字符串
        StringBuilder smsUrl = new StringBuilder(); 
        smsUrl.append("http://121.101.221.34:8888/sms.aspx");      
       // http://121.101.221.34:8888/sms.aspx
       // ?action=send&userid=12&account=账号&password=密码&mobile=15023239810,13527576163&content=内容&sendTime=时间
        //ID:1972 用户名：jkcs89 密码：saic59161000
        //ID:1979 用户名：chev 密码：59161000  
        Debug.logInfo(smsUrl.toString(), module);
        //发送http请求，并接收http响应
        //String resStr = doGetRequest(smsUrl.toString());
        HashMap<String,String> contentMap=new HashMap<String,String>();
        contentMap.put("action", "send");
        contentMap.put("userid", "1972");
        contentMap.put("account", "jkcs89");
        contentMap.put("password", "saic59161000");
        contentMap.put("mobile", mobileNo);  
        contentMap.put("content", content);
        contentMap.put("sendTime", scheduleDate);
        
        String resStr = doPostRequest(smsUrl.toString(),contentMap);
        Debug.logInfo("sms:xmlrst::"+resStr, module);        
//        //解析响应字符串
//        HashMap<String,String> pp = parseResStr(resStr);       
//        Debug.logInfo("sms:errTag::"+pp.get("errTag"), module);
//        Debug.logInfo("sms:errCode::"+pp.get("errCode"), module);
//        Debug.logInfo("sms:errMsg::"+pp.get("errMsg"), module);
        return null;
    }

   
    
    /**
     * 将普通字符串转换成Hex编码字符串
     * 
     * @param dataCoding 编码格式，15表示GBK编码，8表示UnicodeBigUnmarked编码，0表示ISO8859-1编码
     * @param realStr 普通字符串
     * @return Hex编码字符串
     * @throws UnsupportedEncodingException 
     */
    public static String encodeHexStr(int dataCoding, String realStr) {
        String hexStr = null;
        if (realStr != null) {
            try {
                if (dataCoding == 15) {
                    hexStr = new String(Hex.encodeHex(realStr.getBytes("GBK")));
                } else if ((dataCoding & 0x0C) == 0x08) {
                    hexStr = new String(Hex.encodeHex(realStr.getBytes("UnicodeBigUnmarked")));
                } else {
                    hexStr = new String(Hex.encodeHex(realStr.getBytes("ISO8859-1")));
                }
            } catch (UnsupportedEncodingException e) {
            	Debug.logError(e, module);
            }
        }
        return hexStr;
    }
    
    /**
     * 将Hex编码字符串转换成普通字符串
     * 
     * @param dataCoding 反编码格式，15表示GBK编码，8表示UnicodeBigUnmarked编码，0表示ISO8859-1编码
     * @param hexStr Hex编码字符串
     * @return 普通字符串
     */
    public static String decodeHexStr(int dataCoding, String hexStr) {
        String realStr = null;
        try {
            if (hexStr != null) {
                if (dataCoding == 15) {
                    realStr = new String(Hex.decodeHex(hexStr.toCharArray()), "GBK");
                } else if ((dataCoding & 0x0C) == 0x08) {
                    realStr = new String(Hex.decodeHex(hexStr.toCharArray()), "UnicodeBigUnmarked");
                } else {
                    realStr = new String(Hex.decodeHex(hexStr.toCharArray()), "ISO8859-1");
                }
            }
        } catch (Exception e) {
        	Debug.logError(e, module);
        }
        
        return realStr;
    }

    /**
     * 发送http GET请求，并返回http响应字符串
     * 
     * @param urlstr 完整的请求url字符串
     * @return
     */
    public static String doGetRequest(String urlstr) {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setIntParameter("http.socket.timeout", 10000);
        client.getParams().setIntParameter("http.connection.timeout", 5000);
        
        HttpEntity entity = null;
        String entityContent = null;
        try {
            HttpGet httpGet = new HttpGet(urlstr.toString());

            HttpResponse httpResponse = client.execute(httpGet);
            entityContent = EntityUtils.toString(httpResponse.getEntity());
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (Exception e) {
                	Debug.logError(e, module);
                }
            }
        }
        return entityContent;
    }

    /**
     * 发送http POST请求，并返回http响应字符串
     * 
     * @param urlstr 完整的请求url字符串
     * @return
     */
    public static String doPostRequest(String urlstr, HashMap<String,String> content) {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setIntParameter("http.socket.timeout", 10000);
        client.getParams().setIntParameter("http.connection.timeout", 5000);        
        List<NameValuePair> ls = new ArrayList<NameValuePair>();
        for(String key:content.keySet()){
        	NameValuePair param = new BasicNameValuePair(key, content.get(key));
        	 ls.add(param);
        }        
        HttpEntity entity = null;
        String entityContent = null;
        try {
            HttpPost httpPost = new HttpPost(urlstr.toString());
            UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(ls,"UTF-8");
            httpPost.setEntity(uefe);

            HttpResponse httpResponse = client.execute(httpPost);
            entityContent = EntityUtils.toString(httpResponse.getEntity());
            
        } catch (Exception e) {
        	Debug.logError(e, module);
        } finally {
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (Exception e) {
                	Debug.logError(e, module);
                }
            }
        }
        return entityContent;
    }
    
    
    /**
     * 将 短信下行 请求响应字符串解析到一个HashMap中
     * @param resStr
     * @return 
     * errTag:OK/ERROR
     * errCode:MessageID
	 * errMsg:MessageID
     */
    public static HashMap<String,String> parseResStr(String xmlString) {

    	 HashMap<String,String> pp = new HashMap<String,String>();    	
    	 if(UtilValidate.isEmpty(xmlString)){
    		return pp;
    	 }
    	// Debug.logInfo(xmlString, module);
    	 String returnstatus=getXmlValue(xmlString,"returnstatus");
    	 String message=getXmlValue(xmlString,"message");
    	 //String remainpoint=getXmlValue(xmlString,"remainpoint");
    	 //String taskID=getXmlValue(xmlString,"taskID");
    	 //String successCounts=getXmlValue(xmlString,"successCounts");
         try {
        	if(null == returnstatus){
        		return pp;
        	}
        	pp.put("errTag", returnstatus);
        	pp.put("errCode", returnstatus);
        	pp.put("errMsg", message);
           
         } catch (Exception e) {
         	Debug.logError(e, module);
         }
        return pp;
    }
    
    
    /**
     * 启动测试
     * @param args
     */
    public static void main(String[] args) {
    	try {
          //测试单条下行
        	//SmsSimpleClient.sendSingleMt("13861305037","雪佛兰短信测试,短信测试#￥%*（");
        	//SmsSimpleClient.sendSingleMt2("15062261512","雪佛兰短信测试,短信测试#￥%*（");
        	SmsSimpleClient.sendSingleMt2("15062261512","您尾号：2059 的安悦卡账户2014年01月12日17:00充值500.0元成功。");
    		//18616676307 13817949170
        	//SmsSimpleClient.sendSingleMt("13861305037","您尾号：2058 的安悦卡账户2014年01月12日17:00充值500.0元成功。");
        	//SmsSimpleClient.sendSingleMt("18168017761","您尾号：2059 的安悦卡账户2014年01月12日17:00充值500.0元成功。");
        	//SmsSimpleClient.sendSingleMt2("13817949170","您尾号：2059 的安悦卡账户2013年12月19日17:00充值500.0元成功。【安悦e生活】");
        
        } catch (Exception e) {
        	Debug.logError(e, module);
        }
    	
//    	String xmlString="<?xml version=\"1.0\" encoding=\"utf-8\" ?><returnsms>"+
//						 "<returnstatus>Success</returnstatus>"+
//						 "<message>ok</message>"+
//						 "<remainpoint>9993</remainpoint>"+
//						 "<taskID>908432</taskID>"+
//						 "<successCounts>1</successCounts></returnsms>";
//        System.out.println("returnstatus="+getXmlValue(xmlString,"returnstatus")); 
//        System.out.println("message="+getXmlValue(xmlString,"message")); 
//        System.out.println("remainpoint="+getXmlValue(xmlString,"remainpoint")); 
//        System.out.println("taskID="+getXmlValue(xmlString,"taskID")); 
//        System.out.println("successCounts="+getXmlValue(xmlString,"successCounts")); 
    }
    
    public static String getXmlValue(String xmlString,String xmltag) {
   	  String xmlValue=null;    	
   	  if(UtilValidate.isEmpty(xmlString)||UtilValidate.isEmpty(xmltag)){
   		return xmlValue;
   	  }    	 
   	  Pattern p = Pattern.compile("<"+xmltag+"[^>]*?((>.*?</"+xmltag+">)|(/>))");
      Matcher m = p.matcher(xmlString);
      String resStr=m.find()?m.group():""; 
      resStr =  resStr.replaceAll("<"+xmltag+"[^>]*?((>)|(/>))|</"+xmltag+">|\\[|\\]", ""); 
       if(UtilValidate.isNotEmpty(resStr)){
        	xmlValue=resStr.trim();
        }        
        return xmlValue;     
    }
    
    
}
