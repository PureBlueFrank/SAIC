/*
 * SAIC eCommerce Platform
 * Copyright (c) 2015 International Business Machines
 * Copyright (c) 2015 SAIC
 */
package com.saic.interfaces.order.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.saic.interfaces.common.service.CommonInterfaceService;
import com.saic.interfaces.facade.OrderInterfaceFacadeClient;
import com.saic.interfaces.iom.exception.ExceptionTranslator;
import com.saic.interfaces.iom.exception.InterfaceOTOException;


/**
 * <p></p>
 * <p>Created: 2015年12月22日 下午2:50:16</p>
 * <p>
 * 		<table border="1" cellpaddding="2" cellspacing="2" >
 * 			<tr>
 * 				<td align="center" colspan="3">UPDATES</td>
 * 			</tr>
 * 			<tr>
 * 				<td align="center">DATE</td>
 * 				<td align="center">DEVELOPER</td>
 * 				<td align="center">CHANGES</td>	
 * 			</tr>
 * 		</table>
 * </p>
 * @author xiangwanli
 */
public class OrderInterfaceService extends CommonInterfaceService {

	public static final String module = OrderInterfaceService.class.getName();

	private static final String INTERFACE_ORDER_VIEW = UtilProperties
			.getPropertyValue("interface.properties", "interface.order.view",
					"OrderOTOView");

	private static final String INTERFACE_ORDER_RETURN_VIEW = UtilProperties
			.getPropertyValue("interface.properties",
					"interface.order.return.view", "ReturnOrderOTOView");

	private static final String INTERFACE_ORDER_SWAP_VIEW = UtilProperties
			.getPropertyValue("interface.properties",
					"interface.order.swap.view", "SwapOrderOTOView");

