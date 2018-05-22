package com.emall.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVO {
    private List<CartProductVO> cartProductVoList;
    private BigDecimal cartTotalPrice;
    private boolean cartAllChecked;
    private String imageHost;

    public List<CartProductVO> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVO> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public boolean isCartAllChecked() {
        return cartAllChecked;
    }

    public void setCartAllChecked(boolean cartAllChecked) {
        this.cartAllChecked = cartAllChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
