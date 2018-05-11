package com.wingplus.coomohome.web.entity;

/**
 * 发票信息
 *
 * @author leaffun.
 *         Create on 2017/11/13.
 */
public class Receipt {
    /**
     * 发票类型
     */
    private int receiptType;
    /**
     * 发票抬头
     */
    private String title;
    /**
     * 税号
     */
    private String texNo;
    /**
     * 公司地址
     */
    private String companyAddr;
    /**
     * 公司银行
     */
    private String companyBank;
    /**
     * 公司银行账户
     */
    private String companyBankNo;
    /**
     * 公司电话
     */
    private String companyTel;

    public int getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(int receiptType) {
        this.receiptType = receiptType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTexNo() {
        return texNo;
    }

    public void setTexNo(String texNo) {
        this.texNo = texNo;
    }

    public String getCompanyAddr() {
        return companyAddr;
    }

    public void setCompanyAddr(String companyAddr) {
        this.companyAddr = companyAddr;
    }

    public String getCompanyBank() {
        return companyBank;
    }

    public void setCompanyBank(String companyBank) {
        this.companyBank = companyBank;
    }

    public String getCompanyBankNo() {
        return companyBankNo;
    }

    public void setCompanyBankNo(String companyBankNo) {
        this.companyBankNo = companyBankNo;
    }

    public String getCompanyTel() {
        return companyTel;
    }

    public void setCompanyTel(String companyTel) {
        this.companyTel = companyTel;
    }
}
