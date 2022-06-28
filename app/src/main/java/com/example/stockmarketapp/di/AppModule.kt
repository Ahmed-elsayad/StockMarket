package com.example.stockmarketapp.di

import androidx.room.Room
import com.example.stockmarketapp.StockApplication
import com.example.stockmarketapp.data.local.StockDatabase
import com.example.stockmarketapp.data.remote.StockApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesStockApi(): StockApi{
        return Retrofit.Builder()
            .baseUrl(StockApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient()
                .newBuilder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }).build())
            .build().create(StockApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStockDatabase (app: StockApplication): StockDatabase{
        return Room.databaseBuilder(
            app,
            StockDatabase::class.java,
            "stock.db"
        ).build()
    }
}