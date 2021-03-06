package com.nhom4.orderfoodserver;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.nhom4.orderfoodserver.R;
import com.nhom4.orderfoodserver.Common.Common;
import com.nhom4.orderfoodserver.Common.OrderAdapter;
import com.nhom4.orderfoodserver.Interface.ItemClickListener;
import com.nhom4.orderfoodserver.Model.Request;
import com.nhom4.orderfoodserver.ViewHolder.OrderViewHolder;

public class OrderStatus extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase db;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    DatabaseReference requests;
    MaterialSpinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");


        recyclerView =  findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrder();
    }
    private void loadOrder() {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {

                viewHolder.txtOrderId.setText("Id ????n h??ng : "+adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText("Tr???ng th??i : "+ Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderPhone.setText("S??T : "+model.getPhone());
                viewHolder.txtGmail.setText("?????a ch??? : "+model.getAddress());
                viewHolder.txtTotal.setText("Gi?? ti???n : "+model.getTotal());

                OrderAdapter orderAdapter = new OrderAdapter(model.getFoods() , getApplicationContext());
                viewHolder.lvOrders.setAdapter(orderAdapter);

                Log.d("OrderStatus", "populateViewHolder:  " + model.getFoods().size());


                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void OnClick(View view, int position, boolean isLongClick) {

                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE))
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        else if (item.getTitle().equals(Common.DELETE))
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key) {
        requests.child(key).removeValue();

    }

    private void showUpdateDialog(String key, final Request item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("S???a ????n ?????t");
        alertDialog.setMessage("H??y ch???n tr???ng th??i");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout,null);

        spinner = (MaterialSpinner)view.findViewById(R.id.statusSpiner);
        spinner.setItems("???? ?????t h??ng","??ang g???i h??ng","???? g???i h??ng");
        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("?????ng ??", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                requests.child(localKey).setValue(item);
            }
        });
        alertDialog.setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();

    }

}
