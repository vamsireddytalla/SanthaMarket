package com.talla.santhamarket.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.LocalShopActivity;
import com.talla.santhamarket.databinding.CityItemBinding;
import com.talla.santhamarket.models.CityModel;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.MyViewHolder> {
    private List<CityModel> cityModelList;
    private Context context;

    public CityAdapter(List<CityModel> cityModelListt, Context context) {
        this.cityModelList = cityModelListt;
        this.context = context;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public CityAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CityItemBinding itemBinding = CityItemBinding.inflate(layoutInflater, parent, false);
        return new CityAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CityAdapter.MyViewHolder holder, final int position) {
        holder.onBindView(cityModelList.get(position));

        holder.binding.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CityModel cityModel = cityModelList.get(position);
                Intent intent = new Intent(context, LocalShopActivity.class);
                intent.putExtra(context.getString(R.string.intent_city_id), cityModel.getCityId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cityModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CityItemBinding binding;

        public MyViewHolder(CityItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBindView(CityModel cityModel) {

            String cityName = cityModel.getCityName();
            char ch1 = cityName.charAt(0);
            binding.letter.setText(ch1 + "");
            binding.pincode.setText(cityModel.getPincode());
            binding.cityName.setText(cityName);

        }
    }


}

