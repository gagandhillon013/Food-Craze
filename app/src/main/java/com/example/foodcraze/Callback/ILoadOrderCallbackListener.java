package com.example.foodcraze.Callback;

import com.example.foodcraze.Model.Order;

import java.util.List;

public interface ILoadOrderCallbackListener {
    void onLoadOrderSuccess(List<Order> orderList);
    void onLoadOrderFailed(String message);

}
