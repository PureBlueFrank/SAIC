/*
 * SAIC eCommerce Platform
 * Copyright (c) 2015 International Business Machines
 * Copyright (c) 2015 SAIC
 */
package com.saic.interfaces.common.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;

import com.saic.interfaces.iom.exception.ExceptionTranslator;
import com.saic.interfaces.iom.exception.InterfaceOTOException;
import com.saic.interfaces.util.HandlerRequest;
import com.saic.interfaces.util.HandlerResponse;

/**
 * <p>
 * </p>
 * <p>
 * Created: 2015年12月29日 上午10:14:55
 * </p>
 * <p>
 * <table border="1" cellpaddding="2" cellspacing="2" >
 * <tr>
 * <td align="center" colspan="3">UPDATES</td>
 * </tr>
 * <tr>
 * <td align="center">DATE</td>
 * <td align="center">DEVELOPER</td>
 * <td align="center">CHANGES</td>
 * </tr>
 * </table>
 * </p>
 * 
 * @author xiangwanli
 */
public class OTOInterfaceService {

	public static final String resource = "InterfaceInfoLabels";

	public static final String resource_error = "InterfaceErrorLabels";

	public static final String module = OTOInterfaceService.class.getName();

	private static final String OTO_OPEN_API_CHARSET = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.http.charset", "UTF-8");
	
	private static final String OTO_OPEN_API_REQUEST_METHOD_HEADER = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.http.method.header", "application/json;charset=UTF-8");

	private static final String OTO_CALL_OPEN_API_LOG_LOCALE = UtilProperties
			.getPropertyValue("interface.properties", "oto.openapi.log.locale",
					"zh_CN");

	private static final Locale LOG_LOCALE = OTO_CALL_OPEN_API_LOG_LOCALE
			.equals("zh_CN") ? Locale.CHINESE : Locale.ENGLISH;

	private static final String OTO_OPEN_API_URL = UtilProperties
			.getPropertyValue("interface.properties", "oto.openapi.url",
					"http://openapi.sit.chexiang.com/services");

	private static final String OTO_OPEN_API_TIMESTAMP_FORMAT = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.timestamp.format", "yyyyMMddHHmmss");

	private static final String OTO_OPEN_API_APP_KEY = UtilProperties
			.getPropertyValue("interface.properties", "oto.openapi.appkey", "");

	private static final String OTO_OPEN_API_VERSION = UtilProperties
			.getPropertyValue("interface.properties", "oto.openapi.version",
					"1");

	private static final String OTO_OPEN_API_FORMAT = UtilProperties
			.getPropertyValue("interface.properties", "oto.openapi.format",
					"json");

	private static final String OTO_OPEN_API_SIGNATURE_METHOD = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.signatureMethod", "md5");

	private static final String OTO_OPEN_API_SECURITY_KEY = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.securitykey", "");

	private static final String OTO_OPEN_API_HEADER_PARAM_NAME_TIMESTAMP = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.param.timestamp", "timestamp");

	private static final String OTO_OPEN_API_HEADER_PARAM_NAME_APPKEY = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.param.appKey", "appKey");

	private static final String OTO_OPEN_API_HEADER_PARAM_NAME_VERSION = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.param.version", "version");

	private static final String OTO_OPEN_API_HEADER_PARAM_NAME_FORMAT = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.param.format", "format");

	private static final String OTO_OPEN_API_HEADER_PARAM_NAME_SIGNATURE_METHOD = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.param.signatureMethod", "signatureMethod");

	private static final String OTO_OPEN_API_HEADER_PARAM_NAME_SIGNATURE = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.param.signature", "signature");
	
	private static final String OTO_OPEN_API_HEADER_PARAM_NAME_URL_TARGET_SERVICE = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.param.url.targetservice", "targetService");
	
	private static final String OTO_OPEN_API_HEADER_PARAM_NAME_URL_TARGET_API = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.param.url.targetapi", "targetApi");

