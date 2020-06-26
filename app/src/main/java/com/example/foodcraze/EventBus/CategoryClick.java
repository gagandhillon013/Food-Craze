package com.example.foodcraze.EventBus;

import com.example.foodcraze.Model.categoryModel;

public class CategoryClick {
    private boolean success;
    private categoryModel categoryModel;

    public CategoryClick(boolean success, categoryModel categoryModel) {
        this.success = success;
        this.categoryModel = categoryModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public categoryModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(categoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }
}
