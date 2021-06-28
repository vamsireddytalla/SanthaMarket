package com.talla.santhamarket.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.AddressItemBinding;
import com.talla.santhamarket.databinding.OrderSummaryItemBinding;
import com.talla.santhamarket.interfaces.AddressItemListner;
import com.talla.santhamarket.interfaces.ChartsClickListner;
import com.talla.santhamarket.interfaces.QuantityClickListner;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.UserAddress;
import com.talla.santhamarket.utills.CheckUtill;
import com.talla.santhamarket.utills.StaticUtills;

import java.util.ArrayList;
import java.util.List;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.MyViewHolder> {
    private Context context;
    private List<ProductModel> productModelList;
    private QuantityClickListner listner;
    private ChartsClickListner chartsClickListner;

    public SummaryAdapter(Context context, List<ProductModel> productModelList, QuantityClickListner listner,ChartsClickListner chartsClickListner) {
        this.context = context;
        this.productModelList = productModelList;
        this.chartsClickListner=chartsClickListner;
        this.listner = listner;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public SummaryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        OrderSummaryItemBinding itemBinding = OrderSummaryItemBinding.inflate(layoutInflater, parent, false);
        return new SummaryAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SummaryAdapter.MyViewHolder holder, final int position) {
        holder.onBindView(productModelList.get(position));

        holder.binding.quantitySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
//                    Long qty= Long.valueOf(i)+1;
                    String selectedQty = (String) adapterView.getSelectedItem();
                    productModelList.get(position).setTemp_qty(Long.parseLong(selectedQty));
                    listner.quantityClickListener(position, productModelList.get(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        holder.binding.sumRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(productModelList.get(position));
            }
        });
        holder.binding.sumFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String favTxt=holder.binding.sumFav.getText().toString();
                if (favTxt.equalsIgnoreCase(context.getString(R.string.add_to_fav)))
                {
                    chartsClickListner.onSelectionCLick(productModelList.get(position).getProduct_id(),context.getString(R.string.add_to_fav));
                }else {
                    chartsClickListner.onSelectionCLick(productModelList.get(position).getTemp_favouriteId(),context.getString(R.string.remove_ad_fav));
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return productModelList == null ? 0 : productModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private OrderSummaryItemBinding binding;

        public MyViewHolder(OrderSummaryItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void onBindView(ProductModel productModel) {
            binding.sumTitle.setText(productModel.getProduct_name());
            binding.sumSellerName.setText(productModel.getSeller_name());
            binding.productPrice.setText(CheckUtill.FormatCost(Math.round(productModel.getProduct_price())) + context.getString(R.string.Rs));
            binding.mrpPrice.setText(CheckUtill.FormatCost(Math.round(productModel.getMrp_price())) + context.getString(R.string.Rs));
            binding.mrpPrice.setPaintFlags(binding.mrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            Long mrp_price = productModel.getMrp_price();
            Long selling_price = productModel.getProduct_price();
            float res = StaticUtills.discountPercentage(selling_price, mrp_price);
            binding.discount.setText(String.valueOf(res).substring(0, 2) + "%OFF");
            Glide.with(context).load(productModel.getProduct_images().get(0).getProduct_image()).fitCenter().into(binding.sumPic);
            String selectedColor = productModel.getSelectedColor();
            if (selectedColor != null && !selectedColor.isEmpty()) {
                binding.itemColor.setVisibility(View.VISIBLE);
                binding.colorItem.setBackgroundColor(Color.parseColor(productModel.getSelectedColor()));
            } else {
                binding.itemColor.setVisibility(View.GONE);
            }
            String selectedSize = productModel.getSelectedSize();
            if (selectedColor != null && !selectedColor.isEmpty()) {
                binding.itemChart.setText("Size : " + selectedSize);
            } else {
                binding.itemChart.setVisibility(View.GONE);
            }
            if (productModel.isOut_of_stock()) {
                binding.soldOut.setVisibility(View.VISIBLE);
            } else {
                binding.soldOut.setVisibility(View.INVISIBLE);
            }
            Long max_qty = productModel.getMax_quantity();
            quatitySpin(Math.round(max_qty));
            if (productModel.isTemp_favourite())
            {
                binding.sumFav.setText(R.string.remove_ad_fav);
            }else {
                binding.sumFav.setText(context.getString(R.string.add_to_fav));
            }
        }

        private void quatitySpin(int quantity) {
            List<String> quantityList = new ArrayList<>();
            for (int i = 1; i <= quantity; i++) {
                quantityList.add(i + "");
            }
            QuantityAdapter quantityAdapter = new QuantityAdapter(quantityList, context);
            binding.quantitySpin.setAdapter(quantityAdapter);
        }

    }

    private void showDialog(final ProductModel productModel) {
        String itemName=productModel.getProduct_name();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Are you sure to remove "+itemName+" Item");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                chartsClickListner.onSelectionCLick(productModel.getProduct_id(),context.getString(R.string.remove_item));
                dialog.cancel();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}