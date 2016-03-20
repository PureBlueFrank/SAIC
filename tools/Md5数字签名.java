  /**
     * 签名值    
     * @param secret_key
     * @param signParms
     * @return
     */
 private static String getSignkey(String secret_key,TreeMap<String, String> signParms) {
  String sign;
  StringBuffer signValues=new StringBuffer();
  System.out.println("=============secret_key="+secret_key);
  String md5secret_key=Md5Encrypt.md5(secret_key);
  for(String signKey:signParms.keySet()){     
    signValues.append(signKey).append("=").append(UtilValidate.isEmpty(signParms.get(signKey))?signParms.get(signKey):signParms.get(signKey).trim()).append("&");
   }
   signValues.append(md5secret_key);
   System.out.println("=============input param="+signValues.toString());
   sign=Md5Encrypt.md5(signValues.toString());         
   System.out.println("=============sign="+sign);
  return sign;
 }