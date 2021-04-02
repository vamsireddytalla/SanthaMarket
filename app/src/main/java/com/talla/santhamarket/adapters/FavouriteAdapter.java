package com.talla.santhamarket.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.talla.santhamarket.databinding.DashHomeBannerBinding;
import com.talla.santhamarket.databinding.FavItemBinding;
import com.talla.santhamarket.interfaces.ToggleItemListner;
import com.talla.santhamarket.models.FavouriteModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> {

    private Context context;
    private ToggleItemListner toggleItemListner;
    private List<ProductModel> favouriteModelList;

    public FavouriteAdapter(Context context, List<ProductModel> favouriteModelList,ToggleItemListner toggleItemListner) {
        this.context = context;
        this.favouriteModelList = favouriteModelList;
        this.toggleItemListner=toggleItemListner;
        notifyDataSetChanged();
    }

    public void setFavouriteModelList(List<ProductModel> favouriteModelList) {
        this.favouriteModelList = favouriteModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavouriteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FavItemBinding itemBinding = FavItemBinding.inflate(layoutInflater, parent, false);
        return new FavouriteAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavouriteAdapter.MyViewHolder holder, final int position) {
        holder.onBindView(favouriteModelList.get(position));

        holder.binding.toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    toggleItemListner.toggleChangeListner(position,0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favouriteModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private FavItemBinding binding;

        public MyViewHolder(FavItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void onBindView(ProductModel favouriteModel) {
            Glide.with(context).load(favouriteModel.getProduct_images().get(0).getProduct_image()).fitCenter().into(binding.imageIcon);
            binding.productName.setText(favouriteModel.getProduct_name());
            binding.productDesc.setText(favouriteModel.getSeller_name());
            binding.productPrice.setText(CheckUtill.FormatCost(Math.round(favouriteModel.getProduct_price())) + " Rs");
            binding.mrpPrice.setText(CheckUtill.FormatCost(Math.round(favouriteModel.getMrp_price())) + " Rs");
            binding.mrpPrice.setPaintFlags(binding.mrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            Long mrp_price = favouriteModel.getMrp_price();
            Long selling_price = favouriteModel.getProduct_price();
            float res = StaticUtills.discountPercentage(selling_price, mrp_price);
            binding.discount.setText(String.valueOf(res).substring(0, 2) + "%OFF");;
            binding.toggleBtn.setChecked(true);
        }

    }
}
