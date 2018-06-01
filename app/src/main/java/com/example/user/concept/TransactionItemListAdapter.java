package com.example.user.concept;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TransactionItemListAdapter extends RecyclerView.Adapter<TransactionItemListAdapter.TransactionHolder> {
    List<Transaction> transactionList;
    Context mCtx;

    public TransactionItemListAdapter(List<Transaction> transactionList, Context mCtx) {
        this.transactionList = transactionList;
        this.mCtx = mCtx;
    }

    @Override
    public TransactionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.from(parent.getContext()).inflate(R.layout.topup_transaction_item, parent, false);

        return new TransactionHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.transactionDate.setText(transaction.getDate());

        if(transaction.getType() == 1) {
            holder.transactionTitle.setText("Topped up account");
            holder.transactionAmount.setText("P " + String.format("%.2f", transaction.getAmount()));
            holder.transactionType.setImageDrawable(mCtx.getDrawable(R.drawable.sharp_add_black_24));
        } else {
            holder.transactionTitle.setText("Spent on orders");
            holder.transactionAmount.setText("(P " + String.format("%.2f)", transaction.getAmount()));
            holder.transactionType.setImageDrawable(mCtx.getDrawable(R.drawable.sharp_remove_black_24));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class TransactionHolder extends RecyclerView.ViewHolder {
        TextView transactionDate, transactionAmount, transactionTitle;
        ImageView transactionType;

        public TransactionHolder(View itemView) {
            super(itemView);

            transactionDate = (TextView) itemView.findViewById(R.id.transactionDate);
            transactionTitle = (TextView) itemView.findViewById(R.id.transactionTitle);
            transactionAmount = (TextView) itemView.findViewById(R.id.transactionAmount);

            transactionType = (ImageView) itemView.findViewById(R.id.transactionType);
        }
    }
}
