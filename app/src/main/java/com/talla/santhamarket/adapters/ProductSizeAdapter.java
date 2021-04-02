package com.talla.santhamarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.ViewProductsActivity;
import com.talla.santhamarket.databinding.DashCatLayoutBinding;
import com.talla.santhamarket.databinding.SizeChartBinding;
import com.talla.santhamarket.models.CategoryModel;

import java.util.List;
import java.util.Map;

public class ProductSizeAdapter extends RecyclerView.Adapter<ProductSizeAdapter.MyViewHolder> {
    private Context context;
    private List<Map.Entry<String, Object>> sizeList;
    private int itemIndex=-1;

    public ProductSizeAdapter(Context context, List<Map.Entry<String, Object>> sizeList) {
        this.context = context;
        this.sizeList = sizeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductSizeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SizeChartBinding itemBinding = SizeChartBinding.inflate(layoutInflater, parent, false);
        return new ProductSizeAdapter.MyViewHolder(itemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull ProductSizeAdapter.MyViewHolder holder, final int position) {
        holder.onBindView(sizeList.get(position));

        holder.binding.sizeItemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemIndex=position;
                notifyDataSetChanged();
            }
        });

        if (itemIndex==position)
        {
            holder.binding.sizeItemRoot.setBackground(context.getResources().getDrawable(R.drawable.linear_border_color));
        }else {
            holder.binding.sizeItemRoot.setBackground(context.getResources().getDrawable(R.drawable.linear_border));
        }
    }

    @Override
    public int getItemCount() {
        return sizeList == null ? 0 : sizeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private SizeChartBinding binding;


        public MyViewHolder(SizeChartBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void onBindView(Map.Entry<String, Object> sizeObj) {
            binding.sizeItem.setText(sizeObj.getValue().toString());
            Log.d("DetailProductActivity","  Adapter : "+sizeObj.toString());
        }

    }

}
