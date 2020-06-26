package com.example.foodcraze.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodcraze.Callback.BestDealCallbackListener;
import com.example.foodcraze.Callback.PopularCallbackListener;
import com.example.foodcraze.Common.Common;
import com.example.foodcraze.Model.BestDealModel;
import com.example.foodcraze.Model.PopularCategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeViewModel extends ViewModel implements PopularCallbackListener, BestDealCallbackListener {

    private MutableLiveData<List<PopularCategoryModel>> popularList;
    private MutableLiveData<List<BestDealModel>> bestDealList;
    private MutableLiveData<String> messageError;
    private PopularCallbackListener popularCallbackListener;
    private BestDealCallbackListener bestDealCallbackListener;

    public HomeViewModel() {
       popularCallbackListener = this;
       bestDealCallbackListener = this;
    }

    public MutableLiveData<List<BestDealModel>> getBestDealList() {
        if (bestDealList == null)
        {
           bestDealList = new MutableLiveData<>();
           messageError = new MutableLiveData<>();
           loadBestDealList ();
        }
      return bestDealList;
    }

    private void loadBestDealList() {
        final List<BestDealModel> tempList = new ArrayList<>();
        DatabaseReference bestDealRef = FirebaseDatabase.getInstance().getReference(Common.BEST_DEALS_REF);
        bestDealRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemsnapshot:dataSnapshot.getChildren()){
                    BestDealModel model = itemsnapshot.getValue(BestDealModel.class);
                    tempList.add(model);
                }
                bestDealCallbackListener.onBestDealLoadSucess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
              bestDealCallbackListener.onBestDealLoadFailed(databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<List<PopularCategoryModel>> getPopularList() {
        if(popularList == null){
            popularList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadPopularList();
        }
        return popularList;
    }

    private void loadPopularList() {
        final List<PopularCategoryModel> tempList = new ArrayList<>();
        DatabaseReference popularRef = FirebaseDatabase.getInstance().getReference(Common.POPULAR_CATEGORY_REF);
        popularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for(DataSnapshot itemsnapshot:dataSnapshot.getChildren()){
                   PopularCategoryModel model = itemsnapshot.getValue(PopularCategoryModel.class);
                   tempList.add(model);
               }
               popularCallbackListener.onPopularLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                popularCallbackListener.onPopularLoadFailed(databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onPopularLoadSuccess(List<PopularCategoryModel> popularCategoryModels) {
       popularList.setValue(popularCategoryModels);
    }

    @Override
    public void onPopularLoadFailed(String message) {
           messageError.setValue(message);
    }

    @Override
    public void onBestDealLoadSucess(List<BestDealModel> bestDealModels) {
       bestDealList.setValue(bestDealModels);
    }

    @Override
    public void onBestDealLoadFailed(String message) {
      messageError.setValue(message);
    }
}