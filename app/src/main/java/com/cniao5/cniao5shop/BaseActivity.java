package com.cniao5.cniao5shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cniao5.cniao5shop.bean.User;
import com.cniao5.cniao5shop.widget.CnToolbar;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public abstract class BaseActivity extends AppCompatActivity {

    private void initToolbar() {
        if (getToolbar() != null){
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            setToolbar();
        }
    }

    public CnToolbar getToolbar() {
        return (CnToolbar)findViewById(R.id.toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        ViewUtils.inject(this);

        initToolbar();

        init();

    }

    public abstract int getLayoutId();

    public abstract void init();

    public abstract void setToolbar();

    public void startActivity(Intent intent, boolean isNeedLogin) {

        if (isNeedLogin) {
            User user = MyApplication.getInstance().getUser();

            if (user != null) {
                super.startActivity(intent);
            } else {
                MyApplication.getInstance().putIntent(intent);
                Intent loginIntent = new Intent(this, LoginActivity.class);
                super.startActivity(loginIntent);
            }
        } else {
            super.startActivity(intent);
        }
    }
}
