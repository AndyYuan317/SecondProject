package com.wingplus.coomohome.config;

import com.wingplus.coomohome.application.MallApplication;

/**
 * 配置类
 *
 * @author leaffun.
 *         Create on 2017/8/29.
 */
public class Constant {

    public static class Key {
        public static final String KEY_ACTIVITY_NAME = "activity_name";
        public static final String KEY_ACTIVITY_ID = "activity_id";
        public static final String KEY_GOOD_ID_TYPE = "good_id_type";
        public static final String KEY_GOOD_ID_OR_SN = "good_id";
        public static final String KEY_CATEGORY_TYPE = "category_type";
        public static final String KEY_WEB_URL = "web_url";
        public static final String KEY_REGULAR_TYPE = "regular_type";
        public static final String KEY_WELFARE_ID = "welfare_id";
        public static final String KEY_WELFARE_NAME = "welfare_name";
        public static final String KEY_MSG_CONTENT = "msg_content";
        public static final String KEY_MSG_IMG = "msg_";
        public static final String KEY_MSG_TITLE = "msg_title";
        public static final String KEY_ORDER_ID_OR_CODE = "order_id";
        public static final String KEY_ORDER_ID_TYPE = "order_type";
        public static final String KEY_LIST_CHOOSE = "list_choose";
        public static final String KEY_ORDER_ADDRESS = "orderAddress";
        public static final String KEY_ORDER_BONUS = "orderBonus";
        public static final String KEY_ORDER_UNSURE = "orderUnsure";
        public static final String KEY_AFTER_SALE_GOOD_STR = "good_str";
        public static final String KEY_AFTER_SALE_ORDER_ID = "key_after_sale_order_id";
        public static final String KEY_LOCATION_CHOSE = "key_location_chose";

    }

    public static class Provider{
        public static String getFilePath(){
            return MallApplication.getApplicationId()+".fileprovider";
        }
    }


    /**
     * 分页设置
     */
    public static class Page {
        public static final int START_PAGE = 1;
        public static final String COMMON_PAGE_SIZE = "20";
    }

    /**
     * 网络请求设置
     */
    public static class Http {
        public static final int CONNECT_TIME_OUT = 5;
        public static final int READ_TIME_OUT = 300;
    }

    public static class Image {
        public static final int THUMB_SIZE = 300;
        public static final int IMAGE_SIZE = 1024;

        public static final int IS_LOCAL_IMAGE = 1;
        public static final int IS_NET_IMAGE = 0;

        public static final String PNG = ".png";
        public static final String JPEG = ".jpg";
    }

    /**
     * 图片处理类型
     */
    public class ImageType {
        public static final int CAMERA = 1;
        public static final int PHOTO = 2;
        public static final int CROP = 3;
    }

    public static class UpgradeInfo {
        public static final String OS_TYPE = "安卓";
        public static final String APP_NAME = "楷模家APP";
    }

    /**
     * 优惠券状态
     */
    public static class CouponStatus {
        public static final String COUPON_STATUS_KEY = "coupon_status";
        public static final int FRESH = 0;
        public static final int ALREADY = 1;
        public static final int INVALID = 2;
    }

    /**
     * 消息类型
     */
    public static class NewsType {
        /**
         * 默认文本消息
         */
        public static final int ANNOUNCEMENT = 0;

        /**
         * 活动详情
         */
        public static final int ACTIVITY = 1;

        /**
         * 商品详情
         */
        public static final int GOOD_DETAIL = 2;

        /**
         * 分类列表
         */
        public static final int CATEGORY = 3;

        /**
         * 订单详情
         */
        public static final int ORDER = 4;

        /**
         * 售后详情
         */
        public static final int AFTER_SALE = 5;

        /**
         * 优惠券消息
         */
        public static final int COUPON = 6;
    }

    public static class OrderAddress {
        /**
         * 是默认收货地址
         */
        public static final int IS_DEFAULT = 1;

        /**
         * 不是默认收货地址
         */
        public static final int NOT_DEFAULT = 0;
    }


    /**
     * 性别
     */
    public static class SexType {

        public static final int SECRET = 0;

        public static final int MALE = 1;

