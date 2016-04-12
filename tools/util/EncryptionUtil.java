/*******************************************************************************
	* 系统名称   ： 博瑞思电子商务平台系统
	* 文件名     ： OrderReadHelper.java
 *  			 (C) Copyright brains-info Corporation 2011
 *               All Rights Reserved.
 * *****************************************************************************
 *    注意： 本内容仅限于博瑞思信息技术有限公司内部使用，禁止转发
 * *****************************************************************************/
package org.ofbiz.base.util;

import org.ofbiz.base.crypto.HashCrypt;

/**
 * 加密工具类，用于对参数按照密钥进行加密
 * @author chengj
 *
 */
public class EncryptionUtil {

	/**
	 * 用加密算法对参数进行加密，返回加密后的字符串
	 * @param params
	 * @param key
	 */
	public static String encryption(String params,String key){
		return HashCrypt.getDigestHash(params+"|"+key, getHashType());
	}

    public static String getHashType() {
//        String hashType = UtilProperties.getPropertyValue("security.properties", "password.encrypt.hash.type");

//        if (UtilValidate.isEmpty(hashType)) {
    	  String  hashType = "SHA";
//        }

        return hashType;
    }
    
    /**
     * 以安全的格式显示邮箱地址，隐藏中间几位比如g*****o@gmail.com
     * @param email
     * @return
     */
    public static String securityDisplayEmail(String email){
    	if(!UtilValidate.isEmail(email)){
    		return email;
    	}
    	int atIndex = email.indexOf('@');
    	if(atIndex!=-1){
    		String firstChar = email.substring(0, 1);
    		String lastChar = email.substring(atIndex-2,atIndex-1);
    		String suffix = email.substring(atIndex,email.length());
    		email = firstChar + "*****" + lastChar + suffix;
    	}
    	return email;
    }
    
    /**
     * 以安全的格式显示手机号码，隐藏中间几位比如151*****728
     * @param email
     * @return
     */
    public static String securityDisplayPhoneNumber(String phoneNumber){
    	if(!UtilValidate.isChinesePhoneNumber(phoneNumber)){
    		return phoneNumber;
    	}
		String prefixStr = phoneNumber.substring(0, 3);
		String suffixStr = phoneNumber.substring(8,11);
		phoneNumber = prefixStr + "*****" + suffixStr;
    	return phoneNumber;
    }
    
    /**
     * 以安全的格式显示会员卡号，隐藏中间几位比如15*****28
     * @param cardNum
     * @return
     */
    public static String securityDisplayCardNum(String cardNum){
    	if(UtilValidate.isEmpty(cardNum)){
    		return cardNum;
    	}
    	if(cardNum.length() > 2){
    		String prefixStr = cardNum.substring(0, 2);
    		String suffixStr = cardNum.substring(cardNum.length()-2,cardNum.length());
    		cardNum = prefixStr + "*****" + suffixStr;
    	}
    	return cardNum;
    }
}