	/**
	 * Create OTO Order
	 * 
	 * @param dctx
	 * @param context
	 * @return OTO orderCode
	 * @throws InterfaceOTOException
	 */
	public static Map<String, Object> createOrder2OTO(DispatchContext dctx,
			Map<String, ?> context) throws InterfaceOTOException {
		final String methodName = "createOrder2OTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String orderId = UtilValidate.isNotEmpty(context.get("orderId")) ? context
				.get("orderId").toString() : "";
		@SuppressWarnings("unchecked")
		Map<String, Object> orderMap = UtilValidate.isNotEmpty(context.get("orderMap")) ? (Map<String, Object>) context
				.get("orderMap") : null ;
		String strOrderJson = UtilValidate.isNotEmpty(context.get("orderJson")) ? context
				.get("orderJson").toString() : "";
				
		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			JSONObject jsonOrder = null;
			if (UtilValidate.isNotEmpty(strOrderJson)){
				jsonOrder = JSONObject.fromObject(strOrderJson);
			} else if (UtilValidate.isEmpty(orderMap)) {
				List<GenericValue> orderOTOView = delegator.findByAnd(
						INTERFACE_ORDER_VIEW,
						UtilMisc.toMap("orderId", orderId));
				GenericValue originalGV = EntityUtil.getFirst(orderOTOView);
				OrderInterfaceFacadeClient orderClient = new OrderInterfaceFacadeClient();
				jsonOrder = orderClient.getOrderOTOView(originalGV);
			} else {
				OrderInterfaceFacadeClient orderClient = new OrderInterfaceFacadeClient();
				jsonOrder = orderClient.getOrderOTOView(orderMap);
			}

			JSONObject jsonOrderWrapper = new JSONObject();
			jsonOrderWrapper.put("anyueOrderVO", jsonOrder.toString());
			Map<String, Object> returnRes = dispatcher.runSync(
					"callOpenApiOTO", UtilMisc.toMap(TARGET_SERVICE,
							INTERFACE_ORDER_OTO_TARGET_SERVICE_ORDER,
							TARGET_API,
							INTERFACE_ORDER_OTO_TARGETAPI_CREATEORDER, "data",
							jsonOrderWrapper.toString()));
			if (UtilValidate.isNotEmpty(returnRes)
					&& UtilValidate.isNotEmpty(returnRes.get("result"))) {
				JSONObject jsonReturn = JSONObject.fromObject(returnRes
						.get("result"));
				result.put("resultCode", jsonReturn.get("resultCode"));
				result.put("resultMessage", jsonReturn.get("resultMessage"));
				result.put("orderCode", jsonReturn.get("businessCode"));

			}

		} catch (GenericEntityException e) {
			ExceptionTranslator.translate(e, module, methodName);
		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);
		return result;
	}

	/**
	 * Split the OTO Order.
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws InterfaceOTOException
	 */
	public static Map<String, Object> syncSplitOrderFromOTO(
			DispatchContext dctx, Map<String, ?> context)
			throws InterfaceOTOException {
		final String methodName = "syncSplitOrderFromOTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String orderId = UtilValidate.isNotEmpty(context.get("orderId")) ? context
				.get("orderId").toString() : "";
		String paymentSuccTime = UtilValidate.isNotEmpty(context.get("paymentSuccTime")) ? 
				context.get("paymentSuccTime").toString() : "" ;

		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			JSONObject jsonOrder = null;
			if (UtilValidate.isNotEmpty(orderId)) {
				List<GenericValue> orderOTOView = delegator.findByAnd(
						INTERFACE_ORDER_VIEW,
						UtilMisc.toMap("orderId", orderId));
				GenericValue originalGV = EntityUtil.getFirst(orderOTOView);
				OrderInterfaceFacadeClient orderClient = new OrderInterfaceFacadeClient();
				jsonOrder = orderClient.getOrderOTOView(originalGV ,delegator , paymentSuccTime);
			}

			JSONObject jsonOrderWrapper = new JSONObject();
			jsonOrderWrapper.put("anyueOrderVO", jsonOrder.toString());
			jsonOrderWrapper.put("splitAttribute",INTERFACE_ORDER_OTO_SPLIT_ORDER_SPLIT_ATTRIBUTE);
			Map<String, Object> returnRes = dispatcher.runSync(
					"callOpenApiOTO", UtilMisc.toMap(TARGET_SERVICE,
							INTERFACE_ORDER_OTO_TARGET_SERVICE_SPLIT_ORDER,
							TARGET_API,
							INTERFACE_ORDER_OTO_TARGETAPI_SPLIT_ORDER, "data",
							jsonOrderWrapper.toString()));
			if (UtilValidate.isNotEmpty(returnRes)
					&& UtilValidate.isNotEmpty(returnRes.get("result"))) {
				JSONObject jsonReturn = JSONObject.fromObject(returnRes
						.get("result"));
				if(UtilValidate.isNotEmpty(jsonReturn.get("arrayListJson"))){
					result.put("subOrders", jsonReturn.get("arrayListJson").toString());
				}				
				result.put("resultCode", jsonReturn.get("resultCode"));
				result.put("resultMessage", jsonReturn.get("resultMessage"));
			}

		} catch (GenericEntityException e) {
			ExceptionTranslator.translate(e, module, methodName);
		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);
		return result;
	}

	/**
	 * Mapping Anyue OrderId and OTO OrderCode
	 * 
	 * @param dctx
	 * @param context
	 * @return success or failed
	 * @throws InterfaceOTOException
	 */
	public static Map<String, Object> syncOrderCode2OTO(DispatchContext dctx,
			Map<String, ?> context) throws InterfaceOTOException {
		final String methodName = "syncOrderCode2OTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String orderCode = UtilValidate.isNotEmpty(context.get("orderCode")) ? context
				.get("orderCode").toString() : "";
		String anyoOrderCode = UtilValidate.isNotEmpty(context
				.get("anyoOrderCode")) ? context.get("anyoOrderCode")
				.toString() : "";

		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			/*
			 * build open API parameters here ...
			 */
			JSONObject jsonQuery = new JSONObject();
			jsonQuery.put("orderCode", orderCode);
			jsonQuery.put("anyoOrderCode", anyoOrderCode);
			/*
			 * call open API here ...
			 */
			Map<String, Object> returnRes = dispatcher.runSync(
					"callOpenApiOTO", UtilMisc.toMap(TARGET_SERVICE,
							INTERFACE_ORDER_OTO_TARGET_SERVICE_ORDER,
							TARGET_API,
							INTERFACE_ORDER_OTO_TARGETAPI_UPDATE_ORDERCODE,
							"data", jsonQuery.toString()));
			if (UtilValidate.isNotEmpty(returnRes)
					&& UtilValidate.isNotEmpty(returnRes.get("result"))) {
				JSONObject jsonReturn = JSONObject.fromObject(returnRes
						.get("result"));
				result.put("resultCode", jsonReturn.get("resultCode"));
				result.put("resultMessage", jsonReturn.get("resultMessage"));
			}
		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		return result;
	}

	/**
	 * Synchronized the cancelled order to OTO side.
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws InterfaceOTOException
	 */
	public static Map<String, Object> syncUnpaidOrderCancel2OTO(
			DispatchContext dctx, Map<String, ?> context)
			throws InterfaceOTOException {
		final String methodName = "syncUnpaidOrderCancel2OTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String orderCode = UtilValidate.isNotEmpty(context.get("orderCode")) ? context
				.get("orderCode").toString() : "";
		String memberId = UtilValidate.isNotEmpty(context.get("memberId")) ? context
				.get("memberId").toString() : "";
		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			/*
			 * build open API parameters here ...
			 */
			JSONObject jsonCancelUnpaidOrder = new JSONObject();
			jsonCancelUnpaidOrder.put("orderCode", orderCode);
			jsonCancelUnpaidOrder.put("memberId", memberId);
			/*
			 * call open API here ...
			 */
			Map<String, Object> returnRes = dispatcher.runSync(
					"callOpenApiOTO", UtilMisc.toMap(TARGET_SERVICE,
							INTERFACE_ORDER_OTO_TARGET_SERVICE_ORDER,
							TARGET_API,
							INTERFACE_ORDER_OTO_TARGETAPI_CANCELORDER_BYUSER,
							"data", jsonCancelUnpaidOrder.toString()));
			if (UtilValidate.isNotEmpty(returnRes)
					&& UtilValidate.isNotEmpty(returnRes.get("result"))) {
				JSONObject jsonReturn = JSONObject.fromObject(returnRes
						.get("result"));
				result.put("resultCode", jsonReturn.get("resultCode"));
				result.put("resultMessage", jsonReturn.get("resultMessage"));
			}
		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		return result;
	}

	/**
	 * Synchronized the cancelled paid order to OTO side .
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws InterfaceOTOException
	 */
	public static Map<String, Object> syncPaidOrderCancel2OTO(
			DispatchContext dctx, Map<String, ?> context)
			throws InterfaceOTOException {
		final String methodName = "syncPaidOrderCancel2OTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String orderCode = UtilValidate.isNotEmpty(context.get("orderCode")) ? context
				.get("orderCode").toString() : "";
		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			/*
			 * build open API parameters here ...
			 */
			JSONObject jsonCancelPaidOrder = new JSONObject();
			jsonCancelPaidOrder.put("orderCode", orderCode);
			/*
			 * call open API here ...
			 */
			Map<String, Object> returnRes = dispatcher.runSync(
					"callOpenApiOTO", UtilMisc.toMap(TARGET_SERVICE,
							INTERFACE_ORDER_OTO_TARGET_SERVICE_ORDER,
							TARGET_API,
							INTERFACE_ORDER_OTO_TARGETAPI_CANCEL_AFTERPAIDORDER,
							"data", jsonCancelPaidOrder.toString()));
			result.put("result", returnRes);
		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		return result;
	}

	/**
	 * Synchronized the return order to OTO side .
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws InterfaceOTOException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> syncReturnOrder2OTO(DispatchContext dctx,
			Map<String, ?> context) throws InterfaceOTOException {
		final String methodName = "syncReturnOrder2OTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String returnId = UtilValidate.isNotEmpty(context.get("returnId")) ? context
				.get("returnId").toString() : "";
		List<Map<String , Object>> returnMapList = UtilValidate.isNotEmpty(context.get("returnMapList")) ? (List<Map<String , Object>>)context
				.get("returnMapList") : null ;		
		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			/*
			 * build open API parameters here ...
			 */
			List<JSONObject> jsonReturnOrders = new ArrayList<JSONObject>();

			if (UtilValidate.isNotEmpty(returnMapList)){
				for (Map<String ,Object> returnItemMap : returnMapList) {
					OrderInterfaceFacadeClient orderClient = new OrderInterfaceFacadeClient();
					JSONObject jsonReturnOrderItem = orderClient
							.getReturnOrderOTOView(returnItemMap);
					jsonReturnOrders.add(jsonReturnOrderItem);
				}
			} else if (UtilValidate.isNotEmpty(returnId)) {
				List<GenericValue> returnOrderItems = delegator.findByAnd(
						INTERFACE_ORDER_RETURN_VIEW,
						UtilMisc.toMap("returnId", returnId));
				if (UtilValidate.isNotEmpty(returnOrderItems)) {
					for (GenericValue returnOrderItem : returnOrderItems) {
						OrderInterfaceFacadeClient orderClient = new OrderInterfaceFacadeClient();
						JSONObject jsonReturnOrderItem = orderClient
								.getReturnOrderOTOView(returnOrderItem);
						jsonReturnOrders.add(jsonReturnOrderItem);
					}
				}
			}
			
			JSONObject returnOrdersWrapper = new JSONObject();
			returnOrdersWrapper.put("returnGoodsVO",
					jsonReturnOrders.toString());
			/*
			 * call open API here ...
			 */
			Map<String, Object> returnRes = dispatcher.runSync(
					"callOpenApiOTO", UtilMisc.toMap(TARGET_SERVICE,
							INTERFACE_ORDER_OTO_TARGET_SERVICE_RETURN,
							TARGET_API,
							INTERFACE_ORDER_OTO_TARGETAPI_RETURN_ORDER, "data",
							returnOrdersWrapper.toString()));
