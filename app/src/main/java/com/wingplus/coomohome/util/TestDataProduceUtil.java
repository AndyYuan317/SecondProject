package com.wingplus.coomohome.util;

import com.wingplus.coomohome.R;
import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.fragment.main.CartFragment;
import com.wingplus.coomohome.fragment.order.OrderBaseFragment;
import com.wingplus.coomohome.web.Dialog;
import com.wingplus.coomohome.web.GoodIntro;
import com.wingplus.coomohome.web.News;
import com.wingplus.coomohome.web.entity.OrderAddress;
import com.wingplus.coomohome.web.Promotion;
import com.wingplus.coomohome.web.Service;
import com.wingplus.coomohome.web.entity.Welfare;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.entity.Order;
import com.wingplus.coomohome.web.entity.RechargeRecord;
import com.wingplus.coomohome.web.entity.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * 生产测试数据
 *
 * @author leaffun.
 *         Create on 2017/9/26.
 */
public class TestDataProduceUtil {
    public static final String url = "http://flv2.bn.netease.com/tvmrepo/2017/3/K/I/ECF9KFDKI/SD/ECF9KFDKI-mobile.mp4";


    /**
     * 获取我的服务项
     *
     * @return
     */
    public static List<Service> getServices() {
        List<Service> list = new ArrayList<>();
//        Service s1 = new Service();
//        s1.setImg(R.drawable.icon_wallet);
//        s1.setName("钱包");
//        s1.setType(0);

        Service s2 = new Service();
        s2.setImg(R.drawable.icon_coupon);
        s2.setName("我的优惠券");
        s2.setType(1);

        Service s3 = new Service();
        s3.setImg(R.drawable.icon_integral);
        s3.setName("我的积分");
        s3.setType(2);

        Service s4 = new Service();
        s4.setImg(R.drawable.icon_news);
        s4.setName("我的消息");
        s4.setType(3);

        Service s5 = new Service();
        s5.setImg(R.drawable.icon_collection);
        s5.setName("我的收藏");
        s5.setType(4);

        Service s6 = new Service();
        s6.setImg(R.drawable.icon_store);
        s6.setName("查找门店");
        s6.setType(5);

        Service s7 = new Service();
        s7.setImg(R.drawable.icon_publicwelfare);
        s7.setName("公益活动");
        s7.setType(6);

        Service s8 = new Service();
        s8.setImg(R.drawable.icon_gift);
        s8.setName("礼品中心");
        s8.setType(7);

        Service s9 = new Service();
        s9.setImg(R.drawable.icon_address);
        s9.setName("收货地址");
        s9.setType(8);

        Service s10 = new Service();
        s10.setImg(R.drawable.icon_customerservice);
        s10.setName("我的客服");
        s10.setType(9);

        Service s11 = new Service();
        s11.setImg(R.drawable.icon_invitingfriends);
        s11.setName("邀请好友");
        s11.setType(10);

        Service s12 = new Service();
        s12.setImg(R.drawable.icon_set);
        s12.setName("设置");
        s12.setType(11);

//        list.add(s1);
        list.add(s2);
        list.add(s3);
        list.add(s4);
        list.add(s5);
        list.add(s6);
        list.add(s7);
        list.add(s8);
        list.add(s9);
        list.add(s10);
        list.add(s11);
        list.add(s12);

        return list;
    }

    /**
     * 获取我的订单项
     *
     * @return
     */
    public static List<Service> getOrderItem() {
        List<Service> list = new ArrayList<>();
        Service s1 = new Service();
        s1.setImg(R.drawable.icon_payment);
        s1.setName("待付款");
        s1.setType(-1);

        Service s2 = new Service();
        s2.setImg(R.drawable.icon_shipment);
        s2.setName("待发货");
        s2.setType(-2);

        Service s3 = new Service();
        s3.setImg(R.drawable.icon_shipped);
        s3.setName("待收货");
        s3.setType(-3);

        Service s4 = new Service();
        s4.setImg(R.drawable.icon_evaluation);
        s4.setName("待评价");
        s4.setType(-4);

        Service s5 = new Service();
        s5.setImg(R.drawable.icon_customer);
        s5.setName("售后");
        s5.setType(-5);

        list.add(s1);
        list.add(s2);
        list.add(s3);
        list.add(s4);
        list.add(s5);

        return list;
    }


