package com.example.stockmarketapp.data.remote

import com.example.stockmarketapp.data.remote.dto.CompanyInfoDto
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {
  companion object{
   const val API_KEY = "G1USXWKX272RK4BP"
   const val BASE_URL = "https://alphavantage.co"
  }


  @GET("query?function=LISTING_STATUS")
  suspend fun getListings(
   @Query("apiKey") apiKey: String = API_KEY,
   ): ResponseBody

  @GET("query?function=TIME_SERIES&interval=60min&datatype=CSV")
  suspend fun getIntradayInfo(
   @Query("symbol") symbol: String,
  @Query("apiKey") apiKey: String = API_KEY
  ): ResponseBody

  @GET("query?function=OVERVIEW")
  suspend fun getCompanyInfo(
   @Query("symbol") symbol: String,
   @Query ("apiKey") apiKey: String = API_KEY,
  ): CompanyInfoDto


}