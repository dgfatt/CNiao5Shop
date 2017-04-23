package com.cniao5.cniao5shop.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.bean.Campaign;
import com.cniao5.cniao5shop.bean.HomeCampaign;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 主页商品适配器
 */
public class HomeCampaignAdapter extends RecyclerView.Adapter<HomeCampaignAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<HomeCampaign> mDatas;
    private Context context;

    private OnCampaignClickListener campaignClickListener;

    private static int VIEW_TYPE_L = 0;
    private static int VIEW_TYPE_R = 1;

    public HomeCampaignAdapter(List<HomeCampaign> datas, Context context) {
        this.mDatas = datas;
        this.context = context;
    }

    public void setOnCampaignClickListener(OnCampaignClickListener campaignClickListener) {
        this.campaignClickListener = campaignClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mInflater = LayoutInflater.from(parent.getContext());

        //根据类型切换布局
        if (viewType == VIEW_TYPE_R) {

            return new ViewHolder(mInflater.inflate(R.layout.template_home_cardview2, parent, false));

        }
        return new ViewHolder(mInflater.inflate(R.layout.template_home_cardview, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        HomeCampaign campaign = mDatas.get(position);

        viewHolder.textTitle.setText(campaign.getTitle());

        Picasso.with(context).load(campaign.getCpOne().getImgUrl()).into(viewHolder.imageViewBig);
        Picasso.with(context).load(campaign.getCpTwo().getImgUrl()).into(viewHolder.imageViewSmallTop);
        Picasso.with(context).load(campaign.getCpThree().getImgUrl()).into(viewHolder.imageViewSmallBottom);

    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0)
            return VIEW_TYPE_R;
        else
            return VIEW_TYPE_L;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textTitle;
        ImageView imageViewBig;
        ImageView imageViewSmallTop;
        ImageView imageViewSmallBottom;

        public ViewHolder(View itemView) {
            super(itemView);

            textTitle = (TextView) itemView.findViewById(R.id.text_title);
            imageViewBig = (ImageView) itemView.findViewById(R.id.imgview_big);
            imageViewSmallTop = (ImageView) itemView.findViewById(R.id.imgview_small_top);
            imageViewSmallBottom = (ImageView) itemView.findViewById(R.id.imgview_small_bottom);

            imageViewBig.setOnClickListener(this);
            imageViewSmallTop.setOnClickListener(this);
            imageViewSmallBottom.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (campaignClickListener != null) {
                anim(v);
            }
        }

        /**
         * 图片翻转效果
         * @param v
         */
        private void anim(final View v) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotationX", 0.0F, 360F)
                    .setDuration(200);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    HomeCampaign campaign = mDatas.get(getLayoutPosition());

                    switch (v.getId()) {
                        case R.id.imgview_big:
                            campaignClickListener.onClick(v, campaign.getCpOne());
                            break;
                        case R.id.imgview_small_top:
                            campaignClickListener.onClick(v, campaign.getCpTwo());
                            break;
                        case R.id.imgview_small_bottom:
                            campaignClickListener.onClick(v, campaign.getCpThree());
                            break;
                    }
                }

            });

            animator.start();
        }
    }

    public interface OnCampaignClickListener {

        void onClick(View view, Campaign campaign);
    }
}
