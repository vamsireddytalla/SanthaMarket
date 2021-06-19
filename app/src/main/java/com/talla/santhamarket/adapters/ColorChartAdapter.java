package com.talla.santhamarket.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.ColorChartBinding;
import com.talla.santhamarket.interfaces.ChartsClickListner;

import java.util.List;
import java.util.Map;

public class ColorChartAdapter extends RecyclerView.Adapter<ColorChartAdapter.MyViewHolder> {
    private Context context;
    private List<Map.Entry<String, Object>> sizeList;
    private int itemIndex = 0;
    private ChartsClickListner listner;
    private boolean isFirstTime=true;

    public ColorChartAdapter(Context context, List<Map.Entry<String, Object>> sizeList,ChartsClickListner listner) {
        this.context = context;
        this.sizeList = sizeList;
        this.listner=listner;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ColorChartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ColorChartBinding itemBinding = ColorChartBinding.inflate(layoutInflater, parent, false);
        return new ColorChartAdapter.MyViewHolder(itemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull ColorChartAdapter.MyViewHolder holder, final int position) {
        holder.onBindView(sizeList.get(position));

        holder.binding.colorRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemIndex = position;
                listner.onSelectionCLick(sizeList.get(position).getKey().toString(),context.getString(R.string.Selected_Color));
                notifyDataSetChanged();
            }
        });

        if (itemIndex == position) {
            holder.binding.colorRoot.setBackground(context.getResources().getDrawable(R.drawable.color_slected_item));
            listner.onSelectionCLick(sizeList.get(position).getKey().toString(),context.getString(R.string.Selected_Color));
        } else {
            holder.binding.colorRoot.setBackground(context.getResources().getDrawable(R.drawable.linear_border));
        }
    }

    @Override
    public int getItemCount() {
        return sizeList == null ? 0 : sizeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ColorChartBinding binding;


        public MyViewHolder(ColorChartBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void onBindView(Map.Entry<String, Object> colorObj) {
            binding.colorItem.setBackgroundColor(Color.parseColor(colorObj.getValue().toString()));
        }

    }

}