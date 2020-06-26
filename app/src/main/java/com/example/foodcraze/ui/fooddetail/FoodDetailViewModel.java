package com.example.foodcraze.ui.fooddetail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodcraze.Common.Common;
import com.example.foodcraze.Model.FoodModel;
import com.example.foodcraze.Model.CommentModel;

public class FoodDetailViewModel extends ViewModel {

    private MutableLiveData<FoodModel> MutableLiveDataFood;
    private MutableLiveData<CommentModel> MutableLiveDataComment;

    public void setCommentModel(CommentModel commentModel){
        if(MutableLiveDataComment !=null)
            MutableLiveDataComment.setValue(commentModel);
    }

    public MutableLiveData<CommentModel> getMutableLiveDataComment() {
        return MutableLiveDataComment;
    }

    public FoodDetailViewModel() {
        MutableLiveDataComment = new MutableLiveData<>();

    }

    public MutableLiveData<FoodModel> getModelMutableLiveDataFood() {
        if(MutableLiveDataFood == null)
            MutableLiveDataFood = new MutableLiveData<>();
        MutableLiveDataFood.setValue(Common.selectedFood);
        return MutableLiveDataFood;
    }

    public void setFoodModel(FoodModel foodModel) {
        if(MutableLiveDataFood != null)
            MutableLiveDataFood.setValue(foodModel);
    }
}