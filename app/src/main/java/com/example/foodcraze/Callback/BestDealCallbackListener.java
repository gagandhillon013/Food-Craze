package com.example.foodcraze.Callback;

import com.example.foodcraze.Model.BestDealModel;


import java.util.List;
public interface BestDealCallbackListener {
    void onBestDealLoadSucess(List<BestDealModel> bestDealModels);
    void onBestDealLoadFailed(String message);
}
