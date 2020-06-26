package com.example.foodcraze.ui.menu;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodcraze.Callback.CategoryCallbackListener;
import com.example.foodcraze.Common.Common;
import com.example.foodcraze.Model.categoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuViewModel extends ViewModel implements CategoryCallbackListener {

    private MutableLiveData<List<categoryModel>> categoryListMutable;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private CategoryCallbackListener categoryCallbackListener;

    public MenuViewModel() {
        categoryCallbackListener = this;

    }

    public MutableLiveData<List<categoryModel>> getCategoryListMutable() {
        if(categoryListMutable == null)
        {
            categoryListMutable = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadCategories();
        }
        return categoryListMutable;
    }

    private void loadCategories() {
        List<categoryModel> tempList =new ArrayList<>();
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapShot:dataSnapshot.getChildren())
                {
                    categoryModel categoryModel = dataSnapShot.getValue(categoryModel.class);
                    categoryModel.setMenu_id(dataSnapShot.getKey());
                    tempList.add(categoryModel);
                }

                categoryCallbackListener.onCategorylLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                 categoryCallbackListener.onCategoryLoadFailed(databaseError.getMessage());
            }
        });

    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onCategorylLoadSuccess(List<categoryModel> categoryModels) {
         categoryListMutable.setValue(categoryModels);
    }

    @Override
    public void onCategoryLoadFailed(String message) {
         messageError.setValue(message);
    }
}