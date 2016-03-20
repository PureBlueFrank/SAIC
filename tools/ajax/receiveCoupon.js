var prefix = "";

/**
 * ajax领取抵用券
 * @returns {Boolean}
 */
function ajaxReceiveCoupon(){
   var url = prefix + '/touch/control/receivedCoupon?couponFtl=couponUnused&index=0';
	 $.ajax({
	     url: url,
		 async: false,
		 success : function(data){
			 if("Y" == data.HAVE_RECEIVED){
				 alert("领取成功！");
				 window.location.href="/touch/control/myCouponList?couponFtl=couponUnused&index=0";
			 }else{
				 alert("领取失败！");
				 window.location.href="/touch/control/receiveCouponAndList?couponFtl=couponUnused&index=0&receiveCoupon=yes";
			 }
		 }
		 });
   return false;
}
