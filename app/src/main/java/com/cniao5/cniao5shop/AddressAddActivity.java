package com.cniao5.cniao5shop;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.cniao5.cniao5shop.city.CityModel;
import com.cniao5.cniao5shop.city.DistrictModel;
import com.cniao5.cniao5shop.city.ProvinceModel;
import com.cniao5.cniao5shop.city.XmlParserHandler;
import com.cniao5.cniao5shop.http.OkHttpHelper;
import com.cniao5.cniao5shop.http.SpotsCallBack;
import com.cniao5.cniao5shop.msg.BaseResMsg;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.cniao5.cniao5shop.widget.CnToolbar;
import com.cniao5.cniao5shop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.squareup.okhttp.Response;

import java.io.InputStream;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 添加地址
 */
public class AddressAddActivity extends BaseActivity {

    private List<ProvinceModel> mProvinces;
    private List<String> mProvincesStr = new ArrayList<>();
    private ArrayList<ArrayList<String>> mCities = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<ArrayList<String>>> mDistricts = new ArrayList<ArrayList<ArrayList<String>>>();

    private int TAG;

    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();

    //省市三级联动选择器
    private OptionsPickerView mCityPikerView;

//    private CityPicker cityPicker = new CityPicker.Builder(AddressAddActivity.this)
//            .textSize(16)
//            .title("城市选择")
//            .titleBackgroundColor("#234Dfa")
//            .confirTextColor("#000000")
//            .cancelTextColor("#000000")
//            .textColor(Color.parseColor("#000000"))
//            .provinceCyclic(true)
//            .cityCyclic(false)
//            .districtCyclic(false)
//            .visibleItemsCount(7)
//            .itemPadding(10)
//            .build();

    @ViewInject(R.id.toolbar)
    private CnToolbar mToolBar;

    @ViewInject(R.id.et_consignee)
    private EditText mEtConsignee;

    @ViewInject(R.id.et_phone)
    private EditText mEtPhone;

    @ViewInject(R.id.et_add_des)
    private EditText mEtAddDes;

    @ViewInject(R.id.tv_address)
    private TextView mTvAddress;

    @Override
    public int getLayoutId() {
        return R.layout.activity_address_add;
    }

    @Override
    public void init() {
        initAddress();
    }

