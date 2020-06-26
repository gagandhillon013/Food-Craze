package com.example.foodcraze.Callback;
import com.example.foodcraze.Model.categoryModel;

import java.util.List;

public interface CategoryCallbackListener {
    void onCategorylLoadSuccess(List<categoryModel> categoryModels);
    void onCategoryLoadFailed(String message);
}