//			result.put("result", returnRes);
//			System.out.println("1111111111111111111111111"+returnRes.get("result").getClass());
			JSONObject jsonReturn = JSONObject.fromObject(returnRes.get("result"));
			result.put("returnOrderId", jsonReturn.get("businessCode"));
			result.put("resultCode", jsonReturn.get("resultCode"));
			result.put("resultMessage", jsonReturn.get("resultMessage"));

		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		} catch (GenericEntityException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		return result;
	}

	/**
	 * Synchronized the return order status to OTO side .
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws InterfaceOTOException
	 */
	public static Map<String, Object> syncReturnOrderStatus2OTO(
			DispatchContext dctx, Map<String, ?> context)
			throws InterfaceOTOException {
		final String methodName = "syncReturnOrderStatus2OTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String returnOrderId = UtilValidate.isNotEmpty(context
				.get("returnOrderId")) ? context.get("returnOrderId")
				.toString() : "";
		String operAuditStatus = UtilValidate.isNotEmpty(context
				.get("operAuditStatus")) ? context.get("operAuditStatus")
				.toString() : "";
		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			/*
			 * build open API parameters here ...
			 */
			JSONObject jsonReturnOrderStatus = new JSONObject();
			jsonReturnOrderStatus.put("returnOrderId", returnOrderId);
			jsonReturnOrderStatus.put("operAuditStatus", operAuditStatus);

			/*
			 * call open API here ...
			 */
			Map<String, Object> returnRes = dispatcher.runSync(
					"callOpenApiOTO", UtilMisc.toMap(TARGET_SERVICE,
							INTERFACE_ORDER_OTO_TARGET_SERVICE_RETURN,
							TARGET_API,
							INTERFACE_ORDER_OTO_TARGETAPI_RETURN_ORDER_STATUS,
							"data", jsonReturnOrderStatus.toString()));
			if (UtilValidate.isNotEmpty(returnRes)
					&& UtilValidate.isNotEmpty(returnRes.get("result"))) {
				JSONObject jsonReturnStatus = JSONObject.fromObject(returnRes
						.get("result"));
				result.put("resultCode", jsonReturnStatus.get("resultCode"));
				result.put("resultMessage",
						jsonReturnStatus.get("resultMessage"));
			}
		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		return result;
	}

	/**
	 * Synchronized the swap order to OTO side .
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws InterfaceOTOException
	 */
	public static Map<String, Object> syncSwapOrder2OTO(DispatchContext dctx,
			Map<String, ?> context) throws InterfaceOTOException {
		final String methodName = "syncSwapOrder2OTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String applySwapOrderId = UtilValidate.isNotEmpty(context
				.get("applySwapOrderId")) ? context.get("applySwapOrderId")
				.toString() : "";
		@SuppressWarnings("unchecked")
		Map<String , Object> applySwapOrderMap = UtilValidate.isNotEmpty(context
				.get("applySwapOrderMap")) ? (Map<String , Object>)context.get("applySwapOrderMap") : null;

		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			/*
			 * build open API parameters here ...
			 */
			JSONObject jsonSwapOrder = new JSONObject();
			if (UtilValidate.isNotEmpty(applySwapOrderId)) {
				List<GenericValue> swapOrders = delegator.findByAnd(
						INTERFACE_ORDER_SWAP_VIEW,
						UtilMisc.toMap("applySwapOrderId", applySwapOrderId));
				if (UtilValidate.isNotEmpty(swapOrders)) {
					GenericValue swapOrder = EntityUtil.getFirst(swapOrders);
					OrderInterfaceFacadeClient orderClient = new OrderInterfaceFacadeClient();
					jsonSwapOrder = orderClient.getSwapOrderOTOView(swapOrder);
				}
			} else if (UtilValidate.isNotEmpty(applySwapOrderMap)) {
				OrderInterfaceFacadeClient orderClient = new OrderInterfaceFacadeClient();
				jsonSwapOrder = orderClient.getSwapOrderOTOView(applySwapOrderMap);
			} else {
				Debug.logError("Missing Input value : applySwapOrderId or applySwapOrderMap", module);
			}
			
			JSONObject swapOrdersWrapper = new JSONObject();
			swapOrdersWrapper.put("swapOrderVO", jsonSwapOrder.toString());
			/*
			 * call open API here ...
			 */
			Map<String, Object> returnRes = dispatcher.runSync(
					"callOpenApiOTO", UtilMisc.toMap(TARGET_SERVICE,
							INTERFACE_ORDER_OTO_TARGET_SERVICE_SWAP,
							TARGET_API,
							INTERFACE_ORDER_OTO_TARGETAPI_SWAP_ORDER, "data",
							swapOrdersWrapper.toString()));
			if (UtilValidate.isNotEmpty(returnRes)
					&& UtilValidate.isNotEmpty(returnRes.get("result"))) {
				JSONObject jsonReturn = JSONObject.fromObject(returnRes
						.get("result"));
				result.put("swapOrderId", jsonReturn.get("businessId")
						.toString());
				result.put("resultCode", jsonReturn.get("resultCode"));
				result.put("resultMessage", jsonReturn.get("resultMessage"));
			}

		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		} catch (GenericEntityException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		return result;
	}

	/**
	 * Synchronized the return order status to OTO side .
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws InterfaceOTOException
	 */
	public static Map<String, Object> syncSwapOrderStatus2OTO(
			DispatchContext dctx, Map<String, ?> context)
			throws InterfaceOTOException {
		final String methodName = "syncSwapOrderStatus2OTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String swapOrderId = UtilValidate
				.isNotEmpty(context.get("swapOrderId")) ? context.get(
				"swapOrderId").toString() : "";
		String operAuditStatus = UtilValidate.isNotEmpty(context
				.get("operAuditStatus")) ? context.get("operAuditStatus")
				.toString() : "";
		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			/*
			 * build open API parameters here ...
			 */
			JSONObject jsonSwapOrderStatus = new JSONObject();
			jsonSwapOrderStatus.put("swapOrderId", swapOrderId);
			jsonSwapOrderStatus.put("operAuditStatus", operAuditStatus);

			/*
			 * call open API here ...
			 */
			Map<String, Object> returnRes = dispatcher.runSync(
					"callOpenApiOTO", UtilMisc.toMap(TARGET_SERVICE,
							INTERFACE_ORDER_OTO_TARGET_SERVICE_SWAP,
							TARGET_API,
							INTERFACE_ORDER_OTO_TARGETAPI_SWAP_ORDER_STATUS,
							"data", jsonSwapOrderStatus.toString()));
			if (UtilValidate.isNotEmpty(returnRes)
					&& UtilValidate.isNotEmpty(returnRes.get("result"))) {
				JSONObject jsonSwapStatus = JSONObject.fromObject(returnRes
						.get("result"));
				result.put("resultCode", jsonSwapStatus.get("resultCode"));
				result.put("resultMessage", jsonSwapStatus.get("resultMessage"));
			}
		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		return result;
	}

	/**
	 * Synchronized the sub orders to OTO side .
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws InterfaceOTOException
	 */
	public static Map<String, Object> syncSubOrder2OTO(DispatchContext dctx,
			Map<String, ?> context) throws InterfaceOTOException {
		final String methodName = "syncSubOrder2OTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		@SuppressWarnings("unchecked")
		List<String> subOrderList = (List<String>) context.get("subOrderList");
		String strSubOrdersJson = UtilValidate.isNotEmpty(context.get("subOrdersJson")) ? context.get("subOrdersJson").toString() : "" ;
		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			/*
			 * build open API parameters here ...
			 */
			if (UtilValidate.isEmpty(strSubOrdersJson)) {
				List<JSONObject> jsonSubOrders = new ArrayList<JSONObject>();
				for (String subOrderId : subOrderList) {
					List<GenericValue> orderOTOView = delegator.findByAnd(
							INTERFACE_ORDER_VIEW,
							UtilMisc.toMap("orderId", subOrderId));
					GenericValue originalGV = EntityUtil.getFirst(orderOTOView);
					OrderInterfaceFacadeClient orderClient = new OrderInterfaceFacadeClient();
					JSONObject jsonSubOrder = orderClient
							.getOrderOTOView(originalGV);
					jsonSubOrders.add(jsonSubOrder);
				}
				strSubOrdersJson = jsonSubOrders.toString();
			}

			JSONObject jsonOrderWrapper = new JSONObject();
			jsonOrderWrapper.put("anyueOrderVOs", strSubOrdersJson);
			
			/*
			 * call open API here ...
			 */
			Map<String, Object>  returnRes = dispatcher.runSync("callOpenApiOTO", UtilMisc.toMap(
					TARGET_SERVICE, INTERFACE_ORDER_OTO_TARGET_SERVICE_ORDER,
					TARGET_API, INTERFACE_ORDER_OTO_TARGETAPI_CREATE_SUBORDER,
					"data", jsonOrderWrapper.toString()));
			
			if (UtilValidate.isNotEmpty(returnRes)
					&& UtilValidate.isNotEmpty(returnRes.get("result"))) {
				JSONObject jsonBizData = JSONObject.fromObject(returnRes
						.get("result"));
				result.put("resultCode", jsonBizData.get("resultCode"));
				result.put("resultMessage", jsonBizData.get("resultMessage"));
				result.put("bizData", jsonBizData.get("arrayListJson").toString());
			}
			
		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		} catch (GenericEntityException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		return result;
	}

	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws InterfaceOTOException
	 */
	public static Map<String, Object> queryOrderListFromOTO(
			DispatchContext dctx, Map<String, ?> context)
			throws InterfaceOTOException {
		final String methodName = "queryOrderListFromOTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String pageIndex = UtilValidate.isNotEmpty(context.get("pageIndex")) ? context
				.get("pageIndex").toString() : "";
		String partyId = UtilValidate.isNotEmpty(context.get("partyId")) ? context
				.get("partyId").toString() : "";
		String startTime = UtilValidate.isNotEmpty(context.get("startTime")) ? context
				.get("startTime").toString() : "";
		String endTime = UtilValidate.isNotEmpty(context.get("endTime")) ? context
				.get("endTime").toString() : "";
		String productId = UtilValidate.isNotEmpty(context.get("productId")) ? context
				.get("productId").toString() : "";
		String productName = UtilValidate
				.isNotEmpty(context.get("productName")) ? context.get(
				"productName").toString() : "";

		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			/*
			 * build open API parameters here ...
			 */
			JSONObject jsonQueryOrder = new JSONObject();
			jsonQueryOrder.put("Pagination", pageIndex);
			jsonQueryOrder.put("clientMemberId", partyId);
			jsonQueryOrder.put("startTime", startTime);
			jsonQueryOrder.put("endTIme", endTime);
			jsonQueryOrder.put("productName", productName);
			jsonQueryOrder.put("productId", productId);
			/*
			 * call open API here ...
			 */
			result = dispatcher.runSync("callOpenApiOTO", UtilMisc.toMap(
					TARGET_SERVICE, INTERFACE_ORDER_OTO_TARGET_SERVICE_ORDER,
					TARGET_API, INTERFACE_ORDER_OTO_TARGETAPI_QUERY_ORDERLIST,
					"data", jsonQueryOrder.toString()));
		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		return result;
	}

	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws InterfaceOTOException
	 */
	public static Map<String, Object> queryOrderDetailFromOTO(
			DispatchContext dctx, Map<String, ?> context)
			throws InterfaceOTOException {
		final String methodName = "queryOrderDetailFromOTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		LocalDispatcher dispatcher = dctx.getDispatcher();

		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			/*
			 * build open API parameters here ...
			 */
			JSONObject jsonQueryOrder = new JSONObject();
			/*
			 * call open API here ...
			 */
			result = dispatcher.runSync("callOpenApiOTO", UtilMisc.toMap(
					TARGET_SERVICE, INTERFACE_ORDER_OTO_TARGET_SERVICE_ORDER,
					TARGET_API,
					INTERFACE_ORDER_OTO_TARGETAPI_QUERY_ORDERDETAIL_BYCODE,
					"data", jsonQueryOrder.toString()));
		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		return result;
	}

	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws InterfaceOTOException
	 */
	public static Map<String, Object> queryOrderStatusFromOTO(
			DispatchContext dctx, Map<String, ?> context)
			throws InterfaceOTOException {
		final String methodName = "queryOrderStatusFromOTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		LocalDispatcher dispatcher = dctx.getDispatcher();

		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			/*
			 * build open API parameters here ...
			 */
			JSONObject jsonQueryOrder = new JSONObject();
			/*
			 * call open API here ...
			 */
			result = dispatcher.runSync("callOpenApiOTO", UtilMisc.toMap(
					TARGET_SERVICE,
					INTERFACE_ORDER_OTO_TARGET_SERVICE_ORDERSTATUS, TARGET_API,
					INTERFACE_ORDER_OTO_TARGETAPI_QUERY_ORDERSTATUS, "data",
					jsonQueryOrder.toString()));
		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		return result;
	}

	/**
	 * Synchronized the Order status to OTO.
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws InterfaceOTOException
	 */
	public static Map<String, Object> syncOrderStatus2OTO(DispatchContext dctx,
			Map<String, ?> context) throws InterfaceOTOException {
		final String methodName = "syncOrderStatus2OTO(DispatchContext ctx, Map<String, ?> context)";
		Locale locale = (Locale) context.get("locale");

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_ENTERING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		/*
		 * initialize the input value.
		 */
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String orderCode = UtilValidate.isNotEmpty(context.get("orderCode")) ? context
				.get("orderCode").toString() : "";
		String status = UtilValidate.isNotEmpty(context.get("status")) ? context
				.get("status").toString() : "";
		String operationTime = UtilValidate.isNotEmpty(context
				.get("operationTime")) ? context.get("operationTime")
				.toString() : "";

		/*
		 * initialize the output value
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			/*
			 * build open API parameters here ...
			 */
			JSONObject jsonOrderStatus = new JSONObject();
			jsonOrderStatus.put("orderCode", orderCode);
			jsonOrderStatus.put("status", "");
			jsonOrderStatus.put("anyueStatus", status);
			jsonOrderStatus.put("operationTime", operationTime);
			/*
			 * call open API here ...
			 */
			Map<String, Object> returnRes = dispatcher.runSync(
					"callOpenApiOTO", UtilMisc.toMap(TARGET_SERVICE,
							INTERFACE_ORDER_OTO_TARGET_SERVICE_ORDERSTATUS,
							TARGET_API,
							INTERFACE_ORDER_OTO_TARGETAPI_MODIFY_ORDER_STATUS,
							"data", jsonOrderStatus.toString()));
			if (UtilValidate.isNotEmpty(returnRes)
					&& UtilValidate.isNotEmpty(returnRes.get("result"))) {
				JSONObject jsonReturn = JSONObject.fromObject(returnRes
						.get("result"));
				result.put("resultCode", jsonReturn.get("resultCode"));
				result.put("resultMessage", jsonReturn.get("resultMessage"));
			}
		} catch (GenericServiceException e) {
			ExceptionTranslator.translate(e, module, methodName);
		}

		Debug.logInfo(UtilProperties.getMessage(resource, module + "."
				+ LOG_EXITING, UtilMisc.toList(UtilMisc.toMap("methodName",
				methodName).toString()), locale), module);

		return result;
	}
}
