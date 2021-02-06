package com.talla.santhamarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.talla.santhamarket.activities.ViewProductsActivity;
import com.talla.santhamarket.databinding.DashCatLayoutBinding;
import com.talla.santhamarket.models.CategoryModel;

import java.util.List;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.MyViewHolder> {
    private Context context;
    private List<CategoryModel> categoryModelList;

    public HomeCategoryAdapter(Context context, List<CategoryModel> categoryModelList) {
        this.context = context;
        this.categoryModelList = categoryModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DashCatLayoutBinding itemBinding = DashCatLayoutBinding.inflate(layoutInflater, parent, false);
        return new HomeCategoryAdapter.MyViewHolder(itemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.onBindView(categoryModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryModelList == null ? 0 : categoryModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private DashCatLayoutBinding binding;


        public MyViewHolder(DashCatLayoutBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
            binding.rootItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewProductsActivity.class);
                    intent.putExtra("categoryId",categoryModelList.get(getAdapterPosition()).getIndex());
                    context.startActivity(intent);
                }
            });
        }

        public void onBindView(CategoryModel categoryModel) {
            Glide.with(context).load(categoryModel.getIcon()).fitCenter().into(binding.imageIcon);
            binding.catTitle.setText(categoryModel.getCategoryName() + "");
        }

    }
}
