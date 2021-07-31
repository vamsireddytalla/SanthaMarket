package com.talla.santhamarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.DetailProductActivity;
import com.talla.santhamarket.databinding.SearchedItemBinding;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.List;

public class SearchedProductsAdapter extends RecyclerView.Adapter<SearchedProductsAdapter.MyViewHolder> {
    private List<ProductModel> productModelList;
    private Context context;

    public SearchedProductsAdapter(List<ProductModel> productModelList, Context context) {
        this.productModelList = productModelList;
        this.context = context;
        notifyDataSetChanged();
    }


    public void setProductModelList(List<ProductModel> productModelList) {
        this.productModelList = productModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchedProductsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SearchedItemBinding itemBinding = SearchedItemBinding.inflate(layoutInflater, parent, false);
        return new SearchedProductsAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchedProductsAdapter.MyViewHolder holder, final int position) {
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
            if (productModel.getTotalStock()<=0)
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
