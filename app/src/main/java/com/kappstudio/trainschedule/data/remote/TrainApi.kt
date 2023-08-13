package com.kappstudio.trainschedule.data.remote

import com.kappstudio.trainschedule.BuildConfig
import com.kappstudio.trainschedule.data.remote.dto.StationResponse
import com.kappstudio.trainschedule.data.remote.dto.TimeTableResponse
import com.kappstudio.trainschedule.data.remote.dto.TokenDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface TrainApi {

    @GET(API_RAIL + "DailyTrainTimetable/OD/{OriginStationID}/to/{DestinationStationID}/{TrainDate}")
    suspend fun getTrainTimetable(
        @Header("authorization") token: String,
        @Path("OriginStationID") departureStationId: String,
        @Path("DestinationStationID") arrivalStationId: String,
        @Path("TrainDate") date: String
    ): TimeTableResponse

    @GET(API_RAIL + "Station")
    suspend fun getStations(@Header("authorization") token: String): StationResponse

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("auth/realms/TDXConnect/protocol/openid-connect/token")
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String = GRANT_TYPE,
        @Field("client_id") clientId: String = CLIENT_ID,
        @Field("client_secret") clientSecret: String = CLIENT_SECRET
    ): TokenDto

    companion object {
        const val API_RAIL = "api/basic/v3/Rail/TRA/"
        const val BASE_URL = "https://tdx.transportdata.tw"
        const val GRANT_TYPE = "client_credentials"
        const val CLIENT_ID = BuildConfig.CLIENT_ID
        const val CLIENT_SECRET = BuildConfig.CLIENT_SECRET
    }
}