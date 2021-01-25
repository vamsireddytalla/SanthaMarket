package com.talla.santhamarket.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.talla.santhamarket.databinding.DashHomeBannerBinding;

import java.util.List;

public class HomeBannerAdapter extends RecyclerView.Adapter<HomeBannerAdapter.MyViewHolder> {

    private Context context;
    private List<String> stringList;

    public HomeBannerAdapter(Context context, List<String> stringList) {
        this.context = context;
        this.stringList = stringList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DashHomeBannerBinding itemBinding = DashHomeBannerBinding.inflate(layoutInflater, parent,false);
        return new HomeBannerAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.onBindView(stringList.get(position));
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private DashHomeBannerBinding bannerBinding;


        public MyViewHolder(DashHomeBannerBinding itemView) {
            super(itemView.getRoot());
            this.bannerBinding = itemView;
        }

        public void onBindView(String s) {
            String img =s;
            Glide.with(context).load(img).fitCenter().into(bannerBinding.imgBamItem);
        }

    }
}
