package com.cniao5.cniao5shop.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.bean.Address;
import com.cniao5.cniao5shop.bean.Campaign;

import java.util.List;

public class AddressAdapter extends SimpleAdapter<Address> {

    private AddressLisneter addressLisneter;
    private TextView mTvEdit;
    private TextView mTvDelete;

    public AddressAdapter(Context context, List<Address> datas, AddressLisneter addressLisneter) {
        super(context, datas, R.layout.template_address);

        this.addressLisneter = addressLisneter;
    }

    public TextView getmTvEdit() {
        return mTvEdit;
    }

    public void setmTvEdit(TextView mTvEdit) {
        this.mTvEdit = mTvEdit;
    }

    public TextView getmTvDelete() {
        return mTvDelete;
    }

    public void setmTvDelete(TextView mTvDelete) {
        this.mTvDelete = mTvDelete;
    }

    @Override
    public void bindData(BaseViewHolder holder, final Address address) {
        holder.getTextView(R.id.tv_name).setText(address.getConsignee());
        holder.getTextView(R.id.tv_phone).setText(replacePhoneNum(address.getPhone()));
        holder.getTextView(R.id.tv_address).setText(address.getAddr());
        TextView tvEdit = holder.getTextView(R.id.tv_edit);
        TextView tvDelete = holder.getTextView(R.id.tv_del);

        setmTvEdit(tvEdit);
        setmTvDelete(tvDelete);

        CheckBox checkBox = (CheckBox) holder.getView(R.id.cb_is_defualt);

        boolean isDefault = address.getIsDefault();
        checkBox.setChecked(isDefault);

        if (isDefault) {
            checkBox.setText("默认地址");
            checkBox.setClickable(false);
        } else {
            //默认地址Clickable为false
            checkBox.setClickable(true);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked && addressLisneter != null) {

                        address.setIsDefault(true);
                        addressLisneter.setDefault(address);
                    }
                }
            });
        }

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressLisneter != null)
                    addressLisneter.onClickEdit(address);
            }
        });

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressLisneter != null)
                    addressLisneter.onClickDelete(address);
            }
        });
    }

    public String replacePhoneNum(String phone) {

        return phone.substring(0, phone.length() - (phone.substring(3)).length()) + "****" + phone.substring(7);
    }


    public interface AddressLisneter {

        void setDefault(Address address);

        void onClickEdit(Address address);

        void onClickDelete(Address address);

    }
}