        public static final int FEMALE = 2;

        public static final String SECRET_STR = "保密";

        public static final String MALE_STR = "男";

        public static final String FEMALE_STR = "女";

        public static String getStr(int sexType) {
            switch (sexType) {
                case SECRET:
                    return SECRET_STR;
                case MALE:
                    return MALE_STR;
                case FEMALE:
                    return FEMALE_STR;
                default:
                    return SECRET_STR;
            }
        }

        public static int getInt(String sexType) {
            switch (sexType) {
                case SECRET_STR:
                    return SECRET;
                case MALE_STR:
                    return MALE;
                case FEMALE_STR:
                    return FEMALE;
                default:
                    return SECRET;
            }
        }
    }


    /**
     * 轮播图类型
     */
    public static class BannerType {
        //0:纯图片,不能跳转; 1:跳转到商品详情页; 2:跳转到活动详情页; 3:跳转到商品列表页; 4:跳转到外部网页;5:跳转注册;6:优惠券
        public static final int PURE_IMAGE = 0;
        public static final int GOOD_DETAIL = 1;
        public static final int ACTIVITY_DETAIL = 2;
        public static final int GOOD_LIST = 3;
        public static final int WEB = 4;
        public static final int REG = 5;
        public static final int COUPON = 6;
    }

    /**
     * 用户来源
     */
    public static class UserSource {
        public static final int MOBILE = 0;
        public static final int WEIXIN = 1;
    }

    /**
     * 订单状态
     */
    public static class OrderStatus {
        public static final int ORDER_STATUS_INT_ALL = 0;

        public static final int ORDER_STATUS_INT_WAITING_PAY = 1;//待付款
        public static final int ORDER_STATUS_INT_WAITING_SEND = 2;//待发货
        public static final int ORDER_STATUS_INT_ALREADY_SEND = 3;//已发货
        public static final int ORDER_STATUS_INT_WAITING_COMMIT = 4;//非订单实际拥有的状态-通过【订单接口】获取待评价使用的【字段值】
        public static final int ORDER_STATUS_INT_DONE = 5;//交易成功
        public static final int ORDER_STATUS_INT_CANCEL = 6;//已取消
        public static final int ORDER_STATUS_INT_IN_AFTER_SALE = 7;//参与售后

        public static final String ORDER_STATUS_WAITING_PAY = "待付款";
        public static final String ORDER_STATUS_WAITING_SEND = "待发货";
        public static final String ORDER_STATUS_ALREADY_SEND = "已发货";
        public static final String ORDER_STATUS_WAITING_COMMIT = "待评价";
        public static final String ORDER_STATUS_DONE = "交易成功";
        public static final String ORDER_STATUS_CANCEL = "已取消";


        //售后部分
        public static final int ORDER_AF_STATUS_INT_RECORD = -1;

        public static final int ORDER_AF_STATUS_INT_WAITING_SERVICE = 0;//申请已提交，等待受理
        public static final int ORDER_AF_STATUS_INT_WAITING_LOGISTIC = 1;//客服已受理，前往填写物流
        public static final int ORDER_AF_STATUS_INT_WAITING_RETURN = 3;//收货已确认,前往收货确认页面
        public static final int ORDER_AF_STATUS_INT_DONE_RETURN = 4;//入库完成退款，前往完成退款页面
        public static final int ORDER_AF_STATUS_INT_CANCEL = 5;//用户取消
        public static final int ORDER_AF_STATUS_INT_REJECT = 2;//拒绝
        public static final int ORDER_AF_STATUS_INT_WAITING_GOODS = 6;//等待收货，前往收货确认页面

        public static final String ORDER_AF_STATUS_STR_WAITING_SERVICE = "等待受理";
        public static final String ORDER_AF_STATUS_STR_WAITING_LOGISTIC = "物流选择";
        public static final String ORDER_AF_STATUS_STR_WAITING_GOODS = "收货确认";
        public static final String ORDER_AF_STATUS_STR_REJECT = "拒绝";
        public static final String ORDER_AF_STATUS_STR_WAITING_RETURN = "已收货";
        public static final String ORDER_AF_STATUS_STR_DONE_RETURN = "完成退款";
        public static final String ORDER_AF_STATUS_STR_CANCEL = "用户取消";


    }

