package com.softwareoverflow.hangtight.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.softwareoverflow.hangtight.R;
import com.softwareoverflow.hangtight.helper.MobileAdsHelper;

public class CustomBannerAd extends FrameLayout {

    private AdView bannerAd;

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

    private void initView(Context context) {
        if (MobileAdsHelper.userHasUpgraded) {
            this.setVisibility(GONE);
            return;
        }

        View view = inflate(context.getApplicationContext(), R.layout.partial_banner_ad, null);
        addView(view);

        bannerAd = view.findViewById(R.id.admob_banner);
        bannerAd.loadAd(new AdRequest.Builder().build());
    }

    public void hide() {
        this.setVisibility(GONE);

        if (bannerAd != null) {
            bannerAd.destroy();
        }
    }
}
