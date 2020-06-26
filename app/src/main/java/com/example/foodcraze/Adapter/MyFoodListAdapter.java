package com.example.foodcraze.Adapter;

import android.content.Context;
import android.icu.text.Transliterator;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodcraze.Callback.RecyclerClickListener;
import com.example.foodcraze.Common.Common;
import com.example.foodcraze.Database.CartDataSource;
import com.example.foodcraze.Database.CartDatabase;
import com.example.foodcraze.Database.CartItem;
import com.example.foodcraze.Database.LocalCartDataSource;
import com.example.foodcraze.EventBus.CounterCartEvent;
import com.example.foodcraze.EventBus.FoodItemClick;
import com.example.foodcraze.Model.FoodModel;
import com.example.foodcraze.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyFoodListAdapter extends RecyclerView.Adapter<MyFoodListAdapter.MyViewHolder>{

    private Context context;
    private List<FoodModel> foodModelList;
    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;

    public MyFoodListAdapter(Context context, List<FoodModel> foodModelList) {
        this.context = context;
        this.foodModelList = foodModelList;
        this.compositeDisposable = new CompositeDisposable();
        this.cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
           .inflate(R.layout.layout_food_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(foodModelList.get(position).getImage()).into(holder.img_food_image);
        holder.txt_food_price.setText(new StringBuilder("$")
          .append(foodModelList.get(position).getPrice()));
        holder.txt_food_name.setText(new StringBuilder("")
        .append(foodModelList.get(position).getName()));
        //event
        holder.setListener((view, pos) -> {
            Common.selectedFood = foodModelList.get(pos);
            Common.selectedFood.setKey(String.valueOf(pos));
            EventBus.getDefault().postSticky(new FoodItemClick(true,foodModelList.get(pos)));
        });
        holder.img_quick_cart.setOnClickListener(view -> {
            CartItem cartItem = new CartItem();
            cartItem.setUid(Common.currentUser.getUid());
            cartItem.setUserPhone(Common.currentUser.getPhone());

            cartItem.setFoodId(foodModelList.get(position).getId());
            cartItem.setFoodName(foodModelList.get(position).getName());
            cartItem.setFoodImage(foodModelList.get(position).getImage());
            cartItem.setFoodPrice(Double.valueOf(String.valueOf(foodModelList.get(position).getPrice())));
            cartItem.setFoodQuantity(1);
            cartItem.setFoodExtraPrice(0.0);
            cartItem.setFoodAddon("Default");
            cartItem.setFoodSize("Default");

          cartDataSource.getItemWithAllOptionInCart(Common.currentUser.getUid(),
                  cartItem.getFoodId(),
                  cartItem.getFoodSize(),
                  cartItem.getFoodAddon())
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new SingleObserver<CartItem>() {
                      @Override
                      public void onSubscribe(Disposable d) {

                      }

                      @Override
                      public void onSuccess(CartItem cartItemFromDB) {
                           if(cartItemFromDB.equals(cartItem)){
                               //Already in database,just update
                               cartItemFromDB.setFoodExtraPrice(cartItem.getFoodExtraPrice());
                               cartItemFromDB.setFoodAddon(cartItem.getFoodAddon());
                               cartItemFromDB.setFoodSize(cartItem.getFoodSize());
                               cartItemFromDB.setFoodQuantity(cartItemFromDB.getFoodQuantity() + cartItem.getFoodQuantity());

                               cartDataSource.updateCartItems(cartItemFromDB)
                                       .subscribeOn(Schedulers.io())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .subscribe(new SingleObserver<Integer>() {
                                           @Override
                                           public void onSubscribe(Disposable d) {

                                           }

                                           @Override
                                           public void onSuccess(Integer integer) {
                                               Toast.makeText(context, "Update Cart Success", Toast.LENGTH_SHORT).show();
                                               EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                           }

                                           @Override
                                           public void onError(Throwable e) {
                                               Toast.makeText(context, "[UPDATE CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                           }
                                       });
                           }
                           else {
                               //item not available in cart before, insert new
                               compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                       .subscribeOn(Schedulers.io())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .subscribe(() -> {
                                           Toast.makeText(context, "Add to Cart Success ", Toast.LENGTH_SHORT).show();
                                           EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                               },throwable -> {
                                           Toast.makeText(context, "[CART ERROR]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                               }
                                       ));
                           }
                      }

                      @Override
                      public void onError(Throwable e) {
                          if(e.getMessage().contains("empty")){
                            //default ,if cart empty, this code will will be fired
                              compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                      .subscribeOn(Schedulers.io())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribe(() -> {
                                                  Toast.makeText(context, "Add to Cart Success ", Toast.LENGTH_SHORT).show();
                                                  EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                              },throwable -> {
                                                  Toast.makeText(context, "[CART ERROR]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                              }
                                      ));
                          }
                          else
                              Toast.makeText(context, "[GET CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                      }
                  });



        });
    }

    @Override
    public int getItemCount() {
        return foodModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;
        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;
        @BindView(R.id.img_food_image)
        ImageView img_food_image;
        @BindView(R.id.img_fav)
        ImageView img_fav;
        @BindView(R.id.img_quick_cart)
        ImageView img_quick_cart;

        RecyclerClickListener listener;

        public void setListener(RecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
           listener.onItemClickListener(view,getAdapterPosition());
        }
    }
}
