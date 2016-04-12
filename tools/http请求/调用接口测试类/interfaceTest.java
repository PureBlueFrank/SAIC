package com.saic.interfaces.test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.ofbiz.base.crypto.Md5Encrypt;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;

import com.saic.interfaces.util.Base64;


public class interfaceTest {

	private static String STR_BRAND_SERVICE = "{    \"syncObjectId\": \"cadi\",    \"subDate\": \"20160123161612\",    \"freatureValueList\": [        {            \"ProductFeatureCategoryAppl\": []        }    ],    \"objectValueList\": [        {            \"ProdCatalogCategory\": [                {                    \"fromDate\": \"2016-01-15 12:00:00\",                    \"prodCatalogCategoryTypeId\": \"PCCT_BROWSE_ROOT\",                    \"prodCatalogId\": \"cadi\",                    \"productCategoryId\": \"cadi_root\"                }            ]        },        {            \"ProductCategoryRollup\": [                {                    \"fromDate\": \"2016-01-23 16:16:12\",                    \"parentProductCategoryId\": \"cadi_root\",                    \"productCategoryId\": \"1008351\",                    \"sequenceNum\": \"1\"                },                {                    \"fromDate\": \"2016-01-23 16:16:12\",                    \"parentProductCategoryId\": \"cadi_root\",                    \"productCategoryId\": \"1008350\",                    \"sequenceNum\": \"1\"                },                {                    \"fromDate\": \"2016-01-23 16:16:12\",                    \"parentProductCategoryId\": \"cadi_root\",                    \"productCategoryId\": \"1008344\",                    \"sequenceNum\": \"1\"                },                {                    \"fromDate\": \"2016-01-23 16:16:12\",                    \"parentProductCategoryId\": \"cadi_root\",                    \"productCategoryId\": \"1008343\",                    \"sequenceNum\": \"1\"                }            ]        },        {            \"ProductCategory\": [                {                    \"categoryName\": \"凯迪椅子\",                    \"primaryParentCategoryId\": \"1008350\",                    \"productCategoryId\": \"1008351\",                    \"productCategoryTypeId\": \"CATALOG_CATEGORY\"                },                {                    \"categoryName\": \"凯迪家具\",                    \"primaryParentCategoryId\": \"1008344\",                    \"productCategoryId\": \"1008350\",                    \"productCategoryTypeId\": \"CATALOG_CATEGORY\"                },                {                    \"categoryName\": \"食品\",                    \"primaryParentCategoryId\": \"1008343\",                    \"productCategoryId\": \"1008344\",                    \"productCategoryTypeId\": \"CATALOG_CATEGORY\"                },                {                    \"categoryName\": \"凯迪商城\",                    \"primaryParentCategoryId\": \"S000001\",                    \"productCategoryId\": \"1008343\",                    \"productCategoryTypeId\": \"CATALOG_CATEGORY\"                }            ]        }    ],    \"companyCode\": \"anyolife\"}";
	
	private static String DEV_TEST_SERVER_URI = "http://116.246.11.214:7171" ;
	
	private static String LOCAL_SERVER_URI = "http://localhost:7070" ;
	
