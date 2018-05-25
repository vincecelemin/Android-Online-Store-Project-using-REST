package com.example.user.concept;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductCardListAdapter extends RecyclerView.Adapter<ProductCardListAdapter.ProductCardListHolder> {

    private Context mCtx;
    private List<ProductCardItem> productCartItemList;

    public ProductCardListAdapter(Context mCtx, List<ProductCardItem> productCartItemList) {
        this.mCtx = mCtx;
        this.productCartItemList = productCartItemList;
    }

    @Override
    public ProductCardListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.from(parent.getContext()).inflate(R.layout.product_card_view, parent, false);
        return new ProductCardListHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductCardListHolder holder, int position) {
        final ProductCardItem productCardItem = productCartItemList.get(position);

        holder.productName.setText(productCardItem.getName());
        holder.productSeller.setText("by " + productCardItem.getSeller());
        holder.productPrice.setText("P " + String.valueOf(productCardItem.getPrice()));
        Picasso.get()
                .load("http://10.0.2.2/WebFramFinalProj/public/storage/product_images/" + productCardItem.getImageName())
                .into(holder.productLogo);

        holder.viewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mCtx, ViewProduct.class);
                intent.putExtra("product_id", productCardItem.getProductId());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productCartItemList.size();
    }

    public class ProductCardListHolder extends RecyclerView.ViewHolder {
        ImageView productLogo;
        TextView productName, productSeller, productPrice;
        Button addToCartBtn, viewProductBtn;

        public ProductCardListHolder(View itemView) {
            super(itemView);

            productLogo = (ImageView) itemView.findViewById(R.id.cardProductLogo);
            productName = (TextView) itemView.findViewById(R.id.cardProductName);
            productSeller = (TextView) itemView.findViewById(R.id.cardProductSeller);
            productPrice = (TextView) itemView.findViewById(R.id.cardProductPrice);

            // Buttons
            viewProductBtn = (Button) itemView.findViewById(R.id.cardViewProductBtn);
        }
    }
}
