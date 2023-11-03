package com.kappstudio.trainschedule.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.kappstudio.trainschedule.BuildConfig

@Composable
fun BannerAd(modifier: Modifier = Modifier, adSize: AdSize = AdSize.BANNER) {
    Column(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(adSize)
                    adUnitId = BuildConfig.AD_UNIT_ID_BANNER

                    // Request an Ad
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}