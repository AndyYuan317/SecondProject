package com.wingplus.coomohome.web.entity;

import java.util.List;

/**
 * 商品：规格
 *
 * @author leaffun.
 *         Create on 2017/10/19.
 */
public class GoodSpec {

    /**
     * 商品货号（表示：不同的规格）
     */
    private String productId;

    /**
     * 价格
     */
    private double price;

    /**
     * 库存量
     */
    private int stock;

    private List<TypeSpec> spec;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }


    public List<TypeSpec> getSpec() {
        return spec;
    }

    public void setSpec(List<TypeSpec> spec) {
        this.spec = spec;
    }
}
