/*******************************************************************************
	* 系统名称   ： 博瑞思电子商务平台系统
	* 文件名     ： Md5Encrypt.java
 *  			 (C) Copyright brains-info Corporation 2011
 *               All Rights Reserved.
 * *****************************************************************************
 *    注意： 本内容仅限于博瑞思信息技术有限公司内部使用，禁止转发
 * *****************************************************************************/


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密算法
 */
public class Md5Encrypt {
	/**
	 * Used building output as Hex
	 */
	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 对字符串进行MD5加密
	 * 
	 * @param text
	 *            明文
	 * 
	 * @return 密文
	 */
	public static String md5(String text) {
		MessageDigest msgDigest = null;

		try {
			msgDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(
					"System doesn't support MD5 algorithm.");
		}

		try {
			msgDigest.update(text.getBytes("utf-8"));

		} catch (UnsupportedEncodingException e) {

			throw new IllegalStateException(
					"System doesn't support your  EncodingException.");

		}

		byte[] bytes = msgDigest.digest();

		String md5Str = new String(encodeHex(bytes));

		return md5Str;
	}

	public static char[] encodeHex(byte[] data) {

		int l = data.length;

		char[] out = new char[l << 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}

		return out;
	}
	  /**  

     *   

     * @param text  

     *            明文  

     * @return 32位密文  

     */ 

    public static String encryption(String text) {  

        String re_md5 = new String();  

        try {  

            MessageDigest md = MessageDigest.getInstance("MD5");  

            md.update(text.getBytes());  

            byte b[] = md.digest();  

   

            int i;  

   

            StringBuffer buf = new StringBuffer("");  

            for (int offset = 0; offset < b.length; offset++) {  

                i = b[offset];  

                if (i < 0)  

                    i += 256;  

                if (i < 16)  

                    buf.append("0");  

                buf.append(Integer.toHexString(i));  

            }  

   

            re_md5 = buf.toString();  

   

        } catch (NoSuchAlgorithmException e) {  

            e.printStackTrace();  

        }  

        return re_md5;  

    }  


}
