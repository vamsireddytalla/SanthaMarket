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
import com.talla.santhamarket.models.BackGroundModel;
import com.talla.santhamarket.models.BannerModel;

import java.util.List;

public class HomeBannerAdapter extends RecyclerView.Adapter<HomeBannerAdapter.MyViewHolder> {

    private Context context;
    private List<BannerModel> bannerModelList;

    public HomeBannerAdapter(Context context, List<BannerModel> bannerModelList) {
        this.context = context;
        this.bannerModelList = bannerModelList;
        notifyDataSetChanged();
    }

    public void setBannerModelList(List<BannerModel> bannerModelList) {
        this.bannerModelList = bannerModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DashHomeBannerBinding itemBinding = DashHomeBannerBinding.inflate(layoutInflater, parent, false);
        return new HomeBannerAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.onBindView(bannerModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return bannerModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private DashHomeBannerBinding bannerBinding;


        public MyViewHolder(DashHomeBannerBinding itemView) {
            super(itemView.getRoot());
            this.bannerBinding = itemView;
        }

        public void onBindView(BannerModel bannerModel) {
            String img = bannerModel.getBanner_image();
            Glide.with(context).load(img).fitCenter().into(bannerBinding.imgBamItem);
        }

    }
}
