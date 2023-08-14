package com.kappstudio.trainschedule.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.jar.Attributes.Name

data class FareResponse(
    @SerializedName("ODFares") val odFares: List<ODFareDto>,
    @SerializedName("UpdateTime") val updateTime: String
)

data class ODFareDto(
    @SerializedName("OriginStationID") val originStationId: String,
    @SerializedName("OriginStationName") val originStationName: Name,
    @SerializedName("DestinationStationID") val destinationStationId: String,
    @SerializedName("DestinationStationName") val destinationStationName: Name,
    @SerializedName("Fares") val fares: List<FareDto>,
    @SerializedName("Direction") val direction: Int,
    @SerializedName("TrainType") val trainType: Int,
)

data class FareDto(
    @SerializedName("CabinClass") val cabinClass: Int,
    @SerializedName("FareClass") val fareClass: Int,
    @SerializedName("Price") val price: Int,
    @SerializedName("TicketType") val ticketType: Int
)