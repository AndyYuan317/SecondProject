package com.wingplus.coomohome.config;

import com.wingplus.coomohome.manager.ThreadManager;

/**
 * 楷模家API
 *
 * @author leaffun.
 *         Create on 2017/10/17.
 */
public class APIConfig {


    /**
     * 获取API全路径
     *
     * @param apiString 接口路径
     * @return
     */
    public static String getAPIUrl(String apiString) {
        return API_SERVER + apiString + API_SERVER_AFTER;
    }

    public static String getAPIUrlForBrand(String apiString){
        return API_SERVER_FOR_BRAND + apiString + API_SERVER_AFTER;
    }


    /**
     * 数据请求Wrapper
     *
     * @param runnable 发起runnable请求，并在主线程刷新UI
     *                 <p>
     *                 example：
     *                 getDataIntoView(new Runnable() {
     * @Override public void run() {
     * HashMap<String, String> hash = new HashMap<>();
     * hash.put("city", "上海");
     * HttpUtil.GetDataFromNet(APIConfig.API_ACTIVITY_INFO, hash, HttpUtil.GET);
     * UIUtils.runOnUIThread(new Runnable() {
     * @Override public void run() {
     * }
     * });
     * }
     * });
     * </p>
     */
    public static void getDataIntoView(Runnable runnable) {
        ThreadManager.getThreadPool().exeute(runnable);
    }


    //////////////////////////////////
    ///////////              /////////
    ///////////  全局错误码  /////////
    ///////////      1/2    /////////
    /////////////////////////////////
    public static final int CODE_ERR = 0;
    public static final int CODE_SUCCESS = 1;
    public static final int CODE_PHONE_UNREGISTER = 2;
    public static final int CODE_AUTHENTICATION_ERR = 1001;
    public static final int CODE_TOKEN_OUT_TIME = 1002;
    public static final int CODE_PARAMETER_LOST = 1003;
    public static final int CODE_UNKNOWN_ERR = 9999;


    //////////////////////////////////
    ///////////             //////////
    ///////////   接口URL   //////////
    ///////////    2/2      /////////
    /////////////////////////////////
    private static final String ADDRESS = APIAddress.API_ADDRESS;

    private static final String API_SERVER_FOR_BRAND = "http://" + ADDRESS + "/api/shop/";
    private static final String API_SERVER = "http://" + ADDRESS + "/shop/api/";
    private static final String API_SERVER_AFTER = ".do";

    //通用接口
    public static final String API_JPUSH_REGISTRATION_ID = "message/registerDevice";  //上传极光推送设备识别码
    public static final String API_COMMON_UPLOADIMG = "common/uploadimg";  //上传图片
    public static final String API_COMMON_SEND_VALID_CODE = "common/sendvalidcode";    //发送短信验证码
    public static final String API_COMMON_SHIPPING_COM_LIST = "common/shippingComList";   //快递公司列表
    public static final String API_COMMON_DLY_CENTER = "common/dlyCenter";   //货仓信息
    public static final String API_COMMON_CURSRV = "common/cursrv";   //客服信息
    public static final String API_COMMON_REG_NOTIFICATION = "common/regNotification";   //客服信息


    public static final String SHARE_SERVER_PRE = "http://" + ADDRESS + "/wap/goods-";
    public static final String SHARE_SERVER_AFTER = ".html";

    public static String getShareGoodsUrl(long goodsId) {
        return SHARE_SERVER_PRE + goodsId + SHARE_SERVER_AFTER;
    }

    //首页
    public static final String API_HOME_ADV = "home/adv";
    public static final String API_HOME_INFO = "home/info";
    public static final String API_HOME_REG_ADV = "home/regAdv";

    //用户
    public static final String API_USER_INFO = "user/info";
    public static final String API_USER_LOGIN = "user/login";
    public static final String API_USER_REGISTER = "user/register";
    public static final String API_USER_WEIXIN = "user/ssobyweixin";
    public static final String API_USER_MODIFY = "user/modify";
    public static final String API_USER_HEADIMG_CHANGE = "user/changeHeadImg";
    public static final String API_USER_BIND_WEIXIN = "user/bindWeixin";
    public static final String API_USER_BIND_MOBILE= "user/bindMobile";
    public static final String API_AGENT_BIND= "user/setAgent";

    //个人中心
    //优惠券
    public static final String API_COUPON_LIST = "coupon/list";
    public static final String API_COUPON_GET = "coupon/exchange";//领取

    //消息
    public static final String API_NEWS_LIST = "message/list";
    public static final String API_SET_MSG_READ = "message/setMsgRead";
    public static final String API_UN_READ_CNT = "message/unReadCnt";

    //我的收藏
    public static final String API_COLLECTION_LIST = "collection/list";
    public static final String API_COLLECTION_ADD = "collection/add";
    public static final String API_COLLECTION_DELETE = "collection/delete";
    //收货地址
    public static final String API_ADDRESS_LIST = "address/list";
    public static final String API_ADDRESS_ADD = "address/add";
    public static final String API_ADDRESS_MODIFY = "address/modify";
    public static final String API_ADDRESS_DELETE = "address/delete";
    //钱包充值
    public static final String API_WALLET_HISTORY = "wallet/history";
    public static final String API_WALLET_RECHARGE = "wallet/recharge";
    //门店列表
    public static final String API_STORE_LIST = "store/list";
    //签到积分等级余额
    public static final String API_SCORE_SIGN = "score/sign";
    public static final String API_SCORE_HISTORY = "score/history";
    public static final String API_SCORE_INFO = "score/info";
    public static final String API_WALLET_INFO = "wallet/info";
    //礼品中心
    public static final String API_GIFT_INFO = "gift/info";
    //公益活动
    public static final String API_SOCIAL_LIST = "social/list";
    public static final String API_SOCIAL_INFO = "social/info";

