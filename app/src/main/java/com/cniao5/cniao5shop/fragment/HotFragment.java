package com.cniao5.cniao5shop.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.cniao5.cniao5shop.utils.Pager;
import com.google.gson.reflect.TypeToken;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cjj.MaterialRefreshLayout;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.adapter.BaseAdapter;
import com.cniao5.cniao5shop.adapter.decoration.DividerItemDecortion;
import com.cniao5.cniao5shop.adapter.HWAdapter;
import com.cniao5.cniao5shop.bean.Page;
import com.cniao5.cniao5shop.bean.Wares;
import com.cniao5.cniao5shop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * 热卖 MaterialRefreshLayout：进行数据刷新，实现下拉加载和上拉加载更多
 * RecyclerView：显示数据
 */
public class HotFragment extends BaseFragment implements Pager.onPageListener {

    private HWAdapter mAdapter;

    @ViewInject(R.id.recyclerview_hot)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.refreshlayout_hot)
    private MaterialRefreshLayout mRefreshLayout;

    @Override
    public void setToolbar() {

    }

    @Override
    public void init() {
        Pager pager = Pager.newBuilder()
                .setUrl(Constants.API.WARES_HOT)
                .setLoadMore(true)
                .setPageListener(this)
                .setPageSize(20)
                .setRefreshLayout(mRefreshLayout)
                .builder(getContext(), new TypeToken<Page<Wares>>() {
                }.getType());

        pager.request();
    }


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hot, container, false);
    }

    @Override
    public void load(List datas, int totalPage, int totalCount) {
        mAdapter = new HWAdapter(getContext(),datas);
        mAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
            @Override
            public void onItemClick(View view, int position) {

                Wares wares = mAdapter.getItem(position);

                mAdapter.showDetail(wares);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecortion(getContext(), DividerItemDecortion.VERTICAL_LIST));
    }

    @Override
    public void refresh(List datas, int totalPage, int totalCount) {
        mAdapter.clearData();
        mAdapter.addData(datas);
        mRecyclerView.scrollToPosition(0);
        mRefreshLayout.finishRefresh();
    }

    @Override
    public void loadMore(List datas, int totalPage, int totalCount) {
        mAdapter.addData(mAdapter.getDatas().size(), datas);
        mRecyclerView.scrollToPosition(mAdapter.getDatas().size());
    }

}
