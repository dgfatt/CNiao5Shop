package com.cniao5.cniao5shop.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.TintTypedArray;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cniao5.cniao5shop.R;

/**
 * 自定义Toolbar
 * 1、自定义布局，获取布局并添加相应功能，继承Toolbar
 * 2、引用自定义属性：创建attribute文件，并添加相应控件，并在toolbar进行读写
 * 3、为自定义控件添加监听事件
 */
public class CnToolbar extends Toolbar {

    private LayoutInflater mInflater;

    private View mView;
    private TextView mTextTitle;
    private EditText mSearchView;
    private Button mRightButton;

    public CnToolbar(Context context) {
        this(context, null);
    }

    public CnToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CnToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化控件
        initView();

        setContentInsetsRelative(10, 10);

        if (attrs != null) {

            /**
             * 读取自定义属性
             */
            final TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                    R.styleable.CnToolbar, defStyleAttr, 0);

            //按钮图片
            final Drawable rightButtonIcon = a.getDrawable(R.styleable.CnToolbar_rightButtonIcon);
            if (rightButtonIcon != null) {
                setRightButtonIcon(rightButtonIcon);
            }

            //按钮文字
            CharSequence rightButtonText = a.getText(R.styleable.CnToolbar_rightButtonText);
            if (rightButtonText != null) {
                setRightButtonText(rightButtonText);
            }

            //搜索框
            boolean isShowSearchview = a.getBoolean(R.styleable.CnToolbar_isShowSearchView, false);
            if (isShowSearchview) {
                showSearchView();
                hideTitleView();
            }

            //资源回收
            a.recycle();
        }


    }


    /**
     * 设置按钮文字
     * @param text
     */
    public void setRightButtonText(CharSequence text) {
        mRightButton.setText(text);
        mRightButton.setVisibility(VISIBLE);
    }

    /**
     * 获取按钮
     * @return
     */
    public Button getRightButton() {
        return this.mRightButton;
    }

    /**
     * 设置按钮文字
     * @param id
     */
    public void setRightButtonText(int id) {
        setRightButtonText(getResources().getString(id));
    }

    /**
     * 设置按钮图片
     * @param icon
     */
    private void setRightButtonIcon(Drawable icon) {
        if (icon != null) {
            mRightButton.setBackground(icon);
            showButton();
        }
    }

    /**
     * 设置按钮图片
     * @param icon
     */
    public void setRightButtonIcon(int icon) {

        setRightButtonIcon(getResources().getDrawable(icon));
    }

    /**
     * 按钮监听
     * @param listener
     */
    public void setRightButtonOnClickListener(OnClickListener listener) {

        if (listener != null)
            mRightButton.setOnClickListener(listener);
    }

    /**
     * 初始化控件
     */
    private void initView() {

        //避免控件添加进去为空，setTitle()在构造函数没有调用，因此可能为空，需要为空判断
        if (mView == null) {

            mInflater = LayoutInflater.from(this.getContext());
            mView = mInflater.inflate(R.layout.toolbar, null);

            mTextTitle = (TextView) mView.findViewById(R.id.toolbar_title);
            mTextTitle.setGravity(View.TEXT_ALIGNMENT_CENTER);
            mSearchView = (EditText) mView.findViewById(R.id.toolbar_searchview);
            mRightButton = (Button) mView.findViewById(R.id.toolbar_rightButton);

            mSearchView.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        mSearchView.setHint("");
                    }
                }
            });
            mSearchView.setHint("请输入搜索内容");

            //layout布局
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);

            //将控件添加到toolbar
            addView(mView, lp);
        }
    }

    /**
     * 重写setTitle()方法
     * @param resId 标题资源id
     */
    @Override
    public void setTitle(int resId) {
        setTitle(getContext().getText(resId));
    }

    /**
     * 重写setTitle()方法
     * @param title 标题名
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);

        initView();
        mTextTitle.setText(title);
        showTitleView();
    }


    /**
     * 显示搜索框
     */
    public void showSearchView() {

        if (mSearchView != null)
            mSearchView.setVisibility(VISIBLE);

    }


    /**
     * 隐藏搜索框
     */
    public void hideSearchView() {
        if (mSearchView != null)
            mSearchView.setVisibility(GONE);
    }

    /**
     * 显示title
     */
    public void showTitleView() {
        if (mTextTitle != null)
            mTextTitle.setVisibility(VISIBLE);
    }


    /**
     * 隐藏title
     */
    public void hideTitleView() {
        if (mTextTitle != null)
            mTextTitle.setVisibility(GONE);

    }

    /**
     * 显示按钮
     */
    public void showButton() {
        if (mRightButton != null)
            mRightButton.setVisibility(VISIBLE);
    }


    /**
     * 隐藏按钮
     */
    public void hideButton() {
        if (mRightButton != null)
            mRightButton.setVisibility(GONE);

    }
}
