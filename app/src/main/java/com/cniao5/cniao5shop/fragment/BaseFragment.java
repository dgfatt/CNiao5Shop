package com.cniao5.cniao5shop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cniao5.cniao5shop.LoginActivity;
import com.cniao5.cniao5shop.MyApplication;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.bean.User;
import com.cniao5.cniao5shop.widget.CnToolbar;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public abstract class BaseFragment extends Fragment {

    private void initToolbar() {
        if (getToolbar() != null) {

            setToolbar();
        }
    }

    public abstract void setToolbar();

    public CnToolbar getToolbar() {
        return (CnToolbar) getActivity().findViewById(R.id.toolbar_search_view);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = createView(inflater, container, savedInstanceState);
        ViewUtils.inject(this, view);

        initToolbar();

        init();

        return view;
    }

    public abstract void init();

    public abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public void startActivity(Intent intent, boolean isNeedLogin) {

        if (isNeedLogin) {
            User user = MyApplication.getInstance().getUser();

            if (user != null) {
                super.startActivity(intent);
            } else {
                MyApplication.getInstance().putIntent(intent);
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                super.startActivity(loginIntent);
            }
        } else {
            super.startActivity(intent);
        }
    }
}
