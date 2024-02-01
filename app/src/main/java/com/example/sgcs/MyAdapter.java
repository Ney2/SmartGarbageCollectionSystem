package com.example.sgcs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends ArrayAdapter<Garbage> {
    private Context context;
    private List<Garbage> arrayListGarbage;

    public MyAdapter(@NonNull Context context, List<Garbage> arrayListGarbage) {
        super(context, R.layout.garbage_list_item,arrayListGarbage);

        this.context = context;
        this.arrayListGarbage = arrayListGarbage;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.garbage_list_item,null,true);
        TextView tvID = view.findViewById(R.id.txt_id);
        TextView tvName = view.findViewById(R.id.txt_name);

        tvID.setText(arrayListGarbage.get(position).getId());
        tvName.setText(arrayListGarbage.get(position).getLevel());

        return view;
    }
}
