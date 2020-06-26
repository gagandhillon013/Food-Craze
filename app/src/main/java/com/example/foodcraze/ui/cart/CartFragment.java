package com.example.foodcraze.ui.cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import com.example.foodcraze.Adapter.MyCartAdapter;
import com.example.foodcraze.Callback.ILoadTimeFromFirebaseListener;
import com.example.foodcraze.Common.Common;
import com.example.foodcraze.Common.MySwipeHelper;
import com.example.foodcraze.Database.CartDataSource;
import com.example.foodcraze.Database.CartDatabase;
import com.example.foodcraze.Database.CartItem;
import com.example.foodcraze.Database.LocalCartDataSource;
import com.example.foodcraze.EventBus.CounterCartEvent;
import com.example.foodcraze.EventBus.HideFABCart;
import com.example.foodcraze.EventBus.updateItemInCart;
import com.example.foodcraze.Model.Order;
import com.example.foodcraze.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CartFragment extends Fragment implements ILoadTimeFromFirebaseListener {

    private Parcelable recyclerViewState;
    private CartDataSource cartDataSource;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    ILoadTimeFromFirebaseListener listener;
   @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
   @BindView(R.id.txt_total_price)
   TextView txt_total_price;
   @BindView(R.id.txt_empty_cart)
    TextView txt_empty_cart;
   @BindView(R.id.group_place_holder)
    CardView  group_place_holder;



   @OnClick(R.id.btn_place_order)
   void onPlaceOrderClick(){
       AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
       builder.setTitle("One more step!");

       View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_place_order,null);

       EditText edt_address = (EditText)view.findViewById(R.id.edt_address);
       RadioButton rdi_home = (RadioButton)view.findViewById(R.id.rdi_home_address);
       RadioButton rdi_other_address = (RadioButton)view.findViewById(R.id.rdi_other_address);
       RadioButton rdi_ship_to_this = (RadioButton)view.findViewById(R.id.rdi_ship_this_address);
       RadioButton rdi_cod = (RadioButton)view.findViewById(R.id.rdi_cod);

       //data
       edt_address.setText(Common.currentUser.getAddress());//default address
       //event
       rdi_home.setOnCheckedChangeListener((compoundButton, b) -> {
           if(b)
           {
               edt_address.setText(Common.currentUser.getAddress());
           }
       });
       rdi_other_address.setOnCheckedChangeListener((compoundButton, b) -> {
           if(b)
           {
               edt_address.setText("");//clear
               edt_address.setHint("Enter your address");

           }
       });
       rdi_ship_to_this.setOnCheckedChangeListener((compoundButton, b) -> {
           if(b)
           {
               Toast.makeText(getContext(), "implement late with google api", Toast.LENGTH_SHORT).show();
           }
       });

       builder.setView(view);
       builder.setNegativeButton("NO",(dialogInterface, i) -> {
            dialogInterface.dismiss();

       }).setPositiveButton("YES",(dialogInterface, i) -> {
          // Toast.makeText(getContext(), "Implement late!", Toast.LENGTH_SHORT).show();
           if (rdi_cod.isChecked())
               paymentCOD(edt_address.getText().toString());
       });

       AlertDialog dialog = builder.create();
       dialog.show();
   }

    private void paymentCOD(String address) {
       compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
       .subscribeOn(Schedulers.io())
       .observeOn(AndroidSchedulers.mainThread())
       .subscribe(cartItems -> {
           //
           cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new SingleObserver<Double>() {
                       @Override
                       public void onSubscribe(Disposable d) {

                       }

                       @Override
                       public void onSuccess(Double totalPrice) {
                            double finalPrice = totalPrice;
                           Order order = new Order();
                           order.setUserId(Common.currentUser.getUid());
                           order.setUserName(Common.currentUser.getName());
                           order.setUserPhone(Common.currentUser.getPhone());
                           order.setShippingAddress(address);
                           order.setCartItemList(cartItems);
                           order.setTotalPayment(totalPrice);
                           order.setDiscount(0);
                           order.setFinalPayment(finalPrice);
                           order.setCod(true);
                           order.setTranactionId("Cash On Delivery");

                           //submit order on firebase
                          syncLocalTimeWithGLobaltime(order);
                       }
                       @Override
                       public void onError(Throwable e) {
                           if (!e.getMessage().contains("Query returned empty")){
                           Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();}
                       }
                   });

       },throwable -> {
           Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
       }));


    }
    private void writeOrderToFirebase(Order order) {
        FirebaseDatabase.getInstance()
                .getReference(Common.ORDER_REF)
                .child(Common.createOrderNumber())//create order number with ony digit
                .setValue(order)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }).addOnCompleteListener(task -> {
            //write succes
            cartDataSource.cleanCart(Common.currentUser.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            // clean successs
                            Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (!e.getMessage().contains("Query returned empty")){
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();}
                        }
                    });
        });

    }

    private void syncLocalTimeWithGLobaltime(Order order) {
       final DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
       offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             long offset = dataSnapshot.getValue(Long.class);
             long estimatedServerTimeMs = System.currentTimeMillis()+offset;
               SimpleDateFormat sdf = new SimpleDateFormat("MM dd,yyyy HH:mm");
               Date resultDate = new Date(estimatedServerTimeMs);
               Log.d("TEST_DATE",""+sdf.format(resultDate));
               listener.onLoadTimeSuccess(order,estimatedServerTimeMs);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               listener.onLoadTimeFailed(databaseError.getMessage());
           }
       });
    }

    private Unbinder unbinder;
    private MyCartAdapter adapter;
    private CartViewModel cartViewModel;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        listener = this;
        cartViewModel.initCartDataSource(getContext());
        cartViewModel.getMutableLiveDataCartItems().observe(this, new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> cartItems) {
                if(cartItems == null || cartItems.isEmpty()){
                   recycler_cart.setVisibility(View.GONE);
                   group_place_holder.setVisibility(View.GONE);
                   txt_empty_cart.setVisibility(View.VISIBLE);
                }
                else {
                    recycler_cart.setVisibility(View.VISIBLE);
                    group_place_holder.setVisibility(View.VISIBLE);
                    txt_empty_cart.setVisibility(View.GONE);

                    adapter = new MyCartAdapter(getContext(),cartItems);
                    recycler_cart.setAdapter(adapter);
                }
            }
        });
        unbinder = ButterKnife.bind(this,root);
        initViews();
        return root;
    }

    private void initViews() {
        setHasOptionsMenu(true);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        EventBus.getDefault().postSticky(new HideFABCart(true));

        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_cart.setLayoutManager(layoutManager);
        recycler_cart.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));

        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(),recycler_cart,200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                 buf.add(new MyButton(getContext(),"Delete",30,0, Color.parseColor("#FF3C30"),
                         pos -> {
                            CartItem cartItem = adapter.getItemAtPosition(pos);
                            cartDataSource.deleteCartItem(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                             adapter.notifyItemRemoved(pos);
                                             sumAllItemInCart();// update total price
                                             EventBus.getDefault().postSticky(new CounterCartEvent(true));//updateFab
                                            Toast.makeText(getContext(), "Delete item from Cart Successfull!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            if (!e.getMessage().contains("Query returned empty")){
                                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();}
                                        }
                                    });

                 }));
            }
        };

        sumAllItemInCart();
    }

    private void sumAllItemInCart() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double aDouble) {
                          txt_total_price.setText(new StringBuilder("Total: $").append(aDouble));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!e.getMessage().contains("Query returned empty")){
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);//hide home menu
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu( Menu menu,  MenuInflater inflater) {
        inflater.inflate(R.menu.cart_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        if(item.getItemId() == R.id.action_cart_clear)
        {
            cartDataSource.cleanCart(Common.currentUser.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            Toast.makeText(getContext(), "Clear Cart Success", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        calculateTotalPrice();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);    }

    @Override
    public void onStop() {
        EventBus.getDefault().postSticky(new HideFABCart(false));
        cartViewModel.onStop();
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
        compositeDisposable.clear();
    }

   @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onUpdateItemInCartEvent(updateItemInCart event){
        if(event.getCartItem() != null){
            //first,save state of RecyclerView view
            recyclerViewState = recycler_cart.getLayoutManager().onSaveInstanceState();
            cartDataSource.updateCartItems(event.getCartItem())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            calculateTotalPrice();
                            recycler_cart.getLayoutManager().onRestoreInstanceState(recyclerViewState);//fix error refresh recycler view after update
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (!e.getMessage().contains("Query returned empty")){
                                 Toast.makeText(getContext(), "[UPDATE CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();}

                        }
                    });



        }
   }

    private void calculateTotalPrice() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double price) {
                       txt_total_price.setText(new StringBuilder("Total: $")
                         .append(Common.formatPrice(price)));
                    }

                    @Override
                    public void onError(Throwable e) {
                         {
                             if (!e.getMessage().contains("Query returned empty ")){
                            Toast.makeText(getContext(), "[SUM CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();}
                        }

                    }
                });
    }

    @Override
    public void onLoadTimeSuccess(Order order, long estimateTimeInMs) {
        order.setCreateDate(estimateTimeInMs);
        order.setOrderStatus(0);
        writeOrderToFirebase(order);

    }

    @Override
    public void onLoadTimeFailed(String message) {
        Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();
    }
}