	public void testBrandService() throws Exception {
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(UtilDateTime.nowTimestamp());
		String chexiangToCadillac = "{companyCode:\"cadi\",brandId:\"10000\",brandName:\"ZIPPOtest\",brandEnName:\"ZIPPO1\",brandLogo:\"/images/products/additional/m9zpfnull_View.png\",brandLogoview:\"/images/products/additional/m9zpfnull_View.png\",brandDescribe:\"测试品牌同步\",brandDisabled:\"N\",subDate:";
		chexiangToCadillac = chexiangToCadillac + date + "}";
		Map<String, String> parameterMap = FastMap.newInstance();
		
		JSONObject jsonObject = JSONObject.fromObject(chexiangToCadillac);
		String strJson=jsonObject.toString();
		String inputparam="abcxyz"+strJson;		
		String signValue=bytetoString(DigestUtils.sha(inputparam.getBytes()));
		byte[] arg=strJson.toString().getBytes();
		String params = Base64.encode(arg);
		parameterMap.put("params", params);
		parameterMap.put("signValue", signValue);
		String ret = HttpRequest.postData(LOCAL_SERVER_URI + "/interface/control/chexiangBrandToCadillac", parameterMap, "UTF-8");
		System.out.println(ret);
	}
	//更改订单状态接口
	public void changeOrderStatus() throws Exception {
		String subDate = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-2);
		String subTransDate = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
		String chexiangCancleOrder = "{\"companyCode\":\"cadillac\",\"subOrderId\":\"160128250000157411\",\"mount\":\"1\","
				+ "\"statusId\":\"ORDER_APPROVED\",\"subDate\":";
		chexiangCancleOrder =  chexiangCancleOrder + "\"" + subDate + "\"" + ",";
		chexiangCancleOrder +="\"" + "subTransDate" + "\":" + "\"" + subTransDate + "\"" + "}";
		JSONObject jsonObject = JSONObject.fromObject(chexiangCancleOrder);
		String strJson = jsonObject.toString();
		String inputparam = "abcxyz" + strJson;
		String signValue=bytetoString(DigestUtils.sha(inputparam.getBytes()));
		Map<String, String> parameterMap = FastMap.newInstance();
		byte[] arg=strJson.toString().getBytes();
		String params = Base64.encode(arg);
		parameterMap.put("params", params);
		parameterMap.put("signValue", signValue);
		String ret = HttpRequest.postData("http://127.0.0.1:7070/interface/control/ChangeOrderStatusCX", parameterMap, "UTF-8");
		System.out.println(ret);
	}
	//接受退换货接口
	public void testAcceptReturn() throws Exception{
		//申请接口时间
		String subDate  = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String returnSopParams = "{\"companyCode\":\"卡迪拉克\",\"subOrderId\":\"OCD10003676\","
				+ "\"returnId\":\"1234567890\",\"mount\":\"150000\",\"statusId\":\"RETURN_ACCEPTED\",\"createdBy\":\"S000046\",\"mark\":\"同意退货\",\"subDate\":";
		returnSopParams = returnSopParams +"\""+subDate+"\"}";
		JSONObject jsonObject = JSONObject.fromObject(returnSopParams);
		String strJson = jsonObject.toString();
		String inputparam  = "abcxyz" + strJson;
		String signValue = bytetoString(DigestUtils.sha(inputparam.getBytes()));
		byte[] arg =strJson.toString().getBytes();
		String params = Base64.encode(arg);
		Map parameterMap = FastMap.newInstance();
		parameterMap.put("params", params);
		parameterMap.put("signValue", signValue);
		String ret = HttpRequest.postData("http://127.0.0.1:7070/interface/control/acceptReturnPrams", parameterMap, "UTF-8");
		System.out.println(ret);
	}