	@SuppressWarnings({ "unchecked", "deprecation" })
	public static Map<String, Object> callOpenApiOTO(DispatchContext ctx,
			Map<String, ?> context) throws InterfaceOTOException {
		final String methodName = "callOpenApiOTO(DispatchContext ctx, Map<String, ?> context)" ;
		Debug.logInfo(UtilProperties.getMessage(resource,
				"OTOInterfaceService.callOpenApiOTOStart", LOG_LOCALE), module);
		/*
		 * initialize return value
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		/*
		 * initialize interface API input parameters
		 */
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		DateFormat dFormatOTO = new SimpleDateFormat(
				OTO_OPEN_API_TIMESTAMP_FORMAT);
		String targetService = context.get(OTO_OPEN_API_HEADER_PARAM_NAME_URL_TARGET_SERVICE).toString();
		String targetApi = context.get(OTO_OPEN_API_HEADER_PARAM_NAME_URL_TARGET_API).toString();
		String originalBusinessData = context.get("data").toString();
		String businessData = originalBusinessData ;
		String timestamp = UtilValidate.isEmpty(context.get("timestamp")) ? dFormatOTO
				.format(currentTime) : context.get("timestamp").toString();
		String appKey = UtilValidate.isEmpty(context.get("appKey")) ? OTO_OPEN_API_APP_KEY
				: context.get("appKey").toString();
		String version = UtilValidate.isEmpty(context.get("version")) ? OTO_OPEN_API_VERSION
				: context.get("version").toString();
		String format = UtilValidate.isEmpty(context.get("format")) ? OTO_OPEN_API_FORMAT
				: context.get("format").toString();
		String signatureMethod = UtilValidate.isEmpty(context
				.get("signatureMethod")) ? OTO_OPEN_API_SIGNATURE_METHOD
				: context.get("signatureMethod").toString();
		String securityKey = UtilValidate.isEmpty(context.get("securityKey")) ? OTO_OPEN_API_SECURITY_KEY
				: context.get("securityKey").toString();

		/*
		 * build the original string to be encrypt.
		 */
		StringBuffer strSignatureBuff = new StringBuffer();
		strSignatureBuff.append(targetService);
		strSignatureBuff.append(targetApi);
		strSignatureBuff.append(businessData);
		strSignatureBuff.append("appKey=").append(appKey);
		strSignatureBuff.append("format=").append(format);
		strSignatureBuff.append("timestamp=").append(timestamp);
		strSignatureBuff.append("signatureMethod=").append(signatureMethod);
		strSignatureBuff.append("version=").append(version);
		strSignatureBuff.append(securityKey);
		/*
		 * build the signature value.
		 */
		Debug.logInfo(UtilProperties.getMessage(resource,
				"OTOInterfaceService.callOpenApiOTO.MD5.originalStr", UtilMisc.toList(strSignatureBuff.toString()),
				LOG_LOCALE), module);
		String signature = UtilValidate.isEmpty(context.get("signature")) ? DigestUtils
				.md5Hex(strSignatureBuff.toString()).toUpperCase() : context
				.get("signature").toString();
		Debug.logInfo(UtilProperties.getMessage(resource,
				"OTOInterfaceService.callOpenApiOTO.MD5.signatureStr", UtilMisc.toList(signature),
				LOG_LOCALE), module);

		/*
		 * initialize the request header parameters
		 */
		Map<String, String> aHeaderParamsMap = FastMap.newInstance();
		aHeaderParamsMap.put(OTO_OPEN_API_HEADER_PARAM_NAME_APPKEY, appKey);
		aHeaderParamsMap.put(OTO_OPEN_API_HEADER_PARAM_NAME_VERSION, version);
		aHeaderParamsMap.put(OTO_OPEN_API_HEADER_PARAM_NAME_FORMAT, format);
		aHeaderParamsMap.put(OTO_OPEN_API_HEADER_PARAM_NAME_TIMESTAMP,
				timestamp);
		aHeaderParamsMap.put(OTO_OPEN_API_HEADER_PARAM_NAME_SIGNATURE_METHOD,
				signatureMethod);
		aHeaderParamsMap.put(OTO_OPEN_API_HEADER_PARAM_NAME_SIGNATURE,
				signature);

		/*
		 * initialize the request and response object
		 */
		String strUrl = OTO_OPEN_API_URL + "/" + targetService + "/"
				+ targetApi;
		HandlerRequest reqHandler = new HandlerRequest();
		HandlerResponse resHandler = new HandlerResponse();
		reqHandler.setCharset(OTO_OPEN_API_CHARSET);
		reqHandler.setGateUrl(strUrl);

		/*
		 * set header parameters
		 */
		for (Map.Entry<String, String> entry : aHeaderParamsMap.entrySet()) {
			if (UtilValidate.isNotEmpty(entry.getKey())) {
				if (UtilValidate.isNotEmpty(entry.getValue())) {
					reqHandler.setParameterHeader(entry.getKey(), entry
							.getValue().toString());
				} else {
					reqHandler.setParameterHeader(entry.getKey(), "");
				}
			}
		}
		
		try {
			Debug.logInfo(UtilProperties.getMessage(resource,
					"OTOInterfaceService.callOpenApiOTO.URL", UtilMisc.toList(reqHandler.getRequestURL()),
					LOG_LOCALE), module);
			Debug.logInfo(UtilProperties.getMessage(resource,
					"OTOInterfaceService.callOpenApiOTO.HeaderParams", UtilMisc.toList(reqHandler.getAllParametersHeader().toString()),
					LOG_LOCALE), module);
			Debug.logInfo(UtilProperties.getMessage(resource,
					"OTOInterfaceService.callOpenApiOTO.BusinessParams", UtilMisc.toList(originalBusinessData),
					LOG_LOCALE), module);
			/*
			 * http call
			 */
			HttpClient httpclient = new HttpClient();
			PostMethod postMethod = new PostMethod(reqHandler.getRequestURL());
			postMethod.setRequestHeader("Content-Type", OTO_OPEN_API_REQUEST_METHOD_HEADER);
			postMethod.setRequestBody(businessData);
			Map<String, String> headParams = reqHandler.getAllParametersHeader();
			for (String key : headParams.keySet()) {
				postMethod.setRequestHeader(key, headParams.get(key));
			}
			int responseCode = httpclient.executeMethod(postMethod);
			if (responseCode == HttpURLConnection.HTTP_OK) {
				byte[] byteRes = postMethod.getResponseBody();
				String strRes = new String(byteRes);
				if (UtilValidate.isNotEmpty(strRes)) {
					Debug.logInfo(UtilProperties.getMessage(resource,
							"OTOInterfaceService.callOpenApiOTO.Return", UtilMisc.toList(strRes), LOG_LOCALE), module);
//					resHandler.setContent(strRes);
					JSONObject jsonReturn = JSONObject.fromObject(strRes);
					if(UtilValidate.isNotEmpty(jsonReturn)){
						if(UtilValidate.isNotEmpty(jsonReturn.get("errorCode"))
								&& "0".equals(jsonReturn.get("errorCode").toString())){
							// successful case
							if (UtilValidate.isNotEmpty(jsonReturn.get("result"))) {
								result.put("result", jsonReturn.get("result").toString());
							} else {
								result.put("result", strRes);
							}
						} else {
							// error case
							throw new InterfaceOTOException(jsonReturn.toString());
						}
					} else {
						throw new InterfaceOTOException("Null Return!");
					}
					
				} else {
					result.put("errorCode", "return null!");
					result.put("errorMsg", "return null!");
				}
			} else {
				result.put("errorCode", "http call failed!");
				result.put("errorMsg", "http call failed!");
			}
		} catch (Exception e) {
			Debug.logError(e, UtilProperties.getMessage(
					resource_error,
					"OTOInterfaceServiceCallOpenApiOTOError",
					UtilMisc.toList(UtilMisc.toMap("URL", strUrl, "params",
							aHeaderParamsMap).toString()), LOG_LOCALE), module);
			ExceptionTranslator.translate(e, module, methodName);
		}
		
		Debug.logInfo(UtilProperties.getMessage(resource,
				"OTOInterfaceService.callOpenApiOTOEnd", LOG_LOCALE), module);
		return result;
	}
}
