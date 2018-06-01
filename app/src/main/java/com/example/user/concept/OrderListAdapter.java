package com.example.user.concept;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context mCtx;
    private FragmentManager fragmentManager;

    public OrderListAdapter(List<Order> orderList, Context mCtx, FragmentManager fragmentManager) {
        this.orderList = orderList;
        this.mCtx = mCtx;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public OrderListAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.from(parent.getContext()).inflate(R.layout.order_list_item_view, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderListAdapter.OrderViewHolder holder, final int position) {
        final Order order = orderList.get(position);

        holder.self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mCtx, "Clicked " + String.valueOf(position), Toast.LENGTH_SHORT).show();
            }
        });

        holder.productName.setText(order.getProductName());
        holder.productSeller.setText("fulfilled by " + order.getSellerName());
        holder.productPrice.setText(String.format("%d piece/s | P %.2f", order.getQuantity(), order.getPrice()));

        switch(order.getStatus()) {
            case 0:
                holder.orderStatus.setText("status: on transit");
                break;

            case 1:
                holder.orderStatus.setText("status: delivered");
                break;

            case 2:
                holder.orderStatus.setText("status: cancelled");
                break;
        }

        Picasso.get()
                .load(mCtx.getString(R.string.productImageBaseURL) + order.getImageLoc())
                .into(holder.productImage);

        holder.orderDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrderDetailsDialog(order);
            }
        });
    }

    private void openOrderDetailsDialog(Order order) {
        OrderDetailsDialog orderDetailsDialog = new OrderDetailsDialog();
        orderDetailsDialog.setOrder(order);
        orderDetailsDialog.show(fragmentManager, "view details");
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productSeller, productPrice, orderStatus;
        Button orderDetailsBtn;
        View self;
        ImageView productImage;

        public OrderViewHolder(View itemView) {
            super(itemView);

            self = itemView;
            productName = (TextView) itemView.findViewById(R.id.orderProductName);
            productSeller = (TextView) itemView.findViewById(R.id.orderProductSeller);
            productPrice = (TextView) itemView.findViewById(R.id.orderProductPrice);
            orderStatus = (TextView) itemView.findViewById(R.id.orderStatus);

            orderDetailsBtn = (Button) itemView.findViewById(R.id.orderDetailsBtn);

            productImage = (ImageView) itemView.findViewById(R.id.orderImage);
        }
    }
}
