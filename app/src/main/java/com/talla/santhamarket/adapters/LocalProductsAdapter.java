package com.talla.santhamarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import com.talla.santhamarket.databinding.SearchedItemBinding;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LocalProductsAdapter extends RecyclerView.Adapter<LocalProductsAdapter.MyViewHolder> implements Filterable {
    private List<ProductModel> productModelList;
    private Context context;
    private List<ProductModel> productModelListTemp;

    public LocalProductsAdapter(List<ProductModel> productModelList, Context context) {
        this.productModelList = productModelList;
        this.context = context;
        this.productModelListTemp = new ArrayList<>(productModelList);
        notifyDataSetChanged();
    }


    public void setProductModelList(List<ProductModel> productModelList) {
        this.productModelList = productModelList;
        notifyDataSetChanged();
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


    @NonNull
    @Override
    public LocalProductsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SearchedItemBinding itemBinding = SearchedItemBinding.inflate(layoutInflater, parent, false);
        return new LocalProductsAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalProductsAdapter.MyViewHolder holder, final int position) {
        holder.onBindView(productModelList.get(position));

        holder.binding.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailProductActivity.class);
                intent.putExtra(context.getString(R.string.intent_product_id), productModelList.get(position).getProduct_id());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        SearchedItemBinding binding;

        public MyViewHolder(final SearchedItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBindView(ProductModel productModel) {
            binding.titleView.setText(productModel.getProduct_name());
            binding.sellerName.setText(productModel.getSeller_name());
            double selling_price = productModel.getProduct_price();
            double mrp_price = productModel.getMrp_price();
            int res = StaticUtills.discountPercentage(selling_price, mrp_price);
            binding.sellingPice.setText(context.getResources().getString(R.string.rs_symbol) + CheckUtill.FormatCost((int) Math.round(selling_price)));
            binding.mrpprice.setText(CheckUtill.FormatCost((int) Math.round(mrp_price)) + context.getResources().getString(R.string.Rs));
            binding.mrpprice.setPaintFlags(binding.mrpprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.discount.setText(String.valueOf(res) + "%OFF");

            String imgUrl = productModel.getSubProductModelList().get(0).getProduct_images().get(0).getProduct_image();
            String bgColor = productModel.getSubProductModelList().get(0).getProduct_images().get(0).getProduct_bg_color();
            binding.viewProdImage.setBackgroundColor(Integer.parseInt(bgColor));
            Glide.with(context).load(imgUrl).fitCenter().into(binding.viewProdImage);
            binding.ratingbar.setRating((float) productModel.getAvgRatings());
            binding.totalRaters.setText("(" + productModel.getTotal_ratings() + ")");
            if (productModel.getTotalStock().equals(productModel.getSelled_items()) || productModel.isOut_of_stock())
            {
                binding.soldOut.setText("Sold Out");
                binding.soldOut.setTextColor(context.getResources().getColor(R.color.orange));
            }else {
                binding.soldOut.setText(productModel.getTotalStock()+" Items Available");
                binding.soldOut.setTextColor(context.getResources().getColor(R.color.greeen));
            }
        }
    }


}
