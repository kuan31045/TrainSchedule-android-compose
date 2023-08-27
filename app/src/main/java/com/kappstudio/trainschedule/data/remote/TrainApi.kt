package com.kappstudio.trainschedule.data.remote

import com.kappstudio.trainschedule.BuildConfig
import com.kappstudio.trainschedule.data.remote.dto.FareResponse
import com.kappstudio.trainschedule.data.remote.dto.LineResponse
import com.kappstudio.trainschedule.data.remote.dto.StationLiveBoardDto
import com.kappstudio.trainschedule.data.remote.dto.StationLiveBoardResponse
import com.kappstudio.trainschedule.data.remote.dto.StationResponse
import com.kappstudio.trainschedule.data.remote.dto.TimeTableResponse
import com.kappstudio.trainschedule.data.remote.dto.TokenDto
import com.kappstudio.trainschedule.data.remote.dto.TrainLiveBoardResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface TrainApi {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("auth/realms/TDXConnect/protocol/openid-connect/token")
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String = GRANT_TYPE,
        @Field("client_id") clientId: String = CLIENT_ID,
        @Field("client_secret") clientSecret: String = CLIENT_SECRET,
    ): TokenDto

    @GET(API_RAIL + "Station")
    suspend fun getStations(@Header("authorization") token: String): StationResponse

    @GET(API_RAIL + "DailyTrainTimetable/OD/Inclusive/{OriginStationID}/to/{DestinationStationID}/{TrainDate}")
    suspend fun getTrainTimetable(
        @Header("authorization") token: String,
        @Path("OriginStationID") departureStationId: String,
        @Path("DestinationStationID") arrivalStationId: String,
        @Path("TrainDate") date: String,
    ): TimeTableResponse

    @GET(API_RAIL + "ODFare/{OriginStationID}/to/{DestinationStationID}")
    suspend fun getODFare(
        @Header("authorization") token: String,
        @Path("OriginStationID") departureStationId: String,
        @Path("DestinationStationID") arrivalStationId: String,
    ): FareResponse

    @GET(API_RAIL + "TrainLiveBoard/TrainNo/{TrainNo}")
    suspend fun getTrainLiveBoard(
        @Header("authorization") token: String,
        @Path("TrainNo") trainNumber: String,
    ): TrainLiveBoardResponse


    @GET(API_RAIL + "GeneralTrainTimetable/TrainNo/{TrainNo}")
    suspend fun getGeneralTrainTimetable(
        @Header("authorization") token: String,
        @Path("TrainNo") trainNumber: String,
    ): TimeTableResponse

    @GET(API_RAIL + "DailyTrainTimetable/Today")
    suspend fun getTodayTrainTimetable(
        @Header("authorization") token: String,
    ): TimeTableResponse


    @GET(API_RAIL + "StationOfLine")
    suspend fun getLines(@Header("authorization") token: String): LineResponse

    @GET(API_RAIL + "StationLiveBoard")
    suspend fun getStationLiveBoard(
        @Header("authorization") token: String,
    ): StationLiveBoardResponse

    companion object {
        const val API_RAIL = "api/basic/v3/Rail/TRA/"
        const val BASE_URL = "https://tdx.transportdata.tw"
        const val GRANT_TYPE = "client_credentials"
        const val CLIENT_ID = BuildConfig.CLIENT_ID
        const val CLIENT_SECRET = BuildConfig.CLIENT_SECRET
    }
}