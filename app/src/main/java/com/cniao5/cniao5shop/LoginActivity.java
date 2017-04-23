package com.cniao5.cniao5shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cniao5.cniao5shop.bean.User;
import com.cniao5.cniao5shop.http.OkHttpHelper;
import com.cniao5.cniao5shop.http.SimpleCallback;
import com.cniao5.cniao5shop.http.SpotsCallBack;
import com.cniao5.cniao5shop.msg.LoginRespMsg;
import com.cniao5.cniao5shop.utils.DESUtil;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.cniao5.cniao5shop.widget.ClearEditText;
import com.cniao5.cniao5shop.widget.CnToolbar;
import com.cniao5.cniao5shop.widget.Constants;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

public class LoginActivity extends BaseActivity {

    @ViewInject(R.id.et_phone)
    private ClearEditText mClearEtPhone;

    @ViewInject(R.id.et_pwd)
    private ClearEditText mClearEtPwd;

    @ViewInject(R.id.btn_login)
    private Button mBtnLogin;

    @ViewInject(R.id.tv_register)
    private TextView mTvReg;

    @ViewInject(R.id.tv_forget_pwd)
    private TextView mTvForget;

    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();
    private int RESULT_CODE = 0;


    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {

    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("用户登录");

    }


    @OnClick(R.id.btn_login)
    public void login(View view) {
        String phone = mClearEtPhone.getText().toString().trim();
        String pwd = mClearEtPwd.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请输入手机号码");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.show(this, "请输入登录密码");
            return;
        }

        Map<String, String> params = new HashMap<>(2);
        params.put("phone", phone);
        params.put("password", DESUtil.encode(Constants.DES_KEY, pwd));

        okHttpHelper.doPost(Constants.API.AUTH_LOGIN, params, new SimpleCallback<LoginRespMsg<User>>(this) {

            @Override
            public void onSuccess(Response response, LoginRespMsg<User> userLoginRespMsg) {

                /**
                 * 保存用户数据
                 */
                MyApplication application = MyApplication.getInstance();

                application.putUser(userLoginRespMsg.getData(), userLoginRespMsg.getToken());

                System.out.println("user:" + userLoginRespMsg.getData() + "token:" + userLoginRespMsg.getToken() + "intent:" + application.getIntent() + "message:" + userLoginRespMsg.getMessage());

                /**
                 * 根据登录意图判断是否已经登录
                 */
                if (application.getIntent() == null && userLoginRespMsg.getData() != null && userLoginRespMsg.getToken() != null) {

                    setResult(RESULT_CODE);

                    ToastUtils.show(mContext, "登录成功");

                    finish();
                } else {
                    ToastUtils.show(mContext, "登录失败");
                }

            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(mContext, "登录失败");
            }
        });


    }

    /**
     * 跳转到注册页面
     * @param v
     */
    @OnClick(R.id.tv_register)
    public void register(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
