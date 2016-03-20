package encode;

import org.apache.commons.codec.binary.Base64;  
import org.apache.commons.codec.digest.DigestUtils;    
/**  
 * @author lishijun
 * @since 2010-12-21 
 * @version $Revision$  
 */    
public class MD5Test   
{    
    //被加密的文字串    
    static final String TARGET = "changeme";    
      
    /* 
     * 不可逆算法  MD5 
     */  
    public static void Md5()  
    {  
        String str = DigestUtils.md5Hex(TARGET);  
        print("md5Hex:     "+str);  
    }  
    /* 
     * 不可逆算法  SHA1 
     */  
    public void Sha1()  
    {  
        String str = DigestUtils.shaHex(TARGET);  
        print("shaHex:     "+str);  
        str = DigestUtils.sha256Hex(TARGET);  
        print("sha256Hex:  "+str);  
        str = DigestUtils.sha384Hex(TARGET);  
        print("sha384Hex:  "+str);  
        str = DigestUtils.sha512Hex(TARGET);  
        print("sha512Hex:  "+str);  
    }  
      
    /* 
     * 可逆算法  BASE64 
     */  
    public static void Base64()  
    {  
        //加密  
        byte[] b = Base64.encodeBase64(TARGET.getBytes(), true);  
        String str = new String(b);  
        print("BASE64:     "+str);  
          
        //解密  
        byte[] b1 = Base64.decodeBase64(str);  
        print("解密之后内容为：  "+new String(b1));  
    }  
    public static void print(Object obj)  
    {  
        System.out.println(obj);  
    }  
    public static void main(String[] args){
    	Base64();
    }
}  