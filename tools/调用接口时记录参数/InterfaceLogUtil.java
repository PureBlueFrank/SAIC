package com.saic.interfaces.util;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class InterfaceLogUtil {
	
	public static final String module = InterfaceLogUtil.class.getName();
	/**
	 * 记录日志
	 * @param delegator 
	 * @param logTypeId 日志类型
	 * @param objId 实体对象ID
	 * @param url 子站url
	 * @param currentServiceName 当前服务名称
	 * @param params 参数
	 * @param signValue 数字签名
	 * @param statusId 结果状态
	 * @param retryFlg 是否可以重试
	 * @param resultMessage 返回结果
	 * @param costTime 接口调用执行时间
	 */
	public static void log(Delegator delegator,String logTypeId,String objId,String url,String currentServiceName,String params,String signValue,
			String statusId,String retryFlg,String resultMessage,long costTime){
    	try {
    		GenericValue logType = delegator.findOne("InterfaceCxLogType", true, "logTypeId", logTypeId);
    		if(logType == null || "N".equals(logType.getString("saveLog"))){
    			return;
    		}
			GenericValue logValue = delegator.makeValue("InterfaceCxLog");
			String logId = delegator.getNextSeqId("InterfaceCxLog");
	    	logValue.put("logId", logId);
	    	logValue.put("logTypeId", logTypeId);
	    	logValue.put("refObjectId", objId);
	    	logValue.put("callUrl", url);
	    	logValue.put("serviceName", currentServiceName);
	    	logValue.put("params", params);
	    	logValue.put("signValue", signValue);
	    	if("INTERFACE_SUCCESS".equals(statusId)){
	    		logValue.put("statusId", "INTERFACE_SUCCESS");
	    	} else {
	    		logValue.put("statusId", "INTERFACE_FAILURE");
	    	}
	    	logValue.put("retryFlg", retryFlg);
	    	logValue.put("resultMessage", resultMessage);
	    	logValue.put("createdTime", UtilDateTime.nowTimestamp());
	    	logValue.put("costTime", costTime);
//	        GenericValue logParamValue = delegator.makeValue("InterfaceCxLogParam");
//	        logParamValue.put("logParamId", delegator.getNextSeqId("InterfaceCxLogParam"));
//	        logParamValue.put("logId", logId);
//	        logParamValue.put("paramName", "serviceContext");
//	    	logParamValue.put("content", serviceContext);
			logValue.create();
//			logParamValue.create();
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}
	}
	
	public static Map<String, Object> interfaceLog(DispatchContext ctx, Map<String, ?> context){	
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = dispatcher.getDelegator();
		Map<String,Object> result =  ServiceUtil.returnSuccess();
		String logTypeId = (String) context.get("logTypeId");
		String refObjectId = (String) context.get("refObjectId");
		String callUrl = (String) context.get("callUrl");
		String serviceName = (String) context.get("serviceName");
		String params = (String) context.get("params");
		String signValue = (String) context.get("signValue");
		String statusId = (String) context.get("statusId");
		String retryFlg = (String) context.get("retryFlg");
		String resultMessage = (String) context.get("resultMessage");
		Long costTime = (Long) context.get("costTime");
		try {
    		GenericValue logType = delegator.findOne("InterfaceCxLogType", true, "logTypeId", logTypeId);
    		if(logType == null || "N".equals(logType.getString("saveLog"))){
    			return result;
    		}
			GenericValue logValue = delegator.makeValue("InterfaceCxLog");
			String logId = delegator.getNextSeqId("InterfaceCxLog");
	    	logValue.put("logId", logId);
	    	logValue.put("logTypeId", logTypeId);
	    	logValue.put("refObjectId", refObjectId);
	    	logValue.put("callUrl", callUrl);
	    	logValue.put("serviceName", serviceName);
	    	logValue.put("params", params);
	    	logValue.put("signValue", signValue);
	    	if("INTERFACE_SUCCESS".equals(statusId)){
	    		logValue.put("statusId", "INTERFACE_SUCCESS");
	    	} else {
	    		logValue.put("statusId", "INTERFACE_FAILURE");
	    	}
	    	logValue.put("retryFlg", retryFlg);
	    	logValue.put("resultMessage", resultMessage);
	    	logValue.put("createdTime", UtilDateTime.nowTimestamp());
	    	logValue.put("costTime", costTime);
			logValue.create();
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}
		return result;
	}
}