    /**
     * 订单是否评价过
     */
    public static class CommentType {
        public static final int IS_COMMENTED = 1;
        public static final int NO_COMMENTED = 0;
    }

    /**
     * 售后页签
     */
    public static class AfterSaleType {
        public static final int AFTER_SALE_RECORD = 0;
        public static final int AFTER_SALE_RETURN = 1;
    }

    /**
     * 规则页面的展示类型
     */
    public static class RegularActivityType {
        public static final String REGULAR_ACTIVITY_TYPE_ACTIVITY_REGULAR = "活动规则";
        public static final String REGULAR_ACTIVITY_TYPE_WELFARE = "公益活动";
        public static final String REGULAR_ACTIVITY_TYPE_ANNOUNCEMENT = "默认消息";
        public static final String REGULAR_ACTIVITY_TYPE_ABOUT_US = "关于我们";
        public static final String REGULAR_ACTIVITY_TYPE_PROTOCOL = "服务协议";
        public static final String REGULAR_ACTIVITY_TYPE_INVITED = "邀请规则";
    }

    /**
     * 商品id的类型
     */
    public static class GoodDetailIdType {
        /**
         * 商品主键
         */
        public static final String ID = "id";
        /**
         * 商品货号
         */
        public static final String SN = "sn";
    }

    /**
     * 四大一级分类的中文
     */
    public static class MainCateType {
        public static final String CATEGORY_STR_KITCHEN = "乐活餐厨";
        public static final String CATEGORY_STR_LIFE = "品味生活";
        public static final String CATEGORY_STR_TEXTILE = "甄选家纺";
        public static final String CATEGORY_STR_BATHROOM = "稀奇淘";
    }

    /**
     * 今日是否已签到
     */
    public static class SignType {
        public static final int NOT_SIGN_TODAY = 0;
        public static final int ALEADY_SING_TODAY = 1;
    }

    /**
     * 已收藏
     */
    public static final int GOOD_COLLECT = 1;
    public static final int GOOD_COLLECT_NO = 0;

    /**
     * 选择页面的列表类型
     */
    public static class ListChooseType {
        public static final String ADDRESS = "选择收货地址";
        public static final int ADDRESS_INT = 10;

        public static final String BONUS = "选择优惠券";
        public static final int BONUS_INT = 20;
    }

    /**
     * 购物车添加的商品类型
     */
    public static class GoodType {

        //单品
        public static final int SINGLE = 0;

        //组合
        public static final int COMBINE = 1;
    }

    /**
     * 购物车的商品是否勾选
     */
    public static class CartChecked {

        //未选中
        public static final int NOT_CHECKED = 0;

        //选中
        public static final int IS_CHECKED = 1;
    }

    /**
     * 售后退货方式
     */
    public static class ShippingType {

        //快递
        public static final String DELIVERY = "0";

        //自送
        public static final String PICKSELF = "1";
    }


    /**
     * 发票类型
     */
    public static class ReceiptType {

        public static String[] TYPE_STR= new String[]{"不需要发票", "个人", "公司"};

        //默认不开发票
        public static final int  NONE= 0;

        //个人
        public static final int PERSON = 1;

        //公司
        public static final int COMPANY = 2;
    }

    public static class LogisticState {
        //https://www.kuaidi100.com/openapi/api_post.shtml

        //揽件
        public static final String FETCH = "1";
        public static final int INT_FETCH = 0;

        //在途
        public static final String ONWAY = "0";
        public static final int INT_ONWAY = 1;

        //派件
        public static final String PUSH = "5";
        public static final int INT_PUSH = 2;

        //签收
        public static final String SIGN = "3";
        public static final int INT_SIGN = 3;

        //疑难2
        //退签4
        //退回6

        public static int transform(String state) {
            switch (state) {
                case FETCH:
                    return INT_FETCH;
                case ONWAY:
                    return INT_ONWAY;
                case PUSH:
                    return INT_PUSH;
                case SIGN:
                    return INT_SIGN;
                default:
                    return -1;
            }
        }

    }

}
