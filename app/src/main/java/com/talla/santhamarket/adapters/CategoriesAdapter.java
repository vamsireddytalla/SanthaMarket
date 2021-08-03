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
import com.talla.santhamarket.activities.LocalProductsActivity;
import com.talla.santhamarket.databinding.CategoriesItemBinding;
import com.talla.santhamarket.models.CategoryModel;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {
    private List<CategoryModel> categoryModelList;
    private Context context;

    public CategoriesAdapter(List<CategoryModel> categoryModelList, Context context) {
        this.categoryModelList = categoryModelList;
        this.context = context;
        notifyDataSetChanged();
    }

    public void setCategoryModelList(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public CategoriesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CategoriesItemBinding itemBinding = CategoriesItemBinding.inflate(layoutInflater, parent, false);
        return new CategoriesAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.MyViewHolder holder, final int position) {
        holder.onBindView(categoryModelList.get(position));

        holder.binding.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LocalProductsActivity.class);
                intent.putExtra(context.getString(R.string.intent_category_shopId),categoryModelList.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CategoriesItemBinding binding;

        public MyViewHolder(final CategoriesItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBindView(CategoryModel categoryModel) {

            binding.categoryName.setText(categoryModel.getCat_name());
            Glide.with(context).load(categoryModel.getCat_logo()).fitCenter().into(binding.categoryImage);
        }
    }


}
