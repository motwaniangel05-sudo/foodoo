package com.example.foodordering.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodordering.R;
import com.example.foodordering.database.Cart;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Cart> cartList;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onQuantityChange(Cart cart, int newQty);
        void onDelete(Cart cart);
    }

    public CartAdapter(List<Cart> cartList, OnCartChangeListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        holder.tvCartName.setText(cart.name);
        holder.tvCartPrice.setText("₹" + (cart.price * cart.quantity));
        holder.tvCartQty.setText(String.valueOf(cart.quantity));

        holder.btnCartPlus.setOnClickListener(v -> listener.onQuantityChange(cart, cart.quantity + 1));
        holder.btnCartMinus.setOnClickListener(v -> {
            if (cart.quantity > 1) {
                listener.onQuantityChange(cart, cart.quantity - 1);
            }
        });
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(cart));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void updateList(List<Cart> newList) {
        this.cartList = newList;
        notifyDataSetChanged();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvCartName, tvCartPrice, tvCartQty;
        ImageButton btnCartPlus, btnCartMinus, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCartName = itemView.findViewById(R.id.tvCartName);
            tvCartPrice = itemView.findViewById(R.id.tvCartPrice);
            tvCartQty = itemView.findViewById(R.id.tvCartQty);
            btnCartPlus = itemView.findViewById(R.id.btnCartPlus);
            btnCartMinus = itemView.findViewById(R.id.btnCartMinus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
