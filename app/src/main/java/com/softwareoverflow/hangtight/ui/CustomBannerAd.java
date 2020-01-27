package com.softwareoverflow.hangtight.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.softwareoverflow.hangtight.R;
import com.softwareoverflow.hangtight.helper.MobileAdsHelper;
import com.softwareoverflow.hangtight.helper.UpgradeManager;

public class CustomBannerAd extends FrameLayout {

    private AdView bannerAd;
    private ImageButton closeButton;

    public CustomBannerAd(Context context) {
        super(context);
        initView(context);
    }

    public CustomBannerAd(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomBannerAd(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        if(MobileAdsHelper.userHasUpgraded){
            this.setVisibility(GONE);
            return;
        }

        View view = inflate(context.getApplicationContext(), R.layout.partial_banner_ad, null);
        addView(view);

        closeButton = view.findViewById(R.id.admob_banner_close);
        closeButton.setOnClickListener((v) -> UpgradeManager.upgrade(context, v));

        bannerAd = view.findViewById(R.id.admob_banner);
        bannerAd.loadAd(new AdRequest.Builder().build());
        bannerAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                closeButton.setVisibility(VISIBLE);
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        if(closeButton != null){
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) closeButton.getLayoutParams();
            params.height = h / 3;
            params.width = h / 3;

            closeButton.setLayoutParams(params);
        }
    }

    public void hide(){
        this.setVisibility(GONE);

        if(bannerAd != null){
            bannerAd.destroy();
        }
    }
}
