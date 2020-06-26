package com.example.foodcraze.EventBus;

import com.example.foodcraze.Database.CartItem;

public class updateItemInCart {
    private CartItem cartItem;

   public updateItemInCart(CartItem cartItem){
       this.cartItem = cartItem;
   }

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }
}
