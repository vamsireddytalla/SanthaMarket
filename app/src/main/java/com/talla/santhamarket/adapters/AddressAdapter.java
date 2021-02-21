package com.talla.santhamarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.AddressItemBinding;
import com.talla.santhamarket.interfaces.AddressItemListner;
import com.talla.santhamarket.models.UserAddress;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {
    private Context context;
    private List<UserAddress> userAddressList;
    private AddressItemListner addressItemListner;

    public AddressAdapter(Context context, List<UserAddress> userAddressList,AddressItemListner addressItemListner) {
        this.context = context;
        this.userAddressList = userAddressList;
        this.addressItemListner =addressItemListner;
        notifyDataSetChanged();
    }

    public void setUserAddressList(List<UserAddress> userAddressList) {
        this.userAddressList = userAddressList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AddressItemBinding itemBinding = AddressItemBinding.inflate(layoutInflater, parent, false);
        return new AddressAdapter.MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.MyViewHolder holder, final int position) {
        holder.onBindView(userAddressList.get(position));

        holder.binding.defaultAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                addressItemListner.addressItemListner("Check",position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userAddressList == null ? 0 : userAddressList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private AddressItemBinding binding;

        public MyViewHolder(AddressItemBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

            binding.threeDots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, binding.threeDots);
                    popupMenu.inflate(R.menu.options_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.edit:
                                    addressItemListner.addressItemListner("Edit",getAdapterPosition());
                                    break;
                                case R.id.delete:
                                    addressItemListner.addressItemListner("Delete",getAdapterPosition());
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

        }

        public void onBindView(UserAddress userAddress) {
            binding.addUserName.setText("User Name : "+userAddress.getUser_name());
            binding.addNumber.setText("Alternative Phone : "+userAddress.getUser_alter_phone());
            binding.addPhone.setText("Phone Number : "+userAddress.getUser_phone());
            binding.addCountry.setText("Country : "+userAddress.getUser_country());
            binding.addState.setText("State : "+userAddress.getUser_state());
            binding.addCity.setText("City : "+userAddress.getUser_city());
            binding.addPincode.setText("Pincode : "+userAddress.getUser_pincode());
            binding.addAddress.setText("Address : "+userAddress.getUser_streetAddress());
            boolean val=userAddress.isDefaultAddress();
            binding.defaultAddress.setChecked(val);
            if (val)
            {
                binding.defaultAddress.setText("DEFAULT ADDRESS");
            }else {
                binding.defaultAddress.setText("Make Default Address");
            }
        }

    }
}

