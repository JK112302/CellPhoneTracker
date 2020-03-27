package com.trex.projectprototype.main.registeruser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trex.projectprototype.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList mDataset = new ArrayList();


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public EditText phoneNumber;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneNumber=itemView.findViewById(R.id.input_edittext);
        }
    }

    public MyAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }


    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_input_number, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.phoneNumber.setText(mDataset.get(position).toString());

    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}