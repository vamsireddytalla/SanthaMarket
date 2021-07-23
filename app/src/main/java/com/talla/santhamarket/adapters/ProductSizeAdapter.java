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
import com.talla.santhamarket.interfaces.ChartsClickListner;
import com.talla.santhamarket.models.CategoryModel;

import java.util.List;
import java.util.Map;

public class ProductSizeAdapter extends RecyclerView.Adapter<ProductSizeAdapter.MyViewHolder> {
    private Context context;
    private List<String> sizeList;
    private int itemIndex=0;
    private ChartsClickListner listner;

    public ProductSizeAdapter(Context context, List<String> sizeList,ChartsClickListner listner) {
        this.context = context;
        this.sizeList = sizeList;
        this.listner=listner;
        notifyDataSetChanged();
    }

    public void setSizeList(List<String> sizeList) {
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
                listner.onSelectionCLick(sizeList.get(position),context.getString(R.string.Selected_Size));
                notifyDataSetChanged();
            }
        });

        if (itemIndex==position)
        {
            holder.binding.sizeItemRoot.setBackground(context.getResources().getDrawable(R.drawable.linear_border_color));
            listner.onSelectionCLick(sizeList.get(position),context.getString(R.string.Selected_Size));
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

        public void onBindView(String sizeObj) {
            binding.sizeItem.setText(sizeObj);
            Log.d("DetailProductActivity","  Adapter : "+sizeObj.toString());
        }

    }

}
