package com.talla.santhamarket.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.CategoriesActivity;
import com.talla.santhamarket.activities.DetailProductActivity;
import com.talla.santhamarket.databinding.ShopItemBinding;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.RegisterModel;
import com.talla.santhamarket.models.ShopAddress;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.MyViewHolder> {
    private List<RegisterModel> registerModelList;
    private Context context;

    public ShopsAdapter(List<RegisterModel> registerModelList, Context context) {
        this.registerModelList = registerModelList;
        this.context = context;
        notifyDataSetChanged();
    }

    public void setRegisterModelList(List<RegisterModel> registerModelList) {
        this.registerModelList = registerModelList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ShopsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ShopItemBinding itemBinding = ShopItemBinding.inflate(layoutInflater, parent, false);
        return new ShopsAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopsAdapter.MyViewHolder holder, final int position) {
        holder.onBindView(registerModelList.get(position));

        holder.binding.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterModel registerModel = registerModelList.get(position);
                if (registerModel.isShopOpenedOrNot()) {
                    Intent intent = new Intent(context, CategoriesActivity.class);
                    intent.putExtra(context.getString(R.string.intent_reg_model_key), registerModel);
                    context.startActivity(intent);
                } else {
                    showDialog("Shop Closed","Please try After Some Time");
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return registerModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ShopItemBinding binding;

        public MyViewHolder(ShopItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBindView(RegisterModel registerModel) {
            binding.shopName.setText(registerModel.getName());
            ShopAddress shopAddress = registerModel.getShopAddress();
            if (shopAddress != null) {
                binding.shopAddress.setText(shopAddress.getState() + "\n" + shopAddress.getCity() + "\n" + shopAddress.getPincode());
            }

            Glide.with(context).load(registerModel.getImage_url()).placeholder(R.drawable.shops).fitCenter().into(binding.shopImage);

            if (registerModel.isShopOpenedOrNot()) {
                binding.shopStatus.setTextColor(context.getResources().getColor(R.color.greeen));
                binding.shopStatus.setText("Opened");
            } else {
                binding.shopStatus.setTextColor(context.getResources().getColor(R.color.orange));
                binding.shopStatus.setText("Closed");
            }

        }
    }

    private void showDialog(final String title, String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.show();
    }


}
