package com.example.user.concept;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context mCtx;

    public OrderListAdapter(List<Order> orderList, Context mCtx) {
        this.orderList = orderList;
        this.mCtx = mCtx;
    }

    @Override
    public OrderListAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.from(parent.getContext()).inflate(R.layout.order_list_item_view, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderListAdapter.OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.productName.setText(order.getProductName());
        holder.productSeller.setText("fulfilled by " + order.getSellerName());
        holder.productPrice.setText(String.format("%d piece/s | P %.2f", order.getQuantity(), order.getPrice()));

        switch(order.getStatus()) {
            case 0:
                holder.productStatus.setText("status: on transit");
                break;
            case 1:
                holder.productStatus.setText("status: delivered");
                break;
            case 2:
                holder.productStatus.setText("status: cancelled");
                break;
        }

        Picasso.get()
                .load(mCtx.getString(R.string.productImageBaseURL) + order.getImageLoc())
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productSeller, productPrice, productStatus;
        ImageView productImage;

        public OrderViewHolder(View itemView) {
            super(itemView);

            productName = (TextView) itemView.findViewById(R.id.orderProductName);
            productSeller = (TextView) itemView.findViewById(R.id.orderProductSeller);
            productPrice = (TextView) itemView.findViewById(R.id.orderProductPrice);
            productStatus = (TextView) itemView.findViewById(R.id.orderProductStatus);

            productImage = (ImageView) itemView.findViewById(R.id.orderImage);
        }
    }
}
