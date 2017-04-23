package com.cniao5.cniao5shop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.WaresListActivity;
import com.cniao5.cniao5shop.adapter.decoration.DividerItemDecortion;
import com.cniao5.cniao5shop.adapter.HomeCampaignAdapter;
import com.cniao5.cniao5shop.bean.Banner;
import com.cniao5.cniao5shop.bean.Campaign;
import com.cniao5.cniao5shop.bean.HomeCampaign;
import com.cniao5.cniao5shop.http.OkHttpHelper;
import com.cniao5.cniao5shop.http.SimpleCallback;
import com.cniao5.cniao5shop.http.SpotsCallBack;
import com.cniao5.cniao5shop.widget.CnToolbar;
import com.cniao5.cniao5shop.widget.Constants;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.gson.Gson;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;

import java.util.List;

/**
 * 主页
 * AndroidImageSlider 轮播广告的实现：SliderLayout
 * RecyclerView 商品分类展示：
 */
public class HomeFragment extends BaseFragment {

    @ViewInject(R.id.slider)
    private SliderLayout mSliderLayout;
    private String TAG = "HomeFragment";

    @ViewInject(R.id.recyclerview)
    private RecyclerView mRecyclerView;

    private HomeCampaignAdapter mAdatper;

    private List<Banner> mBanners;

    private OkHttpHelper helper = OkHttpHelper.getInstance();

    @Override
    public void setToolbar() {

        System.out.println("toolbar---"+getToolbar().toString());

    }

    @Override
    public void init() {

        requestImages();

        initRecycleView();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    //请求轮播Banner图片数据
    private void requestImages() {

        helper.doGet(Constants.API.BANNER + "?type=1", new SpotsCallBack<List<Banner>>(getContext()) {

            @Override
            public void onSuccess(Response response, List<Banner> banners) {
                mBanners = banners;

                initSlider();
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onTokenError(Response response, int code) {

            }
        });
    }

    //初始化slider
    private void initSlider() {

        if (mBanners != null) {
            for (Banner banner : mBanners) {
                TextSliderView textSliderView = new TextSliderView(this.getActivity());
                textSliderView.image(banner.getImgUrl());
                textSliderView.description(banner.getName());
                System.out.println(banner.getName()+"====="+banner.getImgUrl());
                textSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                mSliderLayout.addSlider(textSliderView);
            }
        }

        //设置指示器
        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);

//        mSliderLayout.setCustomIndicator(mIndicator);
        //设置动画效果
        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        //设置转场效果
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.RotateUp);
        //设置时长
        mSliderLayout.setDuration(3000);

//        mSliderLayout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.d(TAG, "onPageScrolled");
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Log.d(TAG, "onPageSelected");
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                Log.d(TAG, "onPageScrollStateChanged");
//            }
//        });

    }

    private void initRecycleView() {

        helper.doGet(Constants.API.CAMPAIN_HOME, new SimpleCallback<List<HomeCampaign>>(getContext()) {

            @Override
            public void onSuccess(Response response, List<HomeCampaign> campaigns) {
                initData(campaigns);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }

            @Override
            public void onTokenError(Response response, int code) {

            }
        });
    }

    //获取主页商品数据
    private void initData(List<HomeCampaign> campaigns){

        mAdatper = new HomeCampaignAdapter(campaigns,getContext());

        mAdatper.setOnCampaignClickListener(new HomeCampaignAdapter.OnCampaignClickListener() {
            @Override
            public void onClick(View view, Campaign campaign) {
                Intent intent = new Intent(getActivity(), WaresListActivity.class);
                intent.putExtra(Constants.CAMPAIGN_ID,campaign.getId());
                startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(mAdatper);

        mRecyclerView.addItemDecoration(new DividerItemDecortion(getContext(),DividerItemDecortion.VERTICAL_LIST));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mSliderLayout.stopAutoCycle();
    }
}
