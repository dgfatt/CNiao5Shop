package com.cniao5.cniao5shop.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.bean.Favorite;
import com.cniao5.cniao5shop.bean.Wares;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class FavoriteAdapter extends SimpleAdapter<Favorite> {

    public FavoriteAdapter(Context context, List<Favorite> datas) {
        super(context, datas, R.layout.template_favorite_item);
    }

    @Override
    public void bindData(BaseViewHolder holder, Favorite favorite) {
        Wares wares = favorite.getWares();
        holder.getTextView(R.id.tv_title).setText(wares.getName());
        holder.getTextView(R.id.tv_price).setText("￥ " + wares.getPrice());

        SimpleDraweeView draweeView = (SimpleDraweeView) holder.getView(R.id.drawee_view);
        draweeView.setImageURI(Uri.parse(wares.getImgUrl()));

        Button buttonRemove = holder.getButton(R.id.btn_remove);
        Button buttonLike = holder.getButton(R.id.btn_like);

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}
