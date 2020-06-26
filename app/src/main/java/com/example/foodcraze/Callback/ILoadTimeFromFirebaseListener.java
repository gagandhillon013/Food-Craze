package com.example.foodcraze.Callback;

import com.example.foodcraze.Model.Order;

public interface ILoadTimeFromFirebaseListener {
    void onLoadTimeSuccess(Order order,long estimateTimeInMs);
    void onLoadTimeFailed(String message);
}
