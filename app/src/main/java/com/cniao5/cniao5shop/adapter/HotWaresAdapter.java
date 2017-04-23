package com.cniao5.cniao5shop.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.bean.HomeCampaign;
import com.cniao5.cniao5shop.bean.Wares;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class HotWaresAdapter extends RecyclerView.Adapter<HotWaresAdapter.ViewHolder> {


    private LayoutInflater mInflater;
    private List<Wares> mDatas;


    public HotWaresAdapter(List<Wares> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(parent.getContext());
        View view = mInflater.inflate(R.layout.template_hot_wares, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Wares wares = getData(position);

        holder.simpleDraweeView.setImageURI(Uri.parse(wares.getImgUrl()));
        holder.textTitle.setText(wares.getName());
        holder.textPrice.setText("￥ " + wares.getPrice());

    }


    public Wares getData(int position) {

        return mDatas.get(position);
    }

    public List<Wares> getDatas(){
        return mDatas;
    }

    public void clearData() {
        mDatas.clear();
        notifyItemMoved(0, mDatas.size());
    }

    public void addData(List<Wares> datas) {
       addData(0,datas);
    }

    public void addData(int position,List<Wares> datas) {
        if (datas != null && datas.size() >0){
            mDatas.addAll(datas);
            notifyItemChanged(position, mDatas.size());
        }

    }

    @Override
    public int getItemCount() {
        if (mDatas != null && mDatas.size() > 0)
            return mDatas.size();
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView simpleDraweeView;
        TextView textTitle;
        TextView textPrice;
        Button btnBuy;

        public ViewHolder(View view) {
            super(view);

            simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.drawee_view);
            textTitle = (TextView) view.findViewById(R.id.tv_title);
            textPrice = (TextView) view.findViewById(R.id.tv_price);
            btnBuy = (Button) view.findViewById(R.id.btn_add);

        }
    }
}
