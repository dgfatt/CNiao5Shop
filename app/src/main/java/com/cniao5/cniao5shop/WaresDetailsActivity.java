package com.cniao5.cniao5shop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cniao5.cniao5shop.bean.Favorite;
import com.cniao5.cniao5shop.bean.User;
import com.cniao5.cniao5shop.bean.Wares;
import com.cniao5.cniao5shop.http.OkHttpHelper;
import com.cniao5.cniao5shop.http.SpotsCallBack;
import com.cniao5.cniao5shop.utils.CartProvider;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.cniao5.cniao5shop.widget.CnToolbar;
import com.cniao5.cniao5shop.widget.Constants;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import dmax.dialog.SpotsDialog;

/**
 * 商品详情
 */
public class WaresDetailsActivity extends BaseActivity {

    @ViewInject(R.id.webView)
    private WebView mWebView;

    private WebAppInterface mAppInterface;

    private CartProvider mCartProvider;

    private Wares mWares;

    private SpotsDialog mDialog;

    private WebClient mWebClient;

    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();


    /**
     * 初始化WebView
     * 1.设置允许执⾏JS脚本：
     * webSettings.setJavaScriptEnabled(true);
     * 2.添加通信接口
     * webView.addJavascriptInterface(Interface,”InterfaceName”)
     * 3.JS调⽤Android
     * InterfaceName.MethodName
     * 4.Android调⽤JS
     * webView.loadUrl("javascript:functionName()");
     */
    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        //1、设置允许执行Js脚本
        settings.setJavaScriptEnabled(true);
        //默认为true，无法加载页面图片
        settings.setBlockNetworkImage(false);
        //设置允许缓存
        settings.setAppCacheEnabled(true);

        mWebView.loadUrl(Constants.API.WARES_DETAILS);

        System.out.println(Constants.API.WARES_DETAILS);

        mAppInterface = new WebAppInterface(this);
        mWebClient = new WebClient();

        //2.添加通信接口 name和web页面名称一致
        mWebView.addJavascriptInterface(mAppInterface, "appInterface");

        mWebView.setWebViewClient(mWebClient);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_wares_details;
    }

    @Override
    public void init() {
        Serializable serializable = getIntent().getSerializableExtra(Constants.WARES);
        if (serializable == null)
            this.finish();

        mDialog = new SpotsDialog(this, "loading...");
        mDialog.show();

        mWares = (Wares) serializable;
        mCartProvider = CartProvider.getInstance(this);

        initWebView();
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle(R.string.wares_details);

        getToolbar().setRightButtonText(getString(R.string.share));
        getToolbar().setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
    }


    /**
     * 显示分享界面
     */
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://www.cniao5.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mWares.getName());
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl(mWares.getImgUrl());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.cniao5.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(mWares.getName());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.share));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.cniao5.com");

        // 启动分享GUI
        oks.show(this);
    }


    /**
     * 页面加载完之后才调用方法进行显示数据
     * 需要实现一个监听判断页面是否加载完
     */
    class WebClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();
            //显示详情
            mAppInterface.showDetail();
        }

    }


    /**
     * 定义接口进行通讯
     */
    class WebAppInterface {

        private Context context;

        public WebAppInterface(Context context) {
            this.context = context;
        }

        /**
         * 方法名和js代码中必须一直
         * 显示详情页
         */
        @JavascriptInterface
        private void showDetail() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //调用js代码
                    mWebView.loadUrl("javascript:showDetail(" + mWares.getId() + ")");

                    System.out.println("showDetail--------" + mWares.getId() + "------------" + mWares.getName());
                }
            });
        }

        /**
         * 添加到购物车
         * @param id 商品id
         */
        @JavascriptInterface
        public void buy(long id) {
            mCartProvider.put(mWares);

            ToastUtils.show(context, R.string.has_add_cart);

        }

        /**
         * 添加到收藏夹
         * @param id 商品id
         */
        @JavascriptInterface
        public void addToCart(long id) {
            addToFavorite();
        }
    }

    /**
     * 添加到收藏夹
     */
    private void addToFavorite() {
        User user = MyApplication.getInstance().getUser();
        if (user == null){
            startActivity(new Intent(this,LoginActivity.class));
        }

        String userId = MyApplication.getInstance().getUser().getId()+"";

        if (!TextUtils.isEmpty(userId)) {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", userId + "");
            params.put("ware_id", mWares.getId() + "");

            okHttpHelper.doGet(Constants.API.FAVORITE_CREATE, params, new SpotsCallBack<List<Favorite>>(this) {
                @Override
                public void onSuccess(Response response, List<Favorite> favorites) {

                    System.out.println("name-----" + mWares.getName());
                    ToastUtils.show(WaresDetailsActivity.this, getString(R.string.has_add_favorite));
                }

                @Override
                public void onError(Response response, int code, Exception e) {
                    LogUtils.d("code:" + code);
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ShareSDK.stopSDK(this);
    }
}
