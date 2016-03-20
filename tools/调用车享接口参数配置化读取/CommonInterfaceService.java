/*
 * SAIC eCommerce Platform
 * Copyright (c) 2015 International Business Machines
 * Copyright (c) 2015 SAIC
 */
package com.saic.interfaces.common.service;

import org.ofbiz.base.util.UtilProperties;


/**
 * <p></p>
 * <p>Created: 2015年12月30日 下午1:57:03</p>
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
public class CommonInterfaceService {

	public static final String resource = "InterfaceInfoLabels";

	public static final String resource_error = "InterfaceErrorLabels";

	public static final String LOG_ENTERING = "Entering";

	public static final String LOG_EXITING = "Exiting";

	protected static final String TARGET_SERVICE = UtilProperties
			.getPropertyValue("interface.properties",
					"oto.openapi.param.url.targetservice", "targetService");

	protected static final String TARGET_API = UtilProperties.getPropertyValue(
			"interface.properties", "oto.openapi.param.url.targetapi",
			"targetApi");

	protected static final String INTERFACE_ORDER_OTO_TARGET_SERVICE_ORDER = UtilProperties
			.getPropertyValue(
					"interface.properties",
					"interface.order.oto.openapi.targetservice.emall.orderservice",
					"AnyueOrderService");

	protected static final String INTERFACE_ORDER_OTO_TARGET_SERVICE_ORDERSTATUS = UtilProperties
			.getPropertyValue(
					"interface.properties",
					"interface.order.oto.openapi.targetservice.emall.orderstatus",
					"emallOrderStatusService");

	protected static final String INTERFACE_ORDER_OTO_TARGET_SERVICE_RETURN = UtilProperties
			.getPropertyValue(
					"interface.properties",
					"interface.order.oto.openapi.targetservice.emall.returnservice",
					"emallReturnGoodsOrderService");

	protected static final String INTERFACE_ORDER_OTO_TARGET_SERVICE_SWAP = UtilProperties
			.getPropertyValue(
					"interface.properties",
					"interface.order.oto.openapi.targetservice.emall.swapservice",
					"emallApplySwapOrderService");

	protected static final String INTERFACE_ORDER_OTO_TARGET_SERVICE_SPLIT_ORDER = UtilProperties
			.getPropertyValue(
					"interface.properties",
					"interface.order.oto.openapi.targetservice.emall.ordersplitservice",
					"anyueOrderSplitService");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_CREATEORDER = UtilProperties
			.getPropertyValue("interface.properties",
					"interface.order.oto.openapi.targetapi.create.order",
					"createAnyueOrder");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_QUERY_ORDERLIST = UtilProperties
			.getPropertyValue("interface.properties",
					"interface.order.oto.openapi.targetapi.query.orderlist",
					"queryOrderList");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_UPDATE_ORDERCODE = UtilProperties
			.getPropertyValue("interface.properties",
					"interface.order.oto.openapi.targetapi.update.ordercode",
					"updateAnyueOrderCode");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_CANCELORDER_BYUSER = UtilProperties
			.getPropertyValue(
					"interface.properties",
					"interface.order.oto.openapi.targetapi.cancel.order.byuser",
					"cancleAnyueOrderByUser");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_QUERY_ORDERDETAIL_BYCODE = UtilProperties
			.getPropertyValue(
					"interface.properties",
					"interface.order.oto.openapi.targetapi.query.orderdetail.byordercode",
					"queryOrderDetailByOrderCode");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_CREATE_SUBORDER = UtilProperties
			.getPropertyValue("interface.properties",
					"interface.order.oto.openapi.targetapi.create.suborder",
					"createSubAnyueOrder");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_MODIFY_ORDER_STATUS = UtilProperties
			.getPropertyValue("interface.properties",
					"interface.order.oto.openapi.targetapi.modify.orderstatus",
					"modifyOrderStatus");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_QUERY_ORDERSTATUS = UtilProperties
			.getPropertyValue("interface.properties",
					"interface.order.oto.openapi.targetapi.query.orderstatus",
					"queryOrderStatus");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_CANCEL_PAIDORDER = UtilProperties
			.getPropertyValue("interface.properties",
					"interface.order.oto.openapi.targetapi.cancel.paidorder",
					"canclePaidOrder");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_CANCEL_AFTERPAIDORDER = UtilProperties
			.getPropertyValue("interface.properties",
					"interface.order.oto.openapi.targetapi.cancel.afterpaidorder",
					"cancleAnyueOrderAfterPaid");
	
	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_RETURN_ORDER = UtilProperties
			.getPropertyValue("interface.properties",
					"interface.order.oto.openapi.targetapi.insert.returnorder",
					"insertReturnGoodsOrder");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_SWAP_ORDER = UtilProperties
			.getPropertyValue("interface.properties",
					"interface.order.oto.openapi.targetapi.insert.swaporder",
					"insertApplySwapOrder");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_RETURN_ORDER_STATUS = UtilProperties
			.getPropertyValue(
					"interface.properties",
					"interface.order.oto.openapi.targetapi.modifystatus.returnorder",
					"updateReturnOperAuditStatus");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_SWAP_ORDER_STATUS = UtilProperties
			.getPropertyValue(
					"interface.properties",
					"interface.order.oto.openapi.targetapi.modifystatus.swaporder",
					"updateSwapOperAuditStatus");

	protected static final String INTERFACE_ORDER_OTO_TARGETAPI_SPLIT_ORDER = UtilProperties
			.getPropertyValue("interface.properties",
					"interface.order.oto.openapi.targetapi.split.order",
					"splitOrder");

	protected static final String INTERFACE_ORDER_OTO_SPLIT_ORDER_SPLIT_ATTRIBUTE = UtilProperties
			.getPropertyValue(
					"interface.properties",
					"interface.order.oto.openapi.targetapi.split.order.splitAttribute",
					"[\"providerId\"]");
}
