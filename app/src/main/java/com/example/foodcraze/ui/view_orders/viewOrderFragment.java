package com.example.foodcraze.ui.view_orders;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodcraze.Adapter.MyOrderAdapter;
import com.example.foodcraze.Callback.ILoadOrderCallbackListener;
import com.example.foodcraze.Common.Common;
import com.example.foodcraze.Model.Order;
import com.example.foodcraze.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class viewOrderFragment extends Fragment implements ILoadOrderCallbackListener {
    @BindView(R.id.recycler_orders)
    RecyclerView recycler_orders;
    private Unbinder unbinder;
    AlertDialog dialog;

    private viewOrderViewModel viewOrderViewModel;
    private ILoadOrderCallbackListener listener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewOrderViewModel =
                ViewModelProviders.of(this).get(viewOrderViewModel.class);
        View root = inflater.inflate(R.layout.fragment_view_order, container, false);
        unbinder = ButterKnife.bind(this,root);
        initViews(root);
        loadOrderFromFirebase();

        viewOrderViewModel.getMutableLiveDataOrderList().observe(this,orderList -> {
            MyOrderAdapter adapter = new MyOrderAdapter(getContext(),orderList);
            recycler_orders.setAdapter(adapter);
        });
        return root;
    }

    private void loadOrderFromFirebase() {
        List<Order> orderList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .orderByChild("userId")
                .equalTo(Common.currentUser.getUid())
                .limitToLast(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot orderSnapShot:dataSnapshot.getChildren())
                        {
                            Order order = orderSnapShot.getValue(Order.class);
                            order.setOrderNumber(orderSnapShot.getKey());
                            orderList.add(order);
                        }
                        listener.onLoadOrderSuccess(orderList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                         listener.onLoadOrderFailed(databaseError.getMessage());
                    }
                });
    }

    private void initViews(View root) {
        listener = this;
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();

        recycler_orders.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_orders.setLayoutManager(layoutManager);
        recycler_orders.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));
    }

    @Override
    public void onLoadOrderSuccess(List<Order> orderList) {
        dialog.dismiss();
        viewOrderViewModel.setMutableLiveDataOrderList(orderList);
    }

    @Override
    public void onLoadOrderFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(),message, Toast.LENGTH_SHORT).show();
    }
}
