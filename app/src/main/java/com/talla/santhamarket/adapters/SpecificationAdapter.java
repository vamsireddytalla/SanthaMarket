package com.talla.santhamarket.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.SizeChartBinding;
import com.talla.santhamarket.databinding.SpecifyItemBinding;

import java.util.List;
import java.util.Map;

public class SpecificationAdapter extends RecyclerView.Adapter<SpecificationAdapter.MyViewHolder> {
    private Context context;
    private List<Map.Entry<String, Object>> specList;

    public SpecificationAdapter(Context context, List<Map.Entry<String, Object>> specList) {
        this.context = context;
        this.specList = specList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SpecificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SpecifyItemBinding itemBinding = SpecifyItemBinding.inflate(layoutInflater, parent, false);
        return new SpecificationAdapter.MyViewHolder(itemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull SpecificationAdapter.MyViewHolder holder, final int position) {
        holder.onBindView(specList.get(position));

    }

    @Override
    public int getItemCount() {
        return specList == null ? 0 : specList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private SpecifyItemBinding binding;


        public MyViewHolder(SpecifyItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void onBindView(Map.Entry<String, Object> sizeObj) {
            binding.keyName.setText(sizeObj.getKey().toString());
            binding.valueDesc.setText(sizeObj.getValue().toString());
        }

    }

}

