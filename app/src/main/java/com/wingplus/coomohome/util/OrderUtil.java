package com.wingplus.coomohome.util;

import com.wingplus.coomohome.config.Constant;
import com.wingplus.coomohome.web.entity.Category;
import com.wingplus.coomohome.web.entity.GoodShow;
import com.wingplus.coomohome.web.entity.GoodSpec;
import com.wingplus.coomohome.web.entity.HomeFloor;
import com.wingplus.coomohome.web.entity.OrderAddress;
import com.wingplus.coomohome.web.entity.TypeSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 排序
 *
 * @author leaffun.
 *         Create on 2017/10/19.
 */
public class OrderUtil {

    public static void order(List<GoodShow> gs) {
        if (gs != null && gs.size() > 0) {
            for (int x = 0; x < gs.size() - 1; x++) {
                int min = x;
                for (int y = x + 1; y < gs.size(); y++) {
                    if (gs.get(y).getOrder() < gs.get(min).getOrder()) {
                        min = y;
                    }
                }
                if (min != x) {
                    GoodShow smaller = gs.get(min);
                    GoodShow bigger = gs.get(x);

                    gs.remove(min);
                    gs.add(min, bigger);
                    gs.remove(x);
                    gs.add(x, smaller);
                }
            }
        }
    }

    public static void orderCate(List<Category> gs) {
        if (gs != null && gs.size() > 0) {
            for (int x = 0; x < gs.size() - 1; x++) {
                int min = x;
                for (int y = x + 1; y < gs.size(); y++) {
                    if (gs.get(y).getOrder() < gs.get(min).getOrder()) {
                        min = y;
                    }
                }
                if (min != x) {
                    Category smaller = gs.get(min);
                    Category bigger = gs.get(x);

                    gs.remove(min);
                    gs.add(min, bigger);
                    gs.remove(x);
                    gs.add(x, smaller);
                }
            }
        }
    }

    public static void orderAddress(List<OrderAddress> list) {
        if (list != null && list.size() > 0) {
            OrderAddress def = null;
            for (OrderAddress orderAddress : list) {
                if (orderAddress.getIsDefault() == Constant.OrderAddress.IS_DEFAULT) {
                    def = orderAddress;
                }
            }
            if (def != null) {
                list.remove(def);
                list.add(0, def);
            }
        }
    }

    public static void orderHomeFloor(List<HomeFloor> floors) {
        if (floors != null && floors.size() > 0) {
            List<HomeFloor> homeFloors = new ArrayList<>(floors.size());
            for (int i = 0; i < floors.size(); i++) {
                homeFloors.add(new HomeFloor());
            }

            for (HomeFloor hf : floors) {
                homeFloors.add(hf.getOrder(), hf);
            }
            floors = homeFloors;
        }
    }


    /**
     * 将商品属性分组
     *
     * @param goodSpecs
     */
    public static double[] prepareSpecList(List<GoodSpec> goodSpecs, List<Map.Entry<String, List<String>>> container) {
        double[] minAndMax = new double[]{-1, -1};
        if (goodSpecs != null && goodSpecs.size() > 0) {
            List<String> index = new ArrayList<>();
            LinkedHashMap<String, List<String>> typeAttr = new LinkedHashMap<>(); //保证存储顺序
            for (GoodSpec goodSpec : goodSpecs) { //遍历所有商品的规格
                double price = goodSpec.getPrice();

                if (minAndMax[0] == -1) {//没有最小值
                    minAndMax[0] = price;
                } else {
                    if (minAndMax[1] == -1) {//没有最大值
                        if (price < minAndMax[0]) {
                            minAndMax[1] = minAndMax[0];
                            minAndMax[0] = price;
                        } else if(price > minAndMax[0]){
                            minAndMax[1] = price;
                        }
                    } else {
                        if(price < minAndMax[0]){
                            minAndMax[0] = price;
                        }else if(price > minAndMax[1]){
                            minAndMax[1] = price;
                        }
                    }
                }

                for (TypeSpec typeSpec : goodSpec.getSpec()) { //遍历所有规格的属性
                    if (typeAttr.containsKey(typeSpec.getType())) { //按属性名称存储
                        if (!typeAttr.get(typeSpec.getType()).contains(typeSpec.getValue())) {
                            List<String> strings = typeAttr.get(typeSpec.getType());
                            strings.add(typeSpec.getValue());
                            Collections.sort(strings);
                        }
                    } else {
                        List<String> values = new ArrayList<>();
                        values.add(typeSpec.getValue());
                        typeAttr.put(typeSpec.getType(), values);
                        index.add(typeSpec.getType());
                    }
                }
            }

            //排序
            Collections.sort(index);
            LinkedHashMap<String, List<String>> temp = new LinkedHashMap<>();
            for (int i = 0; i < index.size(); i++) {
                temp.put(index.get(i), typeAttr.get(index.get(i)));
            }

            //整理列表
            for (Map.Entry<String, List<String>> entry : temp.entrySet()) {
                container.add(entry);
            }
        }
        return minAndMax;
    }

}
