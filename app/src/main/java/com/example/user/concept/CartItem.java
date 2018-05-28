package com.example.user.concept;

public class CartItem {
    private String cartProductName;
    private String cartProductSeller;
    private String cartImageLocation;

    public String getCartImageLocation() {
        return cartImageLocation;
    }

    public void setCartImageLocation(String cartImageLocation) {
        this.cartImageLocation = cartImageLocation;
    }

    private double cartProductPrice;
    private int cartQuantity;

    public int getCartQuantity() {
        return cartQuantity;
    }

    public void setCartQuantity(int cartQuantity) {
        this.cartQuantity = cartQuantity;
    }

    private int cartItemId;
    private int cartProductId;

    public int getCartProductId() {
        return cartProductId;
    }

    public void setCartProductId(int cartProductId) {
        this.cartProductId = cartProductId;
    }

    public double getCartProductPrice() {
        return cartProductPrice;
    }

    public void setCartProductPrice(double cartProductPrice) {
        this.cartProductPrice = cartProductPrice;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public String getCartProductName() {
        return cartProductName;
    }

    public void setCartProductName(String cartProductName) {
        this.cartProductName = cartProductName;
    }

    public String getCartProductSeller() {
        return cartProductSeller;
    }

    public void setCartProductSeller(String cartProductSeller) {
        this.cartProductSeller = cartProductSeller;
    }
}
