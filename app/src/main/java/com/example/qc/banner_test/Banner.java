package com.example.qc.banner_test;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import java.util.ArrayList;

/**
 * Created by qianchao on 2018/7/17.
 */
public class Banner extends LinearLayout {
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 图片轮播控件viewpager
     */
    private ViewPager mAdvPager = null;
    /**
     * viewpager适配器
     */
    private ImageAdapter mAdvAdapter;
    /**
     * 图片轮播进度控件
     */
    private ViewGroup mGroup;

    /**
     * ViewPager的显示的imageview
     */
    private ImageView mImageView = null;

    /**
     * 滚动图片进度指示列表
     */
    private ImageView[] dots = null;

    /**
     * 自动循环标志
     */
    private boolean isStop;

    public Banner(Context context) {
        super(context);
        init(context);
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.ad_cycle_view, this);

        mAdvPager = (ViewPager) findViewById(R.id.adv_pager);

        mAdvPager.setOnPageChangeListener(new GuidePageChangeListener());

        // 滚动图片右下指示器
        mGroup = (ViewGroup) findViewById(R.id.circles);

    }


    /**
     * 装填图片数据
     *
     * @param :
     * @param imageCycleViewListener
     */
    public void setImageResources(ArrayList<String> imageUrlList, ImageCycleViewListener imageCycleViewListener) {

        if (imageUrlList != null && imageUrlList.size() > 0) {

            this.setVisibility(View.VISIBLE);
        } else {

            this.setVisibility(View.GONE);
            return;
        }

        // 清除
        mGroup.removeAllViews();
        // 图片广告数量
        final int imageCount = imageUrlList.size();

        dots = new ImageView[imageCount];
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        parms.rightMargin = 20;
        for (int i = 0; i < imageCount; i++) {

            mImageView = new ImageView(mContext);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            dots[i] = mImageView;
            dots[i].setLayoutParams(parms);
            if (i == 0) {
                dots[i].setBackgroundResource(R.drawable.banner_indicator_focus);
            } else {
                dots[i].setBackgroundResource(R.drawable.banner_indicator_normal);
            }
            mGroup.addView(dots[i]);
        }

        mAdvAdapter = new ImageAdapter(mContext, imageUrlList, imageCycleViewListener);
        mAdvPager.setAdapter(mAdvAdapter);
        int diff = Integer.MAX_VALUE / 2 %imageCount;
        mAdvPager.setCurrentItem(Integer.MAX_VALUE / 2 - diff);
        Log.d("---","current="+(Integer.MAX_VALUE / 2 - diff));
//        int middle = mAdvAdapter.getCount() / 2;
//        mAdvPager.setCurrentItem(middle - middle % imageUrlList.size());
    }

    /**
     * 图片轮播(手动控制自动轮播与否，便于资源控件）
     */
    public void startImageCycle() {
        startImageTimerTask();
    }

    /**
     * 暂停轮播—用于节省资源
     */
    public void pauseImageCycle() {
        stopImageTimerTask();
    }

    /**
     * 图片滚动任务
     */
    private void startImageTimerTask() {
        isStop = false;
        // 图片滚动
        mHandler.removeCallbacks(mImageTimerTask);
        mHandler.postDelayed(mImageTimerTask, 3000);
    }

    /**
     * 停止图片滚动任务
     */
    private void stopImageTimerTask() {
        isStop = true;
        mHandler.removeCallbacks(mImageTimerTask);
    }

    private Handler mHandler = new Handler();

    /**
     * 图片自动轮播Task
     */
    private Runnable mImageTimerTask = new Runnable() {
        @Override
        public void run() {
            if (!isStop) {
                mAdvPager.setCurrentItem(mAdvPager.getCurrentItem() + 1);
            }
        }
    };

    /**
     * 轮播图片监听
     *
     * @author minking
     */
    private final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {

            if (state == ViewPager.SCROLL_STATE_IDLE) { //处于停止状态
                startImageTimerTask();
            } else if (state == ViewPager.SCROLL_STATE_DRAGGING) { //用户正在滑动
                stopImageTimerTask();
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

            position = position % dots.length;
            // 设置图片滚动指示器背景
            dots[position].setBackgroundResource(R.drawable.banner_indicator_focus);
            for (int i = 0; i < dots.length; i++) {
                if (position != i) {
                    dots[i].setBackgroundResource(R.drawable.banner_indicator_normal);
                }
            }
        }
    }

    private class ImageAdapter extends PagerAdapter {

        /**
         * 图片视图缓存列表
         */
        private ArrayList<ImageView> mImageViewCacheList;

        /**
         * 图片资源列表URL
         */
        private ArrayList<String> mAdURL;

        /**
         * 广告图片点击监听
         */
        private ImageCycleViewListener mImageCycleViewListener;

        private Context mContext;

        public ImageAdapter(Context context, ArrayList<String> mAdURL, ImageCycleViewListener imageCycleViewListener) {

            this.mContext = context;
            this.mAdURL = mAdURL;
            mImageCycleViewListener = imageCycleViewListener;
            mImageViewCacheList = new ArrayList<ImageView>();
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            String imageUrl = mAdURL.get(position % mAdURL.size());
            ImageView imageView;
            if (mImageViewCacheList.isEmpty()) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                imageView = mImageViewCacheList.remove(0);
            }
            // 设置图片点击监听
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImageCycleViewListener.onImageClick(position % mAdURL.size(), v);
                }
            });
            imageView.setTag(imageUrl);
            container.addView(imageView);
            mImageCycleViewListener.displayImage(imageUrl, imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            mAdvPager.removeView(view);
            mImageViewCacheList.add(view);
        }

    }

    /**
     * 轮播控件的监听事件
     *
     * @author minking
     */
    public interface ImageCycleViewListener {
        /**
         * 加载图片资源
         *
         * @param imageURL
         * @param imageView
         */
        void displayImage(String imageURL, ImageView imageView);

        /**
         * 单击图片事件
         *
         * @param position
         * @param imageView
         */
        void onImageClick(int position, View imageView);
    }
}