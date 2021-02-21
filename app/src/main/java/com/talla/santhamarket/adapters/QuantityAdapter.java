package com.talla.santhamarket.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.talla.santhamarket.R;

import java.util.List;

public class QuantityAdapter extends BaseAdapter {
    private List<String> dataSet;
    Context mContext;

    public QuantityAdapter(List<String> dataSet, Context mContext) {
        this.dataSet = dataSet;
        this.mContext = mContext;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int i) {
        return dataSet.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View view1 = View.inflate(mContext, R.layout.qty_custom_spin, null);
        TextView paymentTerms = view1.findViewById(R.id.spinText);
        paymentTerms.setText(dataSet.get(i) + "");
        return view1;
    }

}
