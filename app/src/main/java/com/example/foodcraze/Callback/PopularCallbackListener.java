package com.example.foodcraze.Callback;

import com.example.foodcraze.Model.PopularCategoryModel;

import java.util.List;

public interface PopularCallbackListener {
    void onPopularLoadSuccess(List<PopularCategoryModel> popularCategoryModels);
    void onPopularLoadFailed(String message);
}
