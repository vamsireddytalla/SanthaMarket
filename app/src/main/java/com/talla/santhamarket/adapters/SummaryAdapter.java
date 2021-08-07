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
import com.talla.santhamarket.models.ProductImageModel;
import com.talla.santhamarket.models.ProductModel;
import com.talla.santhamarket.models.SubProductModel;
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

    public SummaryAdapter(Context context, List<ProductModel> productModelList, QuantityClickListner listner, ChartsClickListner chartsClickListner) {
        this.context = context;
        this.productModelList = productModelList;
        this.chartsClickListner = chartsClickListner;
        this.listner = listner;
        notifyDataSetChanged();
    }

    public void setProductModelList(List<ProductModel> productModelList) {
        this.productModelList = productModelList;
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

        holder.binding.sumRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(productModelList.get(position));
            }
        });
        holder.binding.sumFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String favTxt = holder.binding.sumFav.getText().toString();
                if (favTxt.equalsIgnoreCase(context.getString(R.string.add_to_fav))) {
                    chartsClickListner.onSelectionCLick(productModelList.get(position).getProduct_id(), context.getString(R.string.add_to_fav));
                } else {
                    chartsClickListner.onSelectionCLick(productModelList.get(position).getTemp_favouriteId(), context.getString(R.string.remove_ad_fav));
                }
            }
        });

        holder.binding.addQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductModel productModel = productModelList.get(position);
                if (!productModel.isOut_of_stock() && (productModel.getTotalStock()>0)) {
                    int maxQty = Math.round(productModel.getMax_quantity());
                    String qty = holder.binding.qtyText.getText().toString();
                    int enteredQuatity = Integer.parseInt(qty);
                    if (enteredQuatity < maxQty && productModel.getTotalStock()>enteredQuatity) {
                        enteredQuatity += 1;
                        holder.binding.qtyText.setText(String.format("%02d", enteredQuatity));
                        productModelList.get(position).setTemp_qty(enteredQuatity);
                        double productTotalWeight = (productModel.getProduct_weight() * enteredQuatity);
                        int finalproductPrice = StaticUtills.productWeightConversion((int) productTotalWeight);
                        productModelList.get(position).setDelivery_charges(finalproductPrice);
                        listner.quantityClickListener(position, productModel);
                    }
                }

            }
        });

        holder.binding.removeQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductModel productModel = productModelList.get(position);
                if (!productModel.isOut_of_stock() && (productModel.getTotalStock()>0)) {
                    int maxQty = Math.round(productModel.getMax_quantity());
                    String qty = holder.binding.qtyText.getText().toString();
                    int enteredQuatity = Integer.parseInt(qty);
                    if (enteredQuatity >= 2) {
                        enteredQuatity -= 1;
                        holder.binding.qtyText.setText(String.format("%02d", enteredQuatity));
                        productModelList.get(position).setTemp_qty(enteredQuatity);
                        double productTotalWeight = (productModel.getProduct_weight() * enteredQuatity);
                        int finalproductPrice = StaticUtills.productWeightConversion((int) productTotalWeight);
                        productModelList.get(position).setDelivery_charges(finalproductPrice);
                        listner.quantityClickListener(position, productModel);
                    }
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
            binding.productPrice.setText(CheckUtill.FormatCost((int) Math.round(productModel.getProduct_price())) + context.getString(R.string.Rs));
            binding.mrpPrice.setText(CheckUtill.FormatCost((int) Math.round(productModel.getMrp_price())) + context.getString(R.string.Rs));
            binding.mrpPrice.setPaintFlags(binding.mrpPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            double mrp_price = productModel.getMrp_price();
            double selling_price = productModel.getProduct_price();
            int res = StaticUtills.discountPercentage(selling_price, mrp_price);
            binding.discount.setText(String.valueOf(res) + "%OFF");

            String selectedColor = productModel.getSelectedColor();
            if (selectedColor != null && !selectedColor.isEmpty()) {
                binding.itemColor.setVisibility(View.VISIBLE);
                binding.colorItem.setBackgroundColor(Integer.parseInt(selectedColor));
            } else {
                binding.itemColor.setVisibility(View.GONE);
            }
            Glide.with(context).load(getSelectedProductImage(productModel.getSubProductModelList(), selectedColor)).fitCenter().into(binding.sumPic);
            String selectedSize = productModel.getSelectedSize();
            if (selectedSize != null && !selectedSize.isEmpty()) {
                binding.itemChart.setText("Size : " + selectedSize);
            } else {
                binding.itemChart.setVisibility(View.GONE);
            }
            if (productModel.isOut_of_stock() || (productModel.getTotalStock()<=0)) {
                binding.soldOut.setVisibility(View.VISIBLE);
            } else {
                binding.soldOut.setVisibility(View.INVISIBLE);
            }
            if (productModel.isTemp_favourite()) {
                binding.sumFav.setText(R.string.remove_ad_fav);
            } else {
                binding.sumFav.setText(context.getString(R.string.add_to_fav));
            }

            binding.qtyText.setText(productModel.getTemp_qty() + "");
            listner.quantityClickListener(getAdapterPosition(), productModel);

        }

    }


    private String getSelectedProductImage(List<SubProductModel> subProductModelList, String prodColor) {
        for (int i = 0; i < subProductModelList.size(); i++) {
            List<ProductImageModel> productImageModelList = subProductModelList.get(i).getProduct_images();
            if (subProductModelList.get(i).getProduct_color() != null) {
                if (subProductModelList.get(i).getProduct_color().equalsIgnoreCase(prodColor)) {
                    Log.d("SUMMARY ADAPTER", prodColor + i);
                    return productImageModelList.get(0).getProduct_image();
                }
            }

        }
        return subProductModelList.get(0).getProduct_images().get(0).getProduct_image();
    }

    private void showDialog(final ProductModel productModel) {
        String itemName = productModel.getProduct_name();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Are you sure to remove " + itemName + " Item");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                chartsClickListner.onSelectionCLick(productModel.getProduct_id(), context.getString(R.string.remove_item));
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