package com.cniao5.cniao5shop;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.cniao5.cniao5shop.adapter.BaseAdapter;
import com.cniao5.cniao5shop.adapter.FavoriteAdapter;
import com.cniao5.cniao5shop.adapter.decoration.CardViewtemDecortion;
import com.cniao5.cniao5shop.bean.Favorite;
import com.cniao5.cniao5shop.http.OkHttpHelper;
import com.cniao5.cniao5shop.http.SpotsCallBack;
import com.cniao5.cniao5shop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFavoriteActivity extends BaseActivity {

    @ViewInject(R.id.recycle_view)
    private RecyclerView mRecyclerview;

    private FavoriteAdapter mAdapter;

    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();

    private void initFavorite() {

        String userId = MyApplication.getInstance().getUser().getId() + "";

        if (!TextUtils.isEmpty(userId)) {
            Map<String, String> params = new HashMap<>();

            params.put("user_id", userId);

            okHttpHelper.doGet(Constants.API.FAVORITE_LIST, params, new SpotsCallBack<List<Favorite>>(this) {
                @Override
                public void onSuccess(Response response, List<Favorite> favorites) {

                    showFavorite(favorites);
                }

                @Override
                public void onError(Response response, int code, Exception e) {

                }
            });
        }
    }

    private void showFavorite(List<Favorite> favorites) {

        if (mAdapter == null) {
            mAdapter = new FavoriteAdapter(this, favorites);
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerview.addItemDecoration(new CardViewtemDecortion());

            mAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
                @Override
                public void onItemClick(View view, int position) {

                }
            });
        } else {
            mAdapter.refreshData(favorites);
            mRecyclerview.setAdapter(mAdapter);
        }

    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_my_favorite;
    }

    @Override
    public void init() {
        initFavorite();
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("我的收藏");
    }
}
