package com.cniao5.cniao5shop;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cniao5.cniao5shop.adapter.BaseAdapter;
import com.cniao5.cniao5shop.adapter.HWAdapter;
import com.cniao5.cniao5shop.adapter.decoration.DividerItemDecortion;
import com.cniao5.cniao5shop.bean.Page;
import com.cniao5.cniao5shop.bean.Wares;
import com.cniao5.cniao5shop.utils.Pager;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.cniao5.cniao5shop.widget.CnToolbar;
import com.cniao5.cniao5shop.widget.Constants;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

public class WaresListActivity extends BaseActivity implements Pager.onPageListener<Wares>, TabLayout.OnTabSelectedListener, View.OnClickListener {


    @ViewInject(R.id.tab_layout)
    private TabLayout mTabLayout;

    @ViewInject(R.id.tv_summary)
    private TextView mTvSummary;

    @ViewInject(R.id.recycle_view)
    private RecyclerView mRecycleViewWares;

    @ViewInject(R.id.refresh_layout)
    private MaterialRefreshLayout mRefreshLayout;

    private HWAdapter waresAdapter;

    @ViewInject(R.id.toolbar)
    private CnToolbar mToolbar;

    private long campaignId = 0;
    private int orderBy = 0;

    private Pager pager;
    private static final int TAG_DEFAULT = 0;
    private static final int TAG_SALE = 1;
    private static final int TAG_PRICE = 2;

    private static final int ACTION_LIST = 1;
    private static final int ACTION_GRID = 2;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wares_list;
    }

    @Override
    public void init() {

        campaignId = getIntent().getLongExtra(Constants.CAMPAIGN_ID, 0);

        //初始化Tab
        initTab();

        //获取数据
        getData();
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle(R.string.wares_list);
        getToolbar().setRightButtonIcon(R.drawable.icon_grid_32);
        getToolbar().getRightButton().setTag(ACTION_LIST);
        getToolbar().setRightButtonOnClickListener(this);

        System.out.println("toolbar---"+getToolbar().toString());
    }

    private void getData() {

        pager = Pager.newBuilder()
                .setUrl(Constants.API.WARES_CAMPAIGN_LIST)
                .putParams("campaignId", campaignId)
                .putParams("orderBy", orderBy)
                .setLoadMore(true)
                .setPageSize(20)
                .setRefreshLayout(mRefreshLayout)
                .setPageListener(this)
                .builder(this, new TypeToken<Page<Wares>>() {
                }.getType());

        pager.request();
    }


    //初始化tab
    private void initTab() {

        TabLayout.Tab tab = mTabLayout.newTab();
        tab.setText(R.string.defaults);
        tab.setTag(TAG_DEFAULT);
        mTabLayout.addTab(tab);

        tab = mTabLayout.newTab();
        tab.setText(R.string.sales);
        tab.setTag(TAG_SALE);
        mTabLayout.addTab(tab);

        tab = mTabLayout.newTab();
        tab.setText(R.string.price);
        tab.setTag(TAG_PRICE);
        mTabLayout.addTab(tab);

        mTabLayout.setOnTabSelectedListener(this);

    }

    @Override
    public void load(List<Wares> datas, int totalPage, int totalCount) {

        mTvSummary.setText("共有" + totalCount + "件商品");

        if (waresAdapter == null) {
            waresAdapter = new HWAdapter(this, datas);

            waresAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
                @Override
                public void onItemClick(View view, int position) {
                    waresAdapter.showDetail(waresAdapter.getItem(position));
                }
            });

            mRecycleViewWares.setAdapter(waresAdapter);
            mRecycleViewWares.setLayoutManager(new LinearLayoutManager(this));
            mRecycleViewWares.addItemDecoration(new DividerItemDecortion(this, DividerItemDecortion.VERTICAL_LIST));
            mRecycleViewWares.setItemAnimator(new DefaultItemAnimator());
        } else {
            waresAdapter.refreshData(datas);
        }
    }

    @Override
    public void refresh(List<Wares> datas, int totalPage, int totalCount) {
        waresAdapter.refreshData(datas);
        mRecycleViewWares.scrollToPosition(0);
    }

    @Override
    public void loadMore(List<Wares> datas, int totalPage, int totalCount) {

        waresAdapter.loadMore(datas);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        orderBy = (int) tab.getTag();
        pager.putParams("orderBy", orderBy);
        pager.request();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onClick(View v) {

        int action = (int) v.getTag();

        if (ACTION_LIST == action) {
            //更改图标，布局，tag
            mToolbar.setRightButtonIcon(R.drawable.icon_list_32);
            mToolbar.getRightButton().setTag(ACTION_GRID);
            waresAdapter.reSetLayout(R.layout.template_grid_wares);
            mRecycleViewWares.setLayoutManager(new GridLayoutManager(this, 2));
            mRecycleViewWares.setAdapter(waresAdapter);
        } else if (ACTION_GRID == action) {
            mToolbar.setRightButtonIcon(R.drawable.icon_grid_32);
            mToolbar.getRightButton().setTag(ACTION_LIST);
            waresAdapter.reSetLayout(R.layout.template_hot_wares);
            mRecycleViewWares.setLayoutManager(new LinearLayoutManager(this));
            mRecycleViewWares.setAdapter(waresAdapter);

        }
    }
}
