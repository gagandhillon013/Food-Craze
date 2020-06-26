package com.example.foodcraze.ui.view_orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodcraze.Model.Order;

import java.util.List;

public class viewOrderViewModel extends ViewModel {

    private MutableLiveData<List<Order>> mutableLiveDataOrderList;
    public viewOrderViewModel() {
      mutableLiveDataOrderList = new MutableLiveData<>();
    }

    public MutableLiveData<List<Order>> getMutableLiveDataOrderList() {
        return mutableLiveDataOrderList;
    }

    public void setMutableLiveDataOrderList(List<Order> orderList) {
        mutableLiveDataOrderList.setValue(orderList);
    }
}