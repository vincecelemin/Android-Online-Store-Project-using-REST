package com.example.user.concept;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class OrderDetailsDialog extends AppCompatDialogFragment {
    private Order order;

    private TextView itemName, itemSeller, contactPerson, contactNumber, deliveryAddress, arrivalDate, orderDate;

    public void setOrder (Order order) {
        this.order = order;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.view_order_details_dialog, null);

        builder.setView(view)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        itemName = (TextView) view.findViewById(R.id.dialogProdName);
        itemSeller = (TextView) view.findViewById(R.id.dialogProdSeller);
        contactPerson = (TextView) view.findViewById(R.id.dialogContactPerson);
        contactNumber = (TextView) view.findViewById(R.id.dialogContactNumber);
        deliveryAddress = (TextView) view.findViewById(R.id.dialogDeliveryAddress);
        orderDate = (TextView) view.findViewById(R.id.dialogOrederedOn);
        arrivalDate = (TextView) view.findViewById(R.id.dialogETA);

        setInformation();

        return builder.create();
    }

    private void setInformation() {
        itemName.setText("Item name: " + order.getProductName());
        itemSeller.setText("Fulfilled by: " + order.getSellerName());
        contactPerson.setText(order.getContactPerson());
        contactNumber.setText(order.getContactNumber());
        deliveryAddress.setText(order.getDeliveryAddress());
        orderDate.setText("Ordered on: " + order.getAddedDate());

        if(order.getStatus() == 0) {
            arrivalDate.setText("Estimated Arrival: " + order.getArrivalDate().substring(0, 10));
        } else if (order.getStatus() == 1) {
            arrivalDate.setText("Delivered: " + order.getArrivalDate());
        } else if (order.getStatus() == 2) {
            arrivalDate.setVisibility(View.GONE);
        }
    }
}