    /**
     * 获取专题活动
     *
     * @return
     */
    private static Promotion getPromotion() {
        Promotion promotion = new Promotion();
        promotion.setTag("中秋钜惠");
        promotion.setName("买特定商品送中秋福袋");
        promotion.setType(CartFragment.ACTIVITY_TYPE_SPECIAL);
        return promotion;
    }

    /**
     * 获取优惠套装活动
     *
     * @return
     */
    private static Promotion getCombind() {
        Promotion promotion = new Promotion();
        promotion.setTag("优惠套装");
        promotion.setName("组合优惠");
        promotion.setType(CartFragment.ACTIVITY_TYPE_COMBIND);
        return promotion;
    }

    /**
     * 获取日常优惠活动
     *
     * @return
     */
    private static Promotion getDaily() {
        Promotion promotion = new Promotion();
        promotion.setTag("日常优惠");
        promotion.setName("本周八折优惠");
        promotion.setType(CartFragment.ACTIVITY_TYPE_DAILY);
        return promotion;
    }

    /**
     * 获取商品列表
     *
     * @return
     */
    private static List<GoodShow> getGoods() {
        GoodShow goodIntro = new GoodShow();
        goodIntro.setName("意大利Smeg超美保温电水壶");
        goodIntro.setDescription("4分钟烧开一壶水");
        goodIntro.setDescription("黑色 精选包装");
        goodIntro.setPrice(1399);
        goodIntro.setNum(1);
        ArrayList<String> imgUrl = new ArrayList<>();
        imgUrl.add("R.drawable.load_err_empty");
        goodIntro.setImgUrl(imgUrl);

        List<GoodShow> list = new ArrayList<>();
        list.add(goodIntro);
        return list;
    }

    /**
     * 获取组合套装商品列表
     *
     * @return
     */
    private static List<GoodShow> getCombindGoods() {
        GoodShow goodIntro = new GoodShow();
        goodIntro.setName("意大利Smeg超美保温电水壶");
        goodIntro.setDescription("黑色 精选包装");
        goodIntro.setPrice(1399);
        goodIntro.setNum(1);
        ArrayList<String> imgUrl = new ArrayList<>();
        imgUrl.add("R.drawable.load_err_empty");
        goodIntro.setImgUrl(imgUrl);

        GoodShow goodIntro2 = new GoodShow();
        goodIntro2.setName("玻璃杯");
        goodIntro2.setDescription("黑色 精选包装");
        goodIntro2.setPrice(1399);
        goodIntro2.setNum(1);
        goodIntro2.setImgUrl(imgUrl);

        List<GoodShow> list = new ArrayList<>();
        list.add(goodIntro2);
        list.add(goodIntro);
        return list;
    }

    /**
     * 获取猜你喜欢商品列表
     *
     * @return
     */
    public static List<GoodIntro> getLikeGoods() {
        GoodIntro goodIntro = new GoodIntro();
        goodIntro.setGoodName("意大利Smeg超美保温电水壶");
        goodIntro.setBrief("4分钟烧开一壶水");
        goodIntro.setPrice(1399);
        goodIntro.setOrgPrice(1799);
        goodIntro.setPromotionName("中秋钜惠");
        goodIntro.setSquareImg(R.drawable.load_err_empty);

        List<GoodIntro> list = new ArrayList<>();
        list.add(goodIntro);
        list.add(goodIntro);
        list.add(goodIntro);
        list.add(goodIntro);
        return list;
    }



    private static Order getWaitingPayOrder() {
        Order order = getOrder();
        order.setState(Constant.OrderStatus.ORDER_STATUS_INT_WAITING_PAY);
        return order;
    }


    private static Order getWaitingSendOrder() {
        Order order = getOrder();
        order.setState(Constant.OrderStatus.ORDER_STATUS_INT_WAITING_SEND);
        return order;
    }


    private static Order getAlreadySendOrder() {
        Order order = getOrder();
        order.setState(Constant.OrderStatus.ORDER_STATUS_INT_ALREADY_SEND);
        return order;
    }

    private static Order getWaitingCommitOrder() {
        Order order = getOrder();
        order.setState(Constant.OrderStatus.ORDER_STATUS_INT_WAITING_COMMIT);
        return order;
    }

