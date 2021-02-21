package com.talla.santhamarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.ProductImageItemBinding;
import com.talla.santhamarket.models.ProductImageModel;

import java.util.List;

public class ProductImagesAdapter extends RecyclerView.Adapter<ProductImagesAdapter.MyViewHolder>
{
    private Context context;
    private List<ProductImageModel> productImageModelList;

    public ProductImagesAdapter(Context context, List<ProductImageModel> productImageModelList) {
        this.context = context;
        this.productImageModelList = productImageModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductImagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ProductImageItemBinding itemBinding = ProductImageItemBinding.inflate(layoutInflater, parent,false);
        return new ProductImagesAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductImagesAdapter.MyViewHolder holder, int position) {
        holder.onBindView(productImageModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return productImageModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ProductImageItemBinding binding;


        public MyViewHolder(ProductImageItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void onBindView(ProductImageModel s) {
            String img =s.getProduct_image();
            Glide.with(context).load(img).placeholder(context.getResources().getDrawable(R.drawable.place_holder)).fitCenter().into(binding.productImage);
        }

    }
}
