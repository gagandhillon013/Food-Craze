package com.example.foodcraze.ui.foodlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodcraze.Adapter.MyFoodListAdapter;
import com.example.foodcraze.Common.Common;
import com.example.foodcraze.Model.FoodModel;
import com.example.foodcraze.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FoodListFragment extends Fragment {

    private FoodListViewModel slideshowViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_food_list)
    RecyclerView recycler_food_list;
    LayoutAnimationController layoutAnimationController;
    MyFoodListAdapter adapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(FoodListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_food_list, container, false);
         unbinder = ButterKnife.bind(this,root);
         initView();
        slideshowViewModel.getMutableLiveDataFoodList().observe(this, new Observer<List<FoodModel>>() {
            @Override
            public void onChanged(List<FoodModel> foodModels) {
                adapter = new MyFoodListAdapter(getContext(),foodModels);
                recycler_food_list.setAdapter(adapter);
                recycler_food_list.setLayoutAnimation(layoutAnimationController);
            }
        });

        return root;
    }

    private void initView() {
        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.CategorySelected.getName());


       recycler_food_list.setHasFixedSize(true);
       recycler_food_list.setLayoutManager(new LinearLayoutManager(getContext()));

       layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
    }
}
