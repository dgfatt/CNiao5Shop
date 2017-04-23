package com.cniao5.cniao5shop;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cniao5.cniao5shop.bean.User;
import com.cniao5.cniao5shop.http.OkHttpHelper;
import com.cniao5.cniao5shop.http.SimpleCallback;
import com.cniao5.cniao5shop.msg.LoginRespMsg;
import com.cniao5.cniao5shop.utils.CountTimerView;
import com.cniao5.cniao5shop.utils.DESUtil;
import com.cniao5.cniao5shop.utils.ManifestUtil;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.cniao5.cniao5shop.widget.ClearEditText;
import com.cniao5.cniao5shop.widget.CnToolbar;
import com.cniao5.cniao5shop.widget.Constants;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import dmax.dialog.SpotsDialog;

/**
 * 注册页面2
 */
public class Register2Activity extends BaseActivity {

    @ViewInject(R.id.tv_tip)
    private TextView mTvTip;

    @ViewInject(R.id.et_code)
    private ClearEditText mEtCode;

    @ViewInject(R.id.btn_reSend)
    private Button mBtnReSend;

    private SpotsDialog mDialog;

    private String phone;
    private String countryCode;
    private String pwd;
    private SMSEventHandler eventHandler;
    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();
    private CountTimerView timerView;


    @OnClick(R.id.btn_reSend)
    public void reSendCode(View view) {

        /**
         * 发送验证码请求
         */
        SMSSDK.getVerificationCode("+" + countryCode, phone);

        System.out.println(phone + "~~~~~~~" + countryCode);

        /**
         * 再次发送验证码并进行计时
         */
        timerView = new CountTimerView(mBtnReSend, R.string.smssdk_resend_identify_code);
        timerView.start();

    }

    /**
     * 分割电话号码
     * @param phone
     * @return
     */
    private String splitPhoneNum(String phone) {
        StringBuilder builder = new StringBuilder(phone);
        builder.reverse();
        //每四个用空格进行切割
        for (int i = 4; i < builder.length(); i += 5) {
            builder.insert(i, ' ');
        }
        builder.reverse();

        System.out.println("builder=" + builder.toString());

        return builder.toString();
    }

    /**
     * 提交验证信息
     */
    private void submitCode() {

        //获取验证码
        String code = mEtCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtils.show(this, R.string.smssdk_write_identify_code);
            return;
        }
        //提交验证信息，提交之后会在EventHandler回调提交成功处理
        SMSSDK.submitVerificationCode(countryCode, phone, code);
        mDialog.show();
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_register2;
    }

    @Override
    public void init() {
        //获取手机号码，密码，验证码
        phone = getIntent().getStringExtra("phone");
        pwd = getIntent().getStringExtra("pwd");
        countryCode = getIntent().getStringExtra("countryCode");

        System.out.println("phone=" + phone + ",pwd=" + pwd);

        String formatedPhone = "+" + countryCode + " " + splitPhoneNum(phone);

        String tip = getString(R.string.smssdk_send_mobile_detail) + formatedPhone;

        mTvTip.setText(Html.fromHtml(tip));

        //倒计时功能
        timerView = new CountTimerView(mBtnReSend);
        timerView.start();

        /**
         * SMSSDK初始化
         */
        SMSSDK.initSDK(this, ManifestUtil.getMetaDataValue(this, "mob_sms_appKey"),
                ManifestUtil.getMetaDataValue(this, "mob_sms_appSectret"));

        eventHandler = new SMSEventHandler();
        SMSSDK.registerEventHandler(eventHandler);

        mDialog = new SpotsDialog(this, "正在校验验证码");
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("用户注册(2/2)");
        getToolbar().setRightButtonText("完成");
        getToolbar().setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCode();
            }
        });
    }


    class SMSEventHandler extends EventHandler {

        @Override
        public void afterEvent(final int event, final int result, final Object data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mDialog != null && mDialog.isShowing())
                        mDialog.dismiss();

                    /**
                     * 请求验证码回调
                     */
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        /**
                         * 注册回调
                         */
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                            //回调验证信息
                            doRegister();

                            mDialog.setMessage("正在提交验证信息");
                            mDialog.show();

                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {

                            if (mDialog != null && mDialog.isShowing())
                                mDialog.dismiss();
                            System.out.println("data" + data);
                        }
                    } else {

                        //根据服务器返回的网络错误，给toast提示

                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;

                            JSONObject object = null;
                            object = new JSONObject(
                                    throwable.getMessage());
                            String des = object.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                return;
                            }
                        } catch (Exception e) {
                            SMSLog.getInstance().w(e);
                        }
                    }
                }
            });
        }
    }

    /**
     * 注册
     */
    private void doRegister() {
        Map<String, String> params = new HashMap<>(2);
        params.put("phone", phone);
        params.put("password", DESUtil.encode(Constants.DES_KEY, pwd));

        okHttpHelper.doPost(Constants.API.AUTH_REG, params, new SimpleCallback<LoginRespMsg<User>>(this) {

            @Override
            public void onSuccess(Response response, LoginRespMsg<User> userLoginRespMsg) {


                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }


                //注册失败
                if (userLoginRespMsg.getStatus() == LoginRespMsg.STATUS_ERROR) {
                    ToastUtils.show(Register2Activity.this, "注册失败" + userLoginRespMsg.getMessage());
                    return;
                }

                //token为null，已经注册
                if (TextUtils.isEmpty(userLoginRespMsg.getToken())) {
                    ToastUtils.show(Register2Activity.this, "您已经注册");
                    return;
                }

                //保存用户信息
                MyApplication application = MyApplication.getInstance();

                application.putUser(userLoginRespMsg.getData(), userLoginRespMsg.getToken());

                ToastUtils.show(Register2Activity.this, "注册成功");

                //跳转到登录页面
                startActivity(new Intent(Register2Activity.this, LoginActivity.class));

                System.out.println("status:" + userLoginRespMsg.getStatus() + ",data:" + userLoginRespMsg.getData() + ",token:" + userLoginRespMsg.getToken());

                finish();
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(mContext, "注册失败");
            }

            @Override
            public void onTokenError(Response response, int code) {
                ToastUtils.show(mContext, "注册失败");
                System.out.println(code);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SMSSDK.unregisterEventHandler(eventHandler);
    }
}