//	//供应商发货通知接口
//	public void testAcceptDeliveryInfo() throws Exception{
//		//申请接口时间
//		String subDate  = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
//		//订单发货时间<yyyyMMddHHmmss>
//		String subTransDate = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
//		String returnSopParams = "{\"companyCode\":\"cadillac\",\"subOrderId\":\"160216250000163711\","
//				+ "\"billNumber\":\"1234567\",\"logistCompanyId\":\"10032\",\"logisticsCompanyName\":\"测试发货信息\",\"subDate\":";
//		returnSopParams = returnSopParams +"\""+subDate+"\",\"subTransDate\":"+"\""+subTransDate+"\"}";
//		JSONObject jsonObject = JSONObject.fromObject(returnSopParams);
//		String strJson = jsonObject.toString();
//		String inputparam  = "abcxyz" + strJson;
//		String signValue = bytetoString(DigestUtils.sha(inputparam.getBytes()));
//		byte[] arg =strJson.toString().getBytes();
//		String params = Base64.encode(arg);
//		Map parameterMap = FastMap.newInstance();
//		parameterMap.put("params", params);
//		parameterMap.put("signValue", signValue);
//		String ret = HttpRequest.postData("http://116.246.11.214:7171/interface/control/acceptDeliveryInfo", parameterMap, "UTF-8");
//		System.out.println(ret);
//	}
	//供应商发货通知接口
	public void testAcceptDeliveryInfo() throws Exception{
		//申请接口时间
		String subDate  = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
		//订单发货时间<yyyyMMddHHmmss>
		String subTransDate = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
        Map<String, Object> param = new HashMap<String,Object>();
        param.put("companyCode", "cadillac");
        param.put("subOrderId", "160216250000163711");
        param.put("billNumber", "1234567");
        param.put("logistCompanyId", "10032");
        param.put("logisticsCompanyName", "测试发货信息");
        param.put("subTransDate", subTransDate);
        param.put("subDate", subDate);
		
		
		JSONObject jsonObject = JSONObject.fromObject(param);
		String strJson = jsonObject.toString();
		String inputparam  = "abcxyz" + strJson;
		String signValue = bytetoString(DigestUtils.sha(inputparam.getBytes()));
		byte[] arg =strJson.toString().getBytes();
		String params = Base64.encode(arg);
		Map parameterMap = FastMap.newInstance();
		parameterMap.put("params", params);
		parameterMap.put("signValue", signValue);
		String ret = HttpRequest.postData("http://116.246.11.214:7171/interface/control/acceptDeliveryInfo", parameterMap, "UTF-8");
		System.out.println(ret);
	}
	//批量取消订单接口			
	public void batchCancel() throws Exception {
		//批量订单
		Map param=new HashMap();
		String companyCode="cadi";
		//申请接口时间
		String subDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		//订单成交时间
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-2);
		String subTransDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
		List orderList=new ArrayList();
		Map order1=new HashMap();
		order1.put("subOrderId","160126250000156541");
		order1.put("subTransDate",subTransDate);
		order1.put("mount","110");
		order1.put("statusId","ORDER_APPROVED");
		orderList.add(order1);
		
		Map order2=new HashMap();
		order2.put("subOrderId","160126250000156591");
		order2.put("subTransDate",subTransDate);
		order2.put("mount","110");
		order2.put("statusId","ORDER_APPROVED");
		orderList.add(order2);
		
		Map order3=new HashMap();
		order3.put("subOrderId","160126250000156601");
		order3.put("subTransDate",subTransDate);
		order3.put("mount","110");
		order3.put("statusId","ORDER_APPROVED");
		orderList.add(order3);
		
		param.put("companyCode", companyCode);
		param.put("subDate", subDate);
		param.put("orderList", orderList);
		//转化为json对象
		JSONObject jsonobj=JSONObject.fromObject(param);
		//传入参数组成字串
		String strJson = jsonobj.toString();
		String inputparam = "abcxyz" + strJson;
		String signValue=bytetoString(DigestUtils.sha(inputparam.getBytes()));
		Map<String, String> parameterMap = FastMap.newInstance();
		//传入参数从字串变成byte
		byte[] arg=strJson.toString().getBytes();
		//传入参数进行编码
		String params = Base64.encode(arg);
		parameterMap.put("params", params);
		parameterMap.put("signValue", signValue);
		String ret = HttpRequest.postData("http://127.0.0.1:7070/interface/control/batchCancelOrder", parameterMap, "UTF-8");
		System.out.println(ret);
	}
	
	public void testProductOn() throws Exception {
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(UtilDateTime.nowTimestamp());
		String json = "{companyCode:\"anyolife\",subDate:\"";
		json = json + date + "\"";
		String product = "product:[{productId:\"20008\",productCode:\"2C01040Z000001\",productName:\"测试商品\",productTypeId:\"FINISHED_GOOD\",requireInventory:\"N\",returnable:\"N\",exchangeable:\"N\",salesDiscontinuationDate:\"2099-12-31 23:59:59\",smallImageUrl:\"/images/products/small/10002.jpg\",taxRate:\"17.000000\",billOfMaterialLevel:\"0\",brandName:\"10000\",chargeShipping:\"N\",createdByUserLogin:\"liangruifeng\",deliverRegionType:\"LOCAL\",detailImageUrl:\"/images/products/detail/10002.jpg\",geoJson:[{\"geoId\":\"CHN\",\"geoName\":\"%E5%85%A8%E5%9B%BD\"}],internalName:\"测试10002蔻诗弥妆后美肌休憩面膜\",introductionDate:\"2013-08-15 14:53:41.0\",isDaiXiao:\"Y\",isVariant:\"N\",isVirtual:\"N\",largeImageUrl:\"/images/products/large/10002.jpg\",	lastModifiedByUserLogin:\"liangruifeng\",lastModifiedDate:\"2013-08-28 15:13:11\",description:\"蔻诗弥妆后美肌休憩面膜针对萃取化妆的积分，使用了前所未有的：建立在花卉上的神经美容“方法，选择了有效和持久的天然活性成分来减缓长期化妆对肌肤造成的压力\",longDescription:\"testtest\",originGeoId:\"CN-31-SH\",originalImageUrl:\"/images/products/original/10002.jpg\",primaryProductCategoryId:\"2C0104\",prodDisabled:\"N\"}]";
		String productPriceList = "productPriceList:[{ProductPrice:[{productId:\"20003\",productPriceTypeId:\"DEFAULT_PRICE\",productPricePurposeId:\"PURCHASE\",currencyUomId:\"CNY\",productStoreGroupId:\"_NA_\",fromDate:\"2015-12-22 23:59:59\"}]}]";
//		String ProductCategoryMember = "ProductCategoryMember:[{productCategoryId:\"2C0104\",productId=\"20002\",fromDate=\"2013-08-15 14:53:41\"}]";
//		String ProductFeatureApplList = null;
		String SupplierProduct = "supplierProductList:[{SupplierProduct:[{availableFromDate:\"2013-06-01 00:00:00\",canDropShip:\"Y\",currencyUomId:\"CNY\",lastPrice:\"50.000\",minimumOrderQuantity:\"0.000000\",partyId:\"S10014\",productId:\"20003\",supplierPrefOrderId:\"10_MAIN_SUPPL\",supplierProductId:\"K003-B1\",supplierProductName:\"蔻诗弥妆后美肌休憩面膜 5片装\",cxSupplierId:\"10000\"}]}]";
		String objectValueList = "objectValueList:[{ProductFeatureCategory:[{productFeatureCategoryId:\"20003\",productFeatureTypeId:\"COLOR\",description:\"床上用品\"}]},{ProductFeature:[{productFeatureId:\"20003\",productFeatureCategoryId:\"20003\",productFeatureTypeId:\"COLOR\",supplier:\"20003\",supplierRefId:\"3\",defaultSequenceNum:\"2\",description:\"佳人画裳\"}]},{ProductAlbumImage:[{albumImageId:\"20003\",albumImageTypeId:\"ADDITIONAL_IMAGE_1\",detailImageUrl:\"/images/products/additional/detail/20003_View_1.jpg\",largeImageUrl:\"/images/products/additional/large/20003_View_1.jpg\",mediumImageUrl:\"/images/products/additional/medium/20003_View_1.jpg\",originalImageUrl:\"/images/products/additional/20003_View_1.jpg\",productId:\"20003\",sequenceId:\"1\",smallImageUrl:\"/images/products/additional/small/20003_View_1.jpg\",webLink:\"N\"}]},{ProductCategoryMember:[{productCategoryId:\"2C0104\",productId=\"20003\",fromDate=\"2013-08-15 14:53:41\"}]},{ProductFeatureAppl:[{productFeatureId:\"20003\",productId:\"20003\",productFeatureApplTypeId:\"SELECTABLE_FEATURE\",sequenceNum:\"1\",fromDate:\"2013-08-15 14:53:41\"}]},{ProductAssoc:[{productId:\"20003\",productIdTo:\"10005_1\",productAssocTypeId:\"PRODUCT_VARIANT\",fromDate:\"2013-08-15 14:53:41\"}]}]";
		String productPriceChangeList = "productPriceChangeList:[{ProductPriceChange:[{productPriceChangeId:\"20002\",productId:\"20003\",productPriceTypeId:\"DEFAULT_PRICE\",productPricePurposeId:\"PURCHASE\",currencyUomId:\"CNY\",productStoreGroupId:\"_NA_\",fromDate:\"2016-12-22 23:59:59\",price:\"60.00\",changedByUserLogin:\"admin\"}]}]";
		json = json + "," + product  + "," + productPriceList + ","  + SupplierProduct + "," + objectValueList +"," + productPriceChangeList+"}";
		Map<String, String> parameterMap = FastMap.newInstance();
		JSONObject jsonObject = JSONObject.fromObject(json);
		String strJson=jsonObject.toString();
		String inputparam="abcxyz"+strJson;		
		String signValue=bytetoString(DigestUtils.sha(inputparam.getBytes()));
		byte[] arg=strJson.toString().getBytes();
		String params = Base64.encode(arg);
		parameterMap.put("params", params);
		parameterMap.put("signValue", signValue);
		String ret = HttpRequest.postData("http://localhost:7070/interface/control/chexiangProductOnToCadillac", parameterMap, "UTF-8");
		System.out.println(ret);
	}
	
	public void testProductDown() throws Exception {
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(UtilDateTime.nowTimestamp());
		Timestamp now  = UtilDateTime.nowTimestamp();
		String chexiangToCadillac = "{companyCode:\"cadillac\",productId:\"10002\",subDate:";
		chexiangToCadillac = chexiangToCadillac + date + ",";
		chexiangToCadillac += "salesDiscontinuationDate:"  + "\"" + now  + "\"" + "}";
		Map<String, String> parameterMap = FastMap.newInstance();
		JSONObject jsonObject = JSONObject.fromObject(chexiangToCadillac);
		String strJson=jsonObject.toString();
		String inputparam="abcxyz"+strJson;		
		String signValue=bytetoString(DigestUtils.sha(inputparam.getBytes()));
		byte[] arg=strJson.toString().getBytes();
		String params = Base64.encode(arg);
		parameterMap.put("params", params);
		parameterMap.put("signValue", signValue);
		String ret = HttpRequest.postData("http://116.246.11.214:7171/interface/control/chexiangProductDownToCadillac", parameterMap, "UTF-8");
		System.out.println(ret);
	}
	
	public void testCatagoryService() throws Exception {
		String test = STR_BRAND_SERVICE ;
		Map<String, String> parameterMap = FastMap.newInstance();
		JSONObject jsonObject = JSONObject.fromObject(test);
		String strJson=jsonObject.toString();
		String inputparam="abcxyz"+strJson;		
		String signValue=bytetoString(DigestUtils.sha(inputparam.getBytes()));
		byte[] arg=strJson.toString().getBytes();
		String params = Base64.encode(arg);
		parameterMap.put("params", params);
		parameterMap.put("signValue", signValue);
		String ret = HttpRequest.postData("http://127.0.0.1:7070/interface/control/chexiangProdCatalogToCadillac", parameterMap, "UTF-8");
		System.out.println(ret);
				
	}
	
	public void testProductFeature() throws Exception {
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(UtilDateTime.nowTimestamp());
		String json = "{companyCode:\"cadi\",subDate:\"";
		json += date + "\"" + ","+"syncObjectId:\"20001\",";
		
		String ProductFeatureCategory = "ProductFeatureCategory:[{productFeatureCategoryId:\"20001\",productFeatureTypeId:\"MATERIAL\",description:\"大小\"}]";
		String ProductFeature = "ProductFeature:[{productFeatureId:\"20001\",productFeatureTypeId:\"DIMENSION\",productFeatureCategoryId:\"20001\",supplier:\"20001\",supplierRefId:\"20001\",defaultSequenceNum:\"1\",description:\"1米5床适用\"},{productFeatureId:\"20002\",productFeatureTypeId:\"DIMENSION\",productFeatureCategoryId:\"20002\",supplier:\"20001\",supplierRefId:\"20001\",defaultSequenceNum:\"2\",description:\"1米8床适用\"}]";
		json += "objectValueList:[" + "{" + ProductFeatureCategory + "}," + "{" + ProductFeature  + "}]" + "}";
		Map<String, String> parameterMap = FastMap.newInstance();
		JSONObject jsonObject = JSONObject.fromObject(json);
		String strJson=jsonObject.toString();
		String inputparam="abcxyz"+strJson;		
		String signValue=bytetoString(DigestUtils.sha(inputparam.getBytes()));
		byte[] arg=strJson.toString().getBytes();
		String params = Base64.encode(arg);
		parameterMap.put("params", params);
		parameterMap.put("signValue", signValue);
		String ret = HttpRequest.postData("http://127.0.0.1:7070/interface/control/chexiangProductFeatureToCadillac", parameterMap, "UTF-8");
		System.out.println(ret);
				
	}
	
	public void testPayment() throws Exception {
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(UtilDateTime.nowTimestamp());
		String json = "{companyCode:\"cadi\",subDate:\"";
		json += date + "\"" + ","+"syncObjectId:\"Cadi\",";
		
		String paymentList = "paymentList:[{productId:\"20001\",paymentType:\"1\",discount:\"\"},{productId:\"20002\",paymentType:\"2\",discount:\"\"}]";
		json += paymentList + "}";
		Map<String, String> parameterMap = FastMap.newInstance();
		JSONObject jsonObject = JSONObject.fromObject(json);
		String strJson=jsonObject.toString();
		System.out.println(strJson);
		String inputparam="abcxyz"+strJson;		
		String signValue=bytetoString(DigestUtils.sha(inputparam.getBytes()));
		byte[] arg=strJson.toString().getBytes();
		String params = Base64.encode(arg);
		parameterMap.put("params", params);
		parameterMap.put("signValue", signValue);
		String ret = HttpRequest.postData("http://127.0.0.1:7070/interface/control/chexiangPaymentToCadillac", parameterMap, "UTF-8");
		System.out.println(ret);
				
	}
	
	public static void main(String[] args) throws Exception {
		interfaceTest app = new interfaceTest();
//		app.testBrandService();
//		app.testProductOn();
//		app.testPayment();
//		app.changeOrderStatus();
//		app.batchCancel();
		app.testAcceptDeliveryInfo();
//		app.testAcceptReturn();
		//app.testSHA();
		//app.testProductDown();
//		app.testCatagoryService();
		//app.testProductFeature();
		//app.testTest();
		//app.testTest2();
		//app.justTest();
		
    } 
	
	public void testtt(){
		Map<String, Object> result = FastMap.newInstance();
		if(UtilValidate.isEmpty(result)){
			System.out.println("gggggggg");
		}
	}
	
	/**
	 * 测试当前时间
	 */
	public void getNowTimeLong(){
		//String timestamp = String.valueOf(UtilDateTime.nowTimestamp().getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(UtilDateTime.nowTimestamp());
		long now = 0;
		try {
			now = sdf.parse(date).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testTest2(){
		//Long nowLong = System.currentTimeMillis();
//		String chexiangToCadillac = "{companyCode:\"anyolife\",product:[{productId:\"10001\",prodcutCode:\"shouji\"}]}";
//		//chexiangToCadillac = chexiangToCadillac + nowLong + "}";
//		Map<String, Object> paramsMap = XmlJSON.jsonToMap(chexiangToCadillac);
////		GenericDelegator.getGenericDelegator("default");
////		GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");  
//		Map<String, Object> returnMap = FastMap.newInstance();
//		for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {  
//			if(!entry.getKey().equals("companyCode") && !entry.getKey().equals("subDate")){
//				String modelName = entry.getKey();
//				Map<String, Object> entityMap = XmlJSON.jsonToMap(entry.getValue().toString());
//				GenericValue entity = MapToGenericValue.mapToGeneric(delegator, modelName, entityMap);
//				returnMap.put(modelName, entity);
//			}
//		}
//		
//    	System.out.println(returnMap);
//    	Object ss = (Object)paramsMap.get("product");
//    	System.out.println(ss);
    	
	}
	
	public void justTest(){
		Long nowLong = System.currentTimeMillis();
		String chexiangToCadillac = "{companyCode:\"anyolife\",brandId:\"10410\",brandName:\"ZIPPOtest\",brandEnName:\"ZIPPO1\",brandLogo:\"/images/products/additional/m9zpfnull_View.png\",brandLogoview:\"/images/products/additional/m9zpfnull_View.png\",brandDescribe:\"测试品牌同步\",brandDisabled:\"N\",subDate:";
		chexiangToCadillac = chexiangToCadillac + nowLong + "}";
		Map<String, String> parameterMap = FastMap.newInstance();
		parameterMap.put("params", chexiangToCadillac);
		String sign = "abcxyz" + chexiangToCadillac;
		String signValue=Md5Encrypt.md5(sign.toString());
		parameterMap.put("signValue", signValue);
		String ret = "";
		try {
			ret = HttpRequest.postData("http://localhost:7070/control/justTest", parameterMap, "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(ret);
	}
	
	public void testSHA(){
		 String param="eyJjb21wYW55Q29kZSI6ImNhZGkiLCJicmFuZElkIjoiMTA0MTciLCJicmFuZE5hbWUiOiJaSVBQT3Rlc3QiLCJicmFuZEVuTmFtZSI6IlpJUFBPMSIsImJyYW5kTG9nbyI6Ii9pbWFnZXMvcHJvZHVjdHMvYWRkaXRpb25hbC9tOXpwZm51bGxfVmlldy5wbmciLCJicmFuZExvZ292aWV3IjoiL2ltYWdlcy9wcm9kdWN0cy9hZGRpdGlvbmFsL205enBmbnVsbF9WaWV3LnBuZyIsImJyYW5kRGVzY3JpYmUiOiLmtYvor5Xlk4HniYzlkIzmraUiLCJicmFuZERpc2FibGVkIjoiTiIsInN1YkRhdGUiOjIwMTUxMjI0MTE0NTU1fQ==";
		 //     String signValue="4487D78E5D1EDDCA1C741409D2444A9F28A972DA";
			    byte[] paramDecode=Base64.decode(param);
				String final_str = new String(paramDecode);
				
				System.out.println(final_str);		
				 String inputparam="abcxyz"+final_str;		
				 String signValue=bytetoString(DigestUtils.sha(inputparam.getBytes()));
				 System.out.println(signValue);
	}
	
	private static String bytetoString(byte[] digest) {
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
