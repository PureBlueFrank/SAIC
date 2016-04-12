/*******************************************************************************
	* 系统名称   ： 博瑞思电子商务平台系统
	* 文件名     ： UtilAdvisoryType.java
 *  			 (C) Copyright brains-info Corporation 2011
 *               All Rights Reserved.
 * *****************************************************************************
 *    注意： 本内容仅限于博瑞思信息技术有限公司内部使用，禁止转发
 * *****************************************************************************/
/**
 * 
 */
package org.ofbiz.base.util;

/**
 * @author liangyx
 *
 */
public class UtilAdvisoryType {

	
	public static String advisoryTypeToName(String id){
		if(id.equals("002")){
			return "库存及配送";
		} else if(id.equals("003")){
			return "支付问题";
		} else if(id.equals("004")){
			return "发票及保修";
		} else if(id.equals("005")){
			return "促销及赠品";
		} else {
			return "商品质量";
		}
	}
}
