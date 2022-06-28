package com.example.stockmarketapp.data.repository

import com.example.stockmarketapp.data.csv.CompanyListingParser
import com.example.stockmarketapp.data.csv.CsvParser
import com.example.stockmarketapp.data.local.StockDatabase
import com.example.stockmarketapp.data.mapper.toCompanyInfo
import com.example.stockmarketapp.data.mapper.toCompanyListing
import com.example.stockmarketapp.data.mapper.toCompanyListingEntity
import com.example.stockmarketapp.data.remote.StockApi
import com.example.stockmarketapp.domain.model.CompanyInfo
import com.example.stockmarketapp.domain.model.CompanyListing
import com.example.stockmarketapp.domain.model.IntradayInfo
import com.example.stockmarketapp.domain.repository.StockRepository
import com.example.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class StockRepositoryImpl @Inject constructor(
      val api:StockApi,
      val db: StockDatabase,
    private val companyListingParser: CsvParser<CompanyListing>,
    private val intradayInfoParser: CsvParser<IntradayInfo>
): StockRepository{

    private val dao = db.dao
    override suspend fun getCompanylistings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        TODO("Not yet implemented")
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

            if (shouldJustLoadFromCache){
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListings = try {
                val response = api.getListings()
                companyListingParser.parse(response.byteStream())
            }catch (e: IOException){
                e.printStackTrace()
                emit(Resource.Error("couldn't load data"))
                null
            }catch (e: HttpException){
                e.printStackTrace()
                emit(Resource.Error("couldn't load data"))
               null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListing()
                dao.insertCompanyListing(
                    listings.map {it.toCompanyListingEntity()}
                )
                emit(Resource.Success(
                    data = dao.searchCompanyListing(query = "")
                        .map { it.toCompanyListing() }
                ))

                emit(Resource.Loading(false))
            }

        }
    }


    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol)
            val results = intradayInfoParser.parse(response.byteStream())
            Resource.Success(data = results)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Error(
                message = "can't load intraday info"
            )
        }catch (e: HttpException){
            e.printStackTrace()
            Resource.Error(message = "can't load intraday info")
        }
    }


    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {

        return try {
            val result = api.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfo())
        }catch (e: IOException){
            e.printStackTrace()
            Resource.Error(message = "can't load intraday info")
        }catch (e: HttpException){
            e.printStackTrace()
            Resource.Error(
                message = "can't load intraday info"
            )
        }
    }
}