package com.example.user.concept;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class CheckoutCartListAdapter extends RecyclerView.Adapter<CheckoutCartListAdapter.CartItemHolder>{

    List<CartItem> cartItemList;
    Context mCtx;

    public CheckoutCartListAdapter(List<CartItem> cartItemList, Context mCtx) {
        this.cartItemList = cartItemList;
        this.mCtx = mCtx;
    }

    @Override
    public CartItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.from(parent.getContext()).inflate(R.layout.checkout_list_item_view, parent, false);

        return new CartItemHolder(view);
    }

    @Override
    public void onBindViewHolder(CartItemHolder holder, int position) {
        CartItem item = cartItemList.get(position);

        holder.productName.setText(item.getCartProductName());
        holder.productSupplier.setText("supplied by " + item.getCartProductSeller());
        holder.productPrice.setText("P " + String.valueOf(item.getCartProductPrice()));

        Picasso.get()
                .load(mCtx.getString(R.string.productImageBaseURL) + item.getCartImageLocation())
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class CartItemHolder extends RecyclerView.ViewHolder{
        TextView productName, productSupplier, productPrice;
        ImageView productImage;
        public CartItemHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.checkoutProductName);
            productSupplier = (TextView) itemView.findViewById(R.id.checkoutSupplier);
            productPrice = (TextView) itemView.findViewById(R.id.checkoutPrice);

            productImage = (ImageView) itemView.findViewById(R.id.productImageRecyclerView);
        }
    }
}
