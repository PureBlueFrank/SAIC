package com.saic.interfaces.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
/**
 * 转化的一些公共方法
 * @author liang.ruifeng
 *
 */
public class Transform {
	
	public static final String module = Transform.class.getName();
	
	/**
	 * json数据转化为相应map对象
	 * @param delegator
	 * @param json
	 * @return
	 */
	public static Map<String, Object> jsonToGenericMap(Delegator delegator,String json){
		Map<String, Object> returnMap = FastMap.newInstance();
		Map<String, Object> paramsMap = jsonToSingleMap(json);
		for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {  
			String entityName = entry.getKey();
			String entityJson = entry.getValue().toString().trim();
			entityJson = entityJson.substring(1, entityJson.length() - 1);
			Map<String, Object> entityMap = jsonToSingleMap(entityJson);
			GenericValue entity = delegator.makeValidValue(entityName, entityMap);
			returnMap.put(entityName, entity);
		}
		return returnMap;
	}
	
	/**
	 * json数据转化为相应List<GenericValue>对象
	 * @param delegator
	 * @param json
	 * @return
	 * @throws GeneralException 
	 */
	public static List<GenericValue> jsonToGenericList(Delegator delegator,String json) throws GeneralException{
		List<GenericValue> returnList = new ArrayList();
		if (UtilValidate.isEmpty(json)) {
			return returnList ;
		}
		JSONArray array = JSONArray.fromObject(json);
        for(int i = 0; i < array.size(); i++){
            JSONObject jsonObject = array.getJSONObject(i);
            Map<String, Object> map = Transform.jsonToSingleMap(jsonObject.toString());
            for (Map.Entry<String, Object> entry : map.entrySet()) {  
				String entityName = entry.getKey();
				String entityJson = entry.getValue().toString().trim();
				ModelEntity entityType = delegator.getModelEntity(entityName);
				if (UtilValidate.isNotEmpty(entityJson)
						&& entityJson.startsWith("[")
						&& entityJson.endsWith("]")) {
					JSONArray arrSub = JSONArray.fromObject(entityJson);
					for(int j = 0; j < arrSub.size(); j++){
						JSONObject subObject = arrSub.getJSONObject(j);
						GenericValue entity = delegator.makeValidValue(entityName, Transform.jsonToSingleMapWithEntityType(entityType,subObject.toString()));
						returnList.add(entity);
					}
				} else {
					entityJson = entityJson.substring(1, entityJson.length() - 1);
			    	GenericValue entity = delegator.makeValidValue(entityName, Transform.jsonToSingleMapWithEntityType(entityType,entityJson));
					returnList.add(entity);
				}
		    }
        }
		return returnList;
	}
	
	/**
	 * json数据转化为相应List<Map<String, Object>>对象
	 * @param json
	 * @return
	 */
	public static List<Map<String, Object>> jsonToMapList(String json){
		List<Map<String, Object>> mapList = new ArrayList();
		JSONArray array = JSONArray.fromObject(json);
        for(int i = 0; i < array.size(); i++){     
            JSONObject jsonObject = array.getJSONObject(i);
            Map<String, Object> map = Transform.jsonToSingleMap(jsonObject.toString());
            mapList.add(map);
        }
		return mapList;
	}
	
	/**
	 * json数据转化为相应List<String>对象
	 * @param json
	 * @return
	 */
	public static List<String> jsonToStringList(String json){
		List<String> stringList = new ArrayList();
		JSONArray array = JSONArray.fromObject(json);
        for(int i = 0; i < array.size(); i++){  
            JSONObject jsonObject = array.getJSONObject(i);
            Map<String, Object> map = Transform.jsonToSingleMap(jsonObject.toString());
            for (Map.Entry<String, Object> entry : map.entrySet()) {  
            	stringList.add(entry.getValue().toString().trim());
		    }
            
        }
		return stringList;
	}
	
	/**
	 * json转化为map
	 * @param json
	 * @return
	 */
	public static Map<String,Object> jsonToSingleMap(String json){
    	Map<String,Object> result = FastMap.newInstance();
    	if (UtilValidate.isNotEmpty(json)) {
    		try {
		    	JSONObject jsonObj = JSONObject.fromObject(json);
				Iterator<String> nameItr = jsonObj.keys();
				String name;
				while (nameItr.hasNext()) {
					name = nameItr.next();
					result.put(name, jsonObj.get(name));
				}
    		} catch (Exception e){
    			Debug.logError("解析JSON字符串错误。", module);
    		}
    	}
		return result;
    }
	
	/**
	 * json转化为map
	 * @param json
	 * @return
	 * @throws GeneralException 
	 */
	public static Map<String,Object> jsonToSingleMapWithEntityType(ModelEntity entity,String json) throws GeneralException{
    	Map<String,Object> result = FastMap.newInstance();
    	if (UtilValidate.isEmpty(json)) {
    		return result;
    	}
    	JSONObject jsonObj = JSONObject.fromObject(json);
		Iterator<String> nameItr = jsonObj.keys();
		String name;
		while (nameItr.hasNext()) {
			name = nameItr.next();
			try {
				Collection<String> field = new ArrayList<String>();
				field.add(name);
				ModelField rhsField = entity.getField(name);
				if(!entity.areFields(field)){
					result.put(name, jsonObj.get(name).toString());
				}else if(rhsField.getType().equals("date-time")){
					result.put(name, Timestamp.valueOf(jsonObj.get(name).toString()));
				}else if(rhsField.getType().equals("fixed-point")){
					result.put(name, new BigDecimal(jsonObj.get(name).toString()));
				}else if(rhsField.getType().equals("numeric")){
					result.put(name, Long.valueOf(jsonObj.get(name).toString()).longValue());
				}else if(rhsField.getType().equals("currency-precise") || rhsField.getType().equals("currency-amount")){
					BigDecimal bigDecimal = new BigDecimal(jsonObj.get(name).toString());
					result.put(name, bigDecimal);
				}else{
					result.put(name, jsonObj.get(name).toString());
				}
			} catch (Exception e) {
				Debug.logError(name + "字段转化异常" + "，字段值为" + 
			(UtilValidate.isNotEmpty(jsonObj.get(name)) ? jsonObj.get(name).toString() : "空"), module);
				throw new GeneralException(name + "字段转化异常" + "，字段值为" + 
						(UtilValidate.isNotEmpty(jsonObj.get(name)) ? jsonObj.get(name).toString() : "空") , e);
			}
			
		}	
		return result;
    }
	
	/**
	 * 字节转String
	 * @param digest
	 * @return
	 */
	public static String bytetoString(byte[] digest) {
		String str = "";
		String tempStr = "";
		for (int i = 0; i < digest.length; i++) {
			tempStr = (Integer.toHexString(digest[i] & 0xff));
			if (tempStr.length() == 1) {
				str = str + "0" + tempStr;
			} else {
				str = str + tempStr;
			}
		}
		return str.toUpperCase();
	}
}
