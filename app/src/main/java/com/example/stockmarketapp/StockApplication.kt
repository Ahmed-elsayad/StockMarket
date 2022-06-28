package com.example.stockmarketapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class StockApplication  @Inject constructor(): Application() {
}