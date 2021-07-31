package com.talla.santhamarket.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.AddressItemBinding;
import com.talla.santhamarket.databinding.StatusItemBinding;
import com.talla.santhamarket.interfaces.AddressItemListner;
import com.talla.santhamarket.models.DeliveryModel;
import com.talla.santhamarket.models.UserAddress;

import java.util.List;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.MyViewHolder> {
    private Context context;
    private List<DeliveryModel> deliveryModelList;
    private static final String TAG = "OrderStatusAdapter";

    public OrderStatusAdapter(Context context, List<DeliveryModel> deliveryModelList) {
        this.context = context;
        this.deliveryModelList = deliveryModelList;
        notifyDataSetChanged();
    }

    public void setDeliveryModelList(List<DeliveryModel> deliveryModelList) {
        this.deliveryModelList = deliveryModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderStatusAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        StatusItemBinding itemBinding = StatusItemBinding.inflate(layoutInflater, parent, false);
        return new OrderStatusAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderStatusAdapter.MyViewHolder holder, final int position) {
        holder.onBindView(deliveryModelList.get(position));

    }

    @Override
    public int getItemCount() {
        return deliveryModelList == null ? 0 : deliveryModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private StatusItemBinding binding;

        public MyViewHolder(StatusItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void onBindView(DeliveryModel deliveryModel) {

            if (getAdapterPosition() == 0) {
                if (getItemCount()==1)
                {
                    binding.topView.setVisibility(View.GONE);
                    binding.downView.setVisibility(View.GONE);
                }else {
                    binding.topView.setVisibility(View.GONE);
                    binding.downView.setVisibility(View.VISIBLE);
                }

            } else if (getLayoutPosition() == getItemCount() - 1) {
                binding.topView.setVisibility(View.VISIBLE);
                binding.downView.setVisibility(View.GONE);
            } else {
                binding.topView.setVisibility(View.VISIBLE);
                binding.downView.setVisibility(View.VISIBLE);
            }

            String delTitle = deliveryModel.getDeliveryTitle();
            if (delTitle.equalsIgnoreCase(context.getResources().getString(R.string.CANCELLED))) {
                binding.topView.getBackground().setColorFilter(context.getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
                binding.downView.getBackground().setColorFilter(context.getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
                binding.dotView.getBackground().setColorFilter(context.getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
            }
            binding.delTitle.setText(delTitle);
            binding.delDate.setText(deliveryModel.getDeliveryDate());
            binding.delMessage.setText(deliveryModel.getDeliveryMessage());


        }

    }

}