    private static Order getDoneOrder() {
        Order order = getOrder();
        order.setState(Constant.OrderStatus.ORDER_STATUS_INT_DONE);
        return order;
    }

    private static Order getCancelOrder() {
        Order order = getOrder();
        order.setState(Constant.OrderStatus.ORDER_STATUS_INT_CANCEL);
        return order;
    }


    public static Order getOrder() {
        Order order = new Order();
        order.setCode("0A00100000005");
        order.setState(Constant.OrderStatus.ORDER_STATUS_INT_WAITING_PAY);
        order.setDiscount(-50d);
        order.setNeedPayAmout(3999);
        order.setFreight(20);
        order.setOrderTime(System.currentTimeMillis());
        order.setPayAmount(3999);
        order.setPayment("支付宝");
        order.setGoods(getGoods());


        order.setOrderAddress(getOrderAddress().get(0));

        return order;
    }

    public static List<OrderAddress> getOrderAddress() {
        OrderAddress address = new OrderAddress();
        address.setPerson("砺学");
        address.setPhone("13866668888");
        address.setProvince("湖南省长沙市");
        address.setAddress("芙蓉南路BOBO天下城213号068");
        address.setAddress("湖南省长沙市芙蓉区");

        List<OrderAddress> oas = new ArrayList<>();
        OrderAddress add = new OrderAddress();
        add.setPerson("砺学");
        add.setPhone("13866668888");
        add.setProvince("湖南省长沙市");
        add.setAddress("芙蓉南路BOBO天下城213号068");
        add.setAddress("湖南省长沙市芙蓉区");
        add.setIsDefault(Constant.OrderAddress.IS_DEFAULT);
        oas.add(add);

        for (int i = 0; i < 11; i++) {
            address.setIsDefault(Constant.OrderAddress.NOT_DEFAULT);
            oas.add(address);
        }

        return oas;
    }


    public static List<RechargeRecord> getIntegralRecord() {
        List<RechargeRecord> rrs = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            RechargeRecord record = new RechargeRecord();
            record.setDate("2017.09.0" + i);
            record.setAmount("+30");
            rrs.add(record);
        }
        return rrs;
    }

    public static List<News> getNews() {
        List<News> ns = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            News news = new News();
            news.setTime(System.currentTimeMillis());
            news.setTitle("精挑细选");
            news.setDes("用设计让你回归自然，精挑细选限时买，抢");
            news.setImg("http://upload-images.jianshu.io/upload_images/1693344-3c891480f5ab3676.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240");
            news.setType(i % 5);
            ns.add(news);
        }
        return ns;
    }


    public static List<Welfare> getWelfare() {
        List<Welfare> ws = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            Welfare w = new Welfare();
            w.setTime(System.currentTimeMillis());
            w.setActivity_name("【公益活动】 益点心意");
            w.setActivity_desp("用设计让你回归自然，精挑细选限时买，抢");
            w.setActivity_img("http://upload-images.jianshu.io/upload_images/1693344-3c891480f5ab3676.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240");
            w.setType(Constant.NewsType.ANNOUNCEMENT);
            ws.add(w);
        }
        return ws;
    }

    public static List<Store> getStores() {
        List<Store> ss = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            Store s = new Store();
            s.setCover("http://upload-images.jianshu.io/upload_images/1693344-dd48d290dc2af904.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240");
            s.setLocation("上海市闵行区真北路");
            s.setName("楷模家居真北店");
            s.setDistance("4.5km");
            ss.add(s);
        }
        return ss;
    }

    public static List<Dialog> getDialog() {
        List<Dialog> dl = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Dialog c = new Dialog();
            c.setStaff("客服嘟嘟");
            c.setStaffImage("http://upload-images.jianshu.io/upload_images/1693344-dd48d290dc2af904.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240");
            c.setStaffNews("亲，您好，请问有什么可以帮助您的？");

            Dialog u = new Dialog();
            if (i % 4 == 0) {
                u.setSendTime(System.currentTimeMillis());
            }
            u.setUser("我");
            u.setUserImage("http://upload-images.jianshu.io/upload_images/1693344-dd48d290dc2af904.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240");
            u.setUserNews("您好，我想请问一下这个怎么卖？有什么优惠活动吗？");

            dl.add(c);
            dl.add(u);
        }
        return dl;
    }
}
