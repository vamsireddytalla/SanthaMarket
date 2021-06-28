package com.talla.santhamarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.DetailProductActivity;
import com.talla.santhamarket.activities.ViewProductsActivity;
import com.talla.santhamarket.databinding.DashCatLayoutBinding;
import com.talla.santhamarket.databinding.ProductItemBinding;
import com.talla.santhamarket.models.CategoryModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<ProductModel> productModelList;
    private List<ProductModel> productModelListTemp;

    public ProductAdapter(Context context, List<ProductModel> productModelList) {
        this.context = context;
        this.productModelList = productModelList;
        this.productModelListTemp = new ArrayList<>(productModelList);
        notifyDataSetChanged();
    }

    public void setProductModel(ProductModel productModel) {
        productModelList.add(productModel);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ProductItemBinding itemBinding = ProductItemBinding.inflate(layoutInflater, parent, false);
        return new ProductAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.onBindView(productModelList.get(position));

        holder.binding.productRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailProductActivity.class);
                intent.putExtra("product_id", productModelList.get(position).getProduct_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ProductModel> filteredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(productModelListTemp);
            } else {
                for (ProductModel productModel : productModelListTemp) {
                    if (productModel.getProduct_name().toLowerCase().contains(charSequence)) {
                        filteredList.add(productModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            productModelList.clear();
            productModelList.addAll((Collection<? extends ProductModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        return productModelList == null ? 0 : productModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ProductItemBinding binding;

        public MyViewHolder(ProductItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;


        }

        public void onBindView(ProductModel productModel) {
            Glide.with(context).load(productModel.getProduct_images().get(0).getProduct_image()).fitCenter().into(binding.productImage);
            binding.productTitle.setText(productModel.getProduct_name());
            binding.sellerName.setText(productModel.getSeller_name());
            Long mrp_price = productModel.getMrp_price();
            Long selling_price = productModel.getProduct_price();
            float res = StaticUtills.discountPercentage(selling_price, mrp_price);
            binding.sellingPrice.setText(context.getResources().getString(R.string.rs_symbol) + CheckUtill.FormatCost(Math.round(selling_price)));
            binding.mrpPrice.setText(CheckUtill.FormatCost(Math.round(mrp_price)) + context.getResources().getString(R.string.Rs));
            binding.mrpPrice.setPaintFlags(binding.mrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.discount.setText(String.valueOf(res).substring(0, 2) + " %OFF");
        }

    }
}
