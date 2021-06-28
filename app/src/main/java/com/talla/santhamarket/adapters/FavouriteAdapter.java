package com.talla.santhamarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.DetailProductActivity;
import com.talla.santhamarket.databinding.FavItemBinding;
import com.talla.santhamarket.databinding.NoItemsFoundBinding;
import com.talla.santhamarket.interfaces.ToggleItemListner;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {

    private Context context;
    private ToggleItemListner toggleItemListner;
    private List<ProductModel> favouriteModelList;
    private static final int LAYOUT_ITEMS = 1;
    private static final int NO_ITEMS_FOUND = 0;

    public FavouriteAdapter(Context context, List<ProductModel> favouriteModelList, ToggleItemListner toggleItemListner) {
        this.context = context;
        this.favouriteModelList = favouriteModelList;
        this.toggleItemListner = toggleItemListner;
        notifyDataSetChanged();
    }

    public void setFavouriteModelList(List<ProductModel> favouriteModelList) {
        this.favouriteModelList = favouriteModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                NoItemsFoundBinding noItemsViewHolder = NoItemsFoundBinding.inflate(layoutInflater, parent, false);
                return new ViewHolder(noItemsViewHolder);
            case 1:
                FavItemBinding itemBinding = FavItemBinding.inflate(layoutInflater, parent, false);
                return new ViewHolder(itemBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final FavouriteAdapter.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case 0:
                holder.setNoItemFound("No Favourite Items Found !");
                break;
            case 1:
                holder.onBindView(favouriteModelList.get(position));
                break;
        }

        holder.binding.buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(context, DetailProductActivity.class);
                in.putExtra(context.getString(R.string.pro_id),favouriteModelList.get(position).getProduct_id());
                context.startActivity(in);
            }
        });

    }

    @Override
    public int getItemCount() {
        return favouriteModelList.isEmpty()?0:favouriteModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (favouriteModelList.isEmpty()) {
            return NO_ITEMS_FOUND;
        } else {
            return LAYOUT_ITEMS;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private FavItemBinding binding;
        private NoItemsFoundBinding noItemsFoundBinding;

        public ViewHolder(NoItemsFoundBinding noItemsFoundBinding) {
            super(noItemsFoundBinding.getRoot());
            this.noItemsFoundBinding = noItemsFoundBinding;
        }

        public ViewHolder(FavItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
            binding.toggleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleItemListner.toggleChangeListner(getAdapterPosition(), 0);
                }
            });
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
            binding.discount.setText(String.valueOf(res).substring(0, 2) + "%OFF");
            ;
            binding.toggleBtn.setChecked(true);
        }

        public void setNoItemFound(String va) {
            noItemsFoundBinding.noItemsFound.setText(va);
        }

    }


}
