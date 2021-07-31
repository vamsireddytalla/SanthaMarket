package com.talla.santhamarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.DetailOrderActivity;
import com.talla.santhamarket.activities.ReviewActivity;
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
                OrderModel orderModel = orderModelList.get(position);
                if (orderModel.isLocal()) {
                    Intent intent = new Intent(context, DetailOrderActivity.class);
                    intent.putExtra(context.getString(R.string.order_status), orderModelList.get(position));
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra(context.getString(R.string.order_status), orderModelList.get(position));
                    context.startActivity(intent);
                }
            }
        });

        holder.binding.ratingRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReviewActivity.class);
                intent.putExtra(context.getString(R.string.order_status), orderModelList.get(position));
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

        public void onBindView(OrderModel orderModel) {
            Glide.with(context).load(orderModel.getProduct_image()).fitCenter().into(binding.orderImage);
            binding.orderTitle.setText(orderModel.getProduct_name());
            binding.orderDate.setText("Ordered At : " + orderModel.getOrdered_date());
            binding.orderPaymentMethod.setText("Payment Mode : " + orderModel.getPayment_method());
            boolean isDelivered = orderModel.isDelivered();
            if (isDelivered) {
                binding.orderStatus.setText(context.getResources().getString(R.string.DELIVERED));
                binding.orderStatus.setTextColor(context.getResources().getColor(R.color.delivered_color));
                binding.myRatingBar.setVisibility(View.VISIBLE);
            } else {
                int size = orderModel.getDeliveryModelList().size();
                String orderStatus = orderModel.getDeliveryModelList().get(size-1).getDeliveryTitle();
                if (orderStatus.equalsIgnoreCase(context.getResources().getString(R.string.PROCESSING))) {
                    binding.orderStatus.setTextColor(context.getResources().getColor(R.color.processing_color));
                    binding.orderStatus.setText(orderStatus);
                } else if (orderStatus.equalsIgnoreCase(context.getResources().getString(R.string.ORDERED))) {
                    binding.orderStatus.setTextColor(context.getResources().getColor(R.color.ordered_color));
                    binding.orderStatus.setText(orderStatus);
                    RunAnimation();
                } else if (orderStatus.equalsIgnoreCase(context.getResources().getString(R.string.CANCELLED))) {
                    binding.orderStatus.setTextColor(context.getResources().getColor(R.color.cancelled_color));
                    binding.orderStatus.setText(orderStatus);
                }
            }

            if (orderModel.getRatingModel()!=null)
            {
                binding.myRatingBar.setRating((float) orderModel.getRatingModel().getRating());
            }

        }

        private void RunAnimation() {
            Animation a = AnimationUtils.loadAnimation(context, R.anim.animate_text);
            a.reset();
            binding.orderStatus.clearAnimation();
            binding.orderStatus.startAnimation(a);
        }

    }

}

