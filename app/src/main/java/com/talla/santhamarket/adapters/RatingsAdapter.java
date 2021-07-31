package com.talla.santhamarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.RatingsItemBinding;
import com.talla.santhamarket.databinding.SizeChartBinding;
import com.talla.santhamarket.models.ProductImageModel;
import com.talla.santhamarket.models.RatingModel;

import java.util.List;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.MyViewHolder> {
    private Context context;
    private List<RatingModel> ratingModelList;
    private int itemIndex=-1;

    public RatingsAdapter(Context context, List<RatingModel> ratingModelList) {
        this.context = context;
        this.ratingModelList = ratingModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RatingsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RatingsItemBinding itemBinding = RatingsItemBinding.inflate(layoutInflater, parent, false);
        return new RatingsAdapter.MyViewHolder(itemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull RatingsAdapter.MyViewHolder holder, final int position) {
        holder.onBindView(ratingModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return ratingModelList == null ? 0 : ratingModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RatingsItemBinding binding;


        public MyViewHolder(RatingsItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void onBindView(RatingModel ratingModel)
        {
            binding.ratingTitle.setText(ratingModel.getRatingMessage());
//            List<ProductImageModel> productImageModelList=ratingModel.getProductImageModelList();
//            if (productImageModelList.size()>0)
//            {
//                binding.productImagesRCV.setVisibility(View.VISIBLE);
//                ProductImagesAdapter productImagesAdapter=new ProductImagesAdapter(context,productImageModelList);
//                binding.productImagesRCV.setAdapter(productImagesAdapter);
//            }else {
//                binding.productImagesRCV.setVisibility(View.GONE);
//            }

        }

    }

}

