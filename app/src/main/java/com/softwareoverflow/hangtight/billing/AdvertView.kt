package com.softwareoverflow.hangtight.billing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.softwareoverflow.hangtight.R

@Composable
fun AdvertView(modifier: Modifier = Modifier) {
    val isUpgraded by UpgradeManager.userUpgradedFlow.collectAsState()

    if(!isUpgraded) {

        val isInEditMode = LocalInspectionMode.current
        if (isInEditMode) {
            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .padding(horizontal = 2.dp, vertical = 6.dp),
                textAlign = TextAlign.Center,
                text = "Advert Here",
            )
        } else {
            AndroidView(
                modifier = modifier.fillMaxWidth(),
                factory = { context ->
                    AdView(context).apply {
                        adSize = MobileAdsManager.bannerAdSize
                        adUnitId = context.getString(R.string.adUnitId_banner)
                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AdvertPreview() {
    AdvertView()
    Column(Modifier.fillMaxSize()){

    }
}