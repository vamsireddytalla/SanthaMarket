package com.talla.santhamarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.WebViewActivity;
import com.talla.santhamarket.databinding.OrderItemBinding;
import com.talla.santhamarket.databinding.RatingsItemBinding;
import com.talla.santhamarket.models.OrderModel;
import com.talla.santhamarket.models.ProductImageModel;
import com.talla.santhamarket.models.RatingModel;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {
    private Context context;
    private List<OrderModel> orderModelList;

    public OrdersAdapter(Context context, List<OrderModel> orderModelList) {
        this.context = context;
        this.orderModelList = orderModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrdersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        OrderItemBinding itemBinding = OrderItemBinding.inflate(layoutInflater, parent, false);
        return new OrdersAdapter.MyViewHolder(itemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.MyViewHolder holder, final int position) {
        holder.onBindView(orderModelList.get(position));
        holder.binding.orderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, WebViewActivity.class);
                intent.putExtra(context.getString(R.string.order_status),orderModelList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderModelList == null ? 0 : orderModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private OrderItemBinding binding;

        public MyViewHolder(OrderItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void onBindView(OrderModel orderModel)
        {
            Glide.with(context).load(orderModel.getProduct_image()).fitCenter().into(binding.orderImage);
            binding.orderTitle.setText(orderModel.getProduct_name());
            binding.orderDate.setText("Ordered At: "+orderModel.getOrdered_date());
            binding.orderPaymentMethod.setText("Payment Mode : "+orderModel.getPayment_method());
            boolean isDelivered=orderModel.isDelivered();
            if (isDelivered)
            {
                binding.deliveryTxt.setText("Delivered");
                binding.deliveryTxt.setTextColor(context.getResources().getColor(R.color.greeen));
            }else {
                binding.deliveryTxt.setText("Pending");
                binding.deliveryTxt.setTextColor(context.getResources().getColor(R.color.orange));
            }
            String cancelProduct=orderModel.getCancel_product();
            if (cancelProduct!=null && !cancelProduct.isEmpty())
            {
                binding.deliveryTxt.setText(cancelProduct);
                binding.deliveryTxt.setTextColor(context.getResources().getColor(R.color.red));
            }
        }

    }

}

