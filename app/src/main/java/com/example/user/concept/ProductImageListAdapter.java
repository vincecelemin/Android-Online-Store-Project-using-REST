package com.example.user.concept;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductImageListAdapter extends RecyclerView.Adapter<ProductImageListAdapter.ProduceImageViewHolder> {
    private Context mContext;
    private List<String> imageAddressList;

    public ProductImageListAdapter(Context mContext, List<String> imageAddressList) {
        this.mContext = mContext;
        this.imageAddressList = imageAddressList;
    }

    @Override
    public ProduceImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.from(parent.getContext()).inflate(R.layout.product_image_carousel_item, parent, false);
        return new ProduceImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProduceImageViewHolder holder, int position) {
        String imageAddress = imageAddressList.get(position);

        Picasso
            .get()
            .load("http://10.0.2.2/WebFramFinalProj/public/storage/product_images/" + imageAddress)
            .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return imageAddressList.size();
    }

    public class ProduceImageViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;

        public ProduceImageViewHolder(View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productCarouselImageItem);
        }
    }
}