    @Override
    public void setToolbar() {

        /**
         * 根据传入的TAG，toolbar显示相应布局
         */
        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TAG == Constants.TAG_SAVE) {
                    //添加新地址
                    creatAddress();
                } else if (TAG == Constants.TAG_COMPLETE) {
                    //编辑地址
                    updateAddress();
                }
            }
        });
    }

    /**
     * 显示添加地址页面
     */
    private void showAddress() {
        String consignee = getIntent().getStringExtra("consignee");
        String phone = getIntent().getStringExtra("phone");
        String addr = getIntent().getStringExtra("addr");

        String address[] = addr.split("-");

        mEtConsignee.setText(consignee);
        mEtPhone.setText(phone);
        mTvAddress.setText(address[0] == null ? "" : address[0]);
        mEtAddDes.setText(address[1] == null ? "" : address[1]);

    }

    /**
     * 编辑地址
     */
    public void updateAddress() {

        String consignee = mEtConsignee.getText().toString();
        String phone = mEtPhone.getText().toString();
        String addr = mTvAddress.getText().toString() + "-" + mEtAddDes.getText().toString();

        String userId = getIntent().getStringExtra("id");
        String zip_code = getIntent().getStringExtra("zip_code");
        String is_default = getIntent().getStringExtra("is_default");

        Map<String, String> params = new HashMap<>(1);
        params.put("id", userId);
        params.put("consignee", consignee);
        params.put("phone", phone);
        params.put("addr", addr);
        params.put("zip_code", zip_code);
        params.put("is_default", is_default);

        okHttpHelper.doPost(Constants.API.ADDR_UPDATE, params, new SpotsCallBack<BaseResMsg>(this) {

            @Override
            public void onSuccess(Response response, BaseResMsg resMsg) {
                if (resMsg.getStatus() == resMsg.STATUS_SUCCESS) {
                    //从服务端更新地址
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

    }

    /**
     * 添加新地址
     */
    private void creatAddress() {
        String consignee = mEtConsignee.getText().toString();
        String phone = mEtPhone.getText().toString();
        String address = mTvAddress.getText().toString() + "-" + mEtAddDes.getText().toString();

        if (checkPhone(phone)) {
            String userId = MyApplication.getInstance().getUser().getId() + "";

            if (!TextUtils.isEmpty(userId)) {
                Map<String, String> params = new HashMap<>(1);
                params.put("user_id", userId);
                params.put("consignee", consignee);
                params.put("phone", phone);
                params.put("addr", address);
                params.put("zip_code", "000000");

                okHttpHelper.doPost(Constants.API.ADDR_CREATE, params, new SpotsCallBack<BaseResMsg>(this) {
                    @Override
                    public void onSuccess(Response response, BaseResMsg resMsg) {
                        if (resMsg.getStatus() == BaseResMsg.STATUS_SUCCESS) {
                            setResult(RESULT_OK);
                            System.out.println(resMsg.getStatus() + "----------" + resMsg.getMessage());

                            finish();

                        }
                    }

                    @Override
                    public void onError(Response response, int code, Exception e) {

                    }
                });
            }
        }

    }

    /**
     * 检验手机号码
     *
     * @param phone
     * @return
     */
    private boolean checkPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请输入手机号码");
            return false;
        }
        if (phone.length() != 11) {
            ToastUtils.show(this, "手机号码长度不对");
            return false;
        }

        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            ToastUtils.show(this, "您输入的手机号码格式不正确");
            return false;
        }

        return true;
    }

    /**
     * 初始化省市数据
     */
    private void initProvinceDatas() {
        AssetManager asset = getAssets();

        try {
            InputStream is = asset.open("province_data.xml");
            //创建一个解析xml的工厂对象
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //解析XMl
            SAXParser parser = factory.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(is, handler);
            is.close();

            //获取解析出来的数据
            mProvinces = handler.getDataList();

        } catch (Throwable e) {
            e.printStackTrace();
        }

        //省份名称List //把省份名称放入provincesStrs
        for (ProvinceModel p : mProvinces) {

            mProvincesStr.add(p.getName());

            //获取城市List数据
            List<CityModel> cities = p.getCityList();

            ArrayList<String> cityNames = new ArrayList<>(cities.size());

            for (CityModel c : cities) {

                cityNames.add(c.getName());

                ArrayList<ArrayList<String>> dts = new ArrayList<>(); // 地区 List

                //获取地区List数据
                List<DistrictModel> districts = c.getDistrictList();

                ArrayList<String> districtNames = new ArrayList<>(districts.size());

                for (DistrictModel d : districts) {
                    districtNames.add(d.getName());
                    dts.add(districtNames);
                    mDistricts.add(dts);
                }
            }
            mCities.add(cityNames);
        }
    }


    /**
     * 初始化地址数据
     */
    private void initAddress() {

        TAG = getIntent().getIntExtra("tag", -1);

        if (TAG == Constants.TAG_SAVE) {
            mToolBar.getRightButton().setText("保存");
            mToolBar.setTitle("添加新地址");
        } else if (TAG == Constants.TAG_COMPLETE) {
            mToolBar.getRightButton().setText("完成");
            mToolBar.setTitle("编辑地址");
            showAddress();
        }


//        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
//            @Override
//            public void onSelected(String... citySelected) {
//                String address = citySelected[0] + " "
//                        + citySelected[1] + " "
//                        + citySelected[2];
//                mTvAddress.setText(address);
//
//            }
//        });


        /**
         * 初始化省市数据
         */
        initProvinceDatas();

        /**
         * 显示省市数据
         */
        mCityPikerView = new OptionsPickerView(this);
        mCityPikerView.setPicker((ArrayList) mProvincesStr, mCities, mDistricts, true);
        mCityPikerView.setTitle("选择城市");
        mCityPikerView.setCyclic(false, false, false);
        mCityPikerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String address = mProvinces.get(options1).getName() + " "
                        + mCities.get(options1).get(option2) + " "
                        + mDistricts.get(options1).get(option2).get(options3);

                mTvAddress.setText(address);
            }
        });


    }

    @OnClick(R.id.ll_city_picker)
    public void showCityPickerView(View v) {
        //确认省市数据
        mCityPikerView.show();
//        cityPicker.show();
    }


}