    //促销活动
    public static final String API_ACTIVITY_INFO = "activity/info";
    public static final String API_ACTIVITY_RULE = "activity/rule";

    //购物车
    public static final String API_CART_ADJUST = "cart/adjust";//加减后的数量
    public static final String API_CART_MODIFY = "cart/modify";//从购物车中清除
    public static final String API_CART_INFO = "cart/info";//购物车列表
    public static final String API_CART_ADD = "cart/add";//添加某商品
    public static final String API_CART_CHECKITEM = "cart/checkItem";//勾选商品
    public static final String API_CART_CHECKITEM_ALL = "cart/checkItemAll";//全反选商品
    public static final String API_CART_CHANGEITEM = "cart/changeItem";//修改规格
    public static final String API_CART_CNT = "cart/cartcnt";//总数量
    public static final String API_CART_GUESS = "cart/guess";//猜你喜欢

    public static final String API_CART_BUY_NOW = "cart/buynow";//立即购买
    public static final String API_CART_BUY_AGAIN = "cart/buyagain";//再次购买

    //商品
    public static final String API_GOODS_TOP_CATEGORY = "goods/topCategory";
    public static final String API_GOODS_CATEGORY = "goods/category";

    public static final String API_GOODS_DESCRIPTION = "goods/description";
    public static final String API_GOODS_DETAIL = "goods/detail";
    public static final String API_GOODS_DETAIL_BY_SN = "goods/detailBySn";
    public static final String API_GOODS_LIST = "goods/list";//分类商品列表
    public static final String API_GOODS_ACTIVITY = "goods/activity";
    public static final String API_GOODS_POPULAR = "goods/popular";
    public static final String API_GOODS_BRAND = "goods/story";

    //搜索
    public static final String API_SEARCH_HOT_WORDS = "search/hotWords";
    public static final String API_SEARCH_HISTORY_WORDS = "search/historyWords";
    public static final String API_SEARCH_CLEAR_HISTORY = "search/clearHistory";
    public static final String API_SEARCH_GOODS = "search/goods";

    //订单
    public static final String API_ORDER_LIST = "order/list";//订单列表（根据状态）
    public static final String API_ORDER_ROGCONFIRM = "order/rogConfirm";//确认收货
    public static final String API_ORDER_ADJUST = "order/adjust";
    public static final String API_ORDER_INFO = "order/info";//订单详情
    public static final String API_ORDER_INFO_BY_SN = "order/infobysn";//订单详情
    public static final String API_ORDER_PAY = "order/pay";
    public static final String API_ORDER_CANCEL = "order/cancel";//订单取消
    public static final String API_ORDER_LOGISTICS = "order/getExpressInfo";//订单物流
    public static final String API_ORDER_CNT = "order/orderCnt";//订单计数

    public static final String API_ORDER_CONFIRM = "order/confirm";//确认订单
    public static final String API_ORDER_ADD = "order/add";//商品加入订单
    public static final String API_ORDER_GET_SHIPPING_LIST = "order/getShippingList";//获取配送方式
    public static final String API_ORDER_GET_COUPON_LIST = "order/getCouponList";//获取可用优惠券
    public static final String API_ORDER_MODIFY_SHIPPING = "order/modifyShipping";//修改配送方式
    public static final String API_ORDER_MODIFY_ADDRESS = "order/modifyAddress";//修改收货地址
    public static final String API_ORDER_MODIFY_BONUS = "order/modifyBonus";//修改要使用的优惠券
    public static final String API_ORDER_GET_ADDRESS_LIST = "order/getAddressList";//不用
    public static final String API_ORDER_GET_PAY_TYPE_LIST = "order/getPayTypeList";//有，不用


    //售后
    public static final String API_AFTER_SALE_EVALUATE = "aftersale/evaluate";//商品评价
    public static final String API_AFTER_SALE_EVALUATE_LIST = "aftersale/evaluationList";//商品评价列表
    public static final String API_AFTER_SALE_HISTORY_LIST = "aftersale/list";//获取售后记录
    public static final String API_AFTER_SALE_VALID_LIST = "aftersale/afterSaleList";//获取可以售后的商品
    public static final String API_AFTER_SALE_ADD = "aftersale/add";//添加售后
    public static final String API_AFTER_SALE_CANCEL = "aftersale/cancel";//取消售后
    public static final String API_AFTER_SALE_INFO = "aftersale/info";//获取售后单详情
    public static final String API_AFTER_SALE_INFO_BY_SN = "aftersale/infobysn";//获取售后单详情
    public static final String API_AFTER_SALE_REASON = "aftersale/afterSaleReason";//获取退货原因
    public static final String API_AFTER_SALE_SHIPPING = "aftersale/shipping";//填写售后物流信息

    //支付
    public static final String API_PAYMENT_CHARGE = "payment/charge";// 请求支付票据

    //意见反馈
    public static final String API_FEEDBACK = "feedback/add";

    //关于
    public static final String API_HELP_INFO = "help/info";

}
