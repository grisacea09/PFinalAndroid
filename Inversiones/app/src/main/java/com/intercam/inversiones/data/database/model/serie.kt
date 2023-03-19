package com.intercam.inversiones.data.database.model

import kotlinx.serialization.*


@Serializable
data class Serie (
val base: String,

@SerialName("end_date")
val end_date: String,

val rates: Map<String, Rate>,

@SerialName("start_date")
val start_date: String,

val success: Boolean,
val timeseries: Boolean
)

@Serializable
data class Rate (
    @SerialName("GBP")
    val GBP: Double,
    @SerialName("BRL")
    val BRL: Double,
    @SerialName("CAD")
    val CAD:  Double,
    @SerialName("USD")
    val USD:  Double,
    @SerialName("EGP")
    val EGP:  Double,
    @SerialName("EUR")
    val EUR:  Double,
    @SerialName("INR")
    val INR:  Double,
    @SerialName("KPW")
    val KPW:  Double,
    @SerialName("KRW")
    val KRW:  Double,
    @SerialName("COP")
    val COP:  Double,
    @SerialName("CUP")
    val CUP:  Double,
    @SerialName("SEK")
    val SEK:  Double,
    @SerialName("RUB")
    val RUB:  Double,
    @SerialName("CNY")
    val CNY:  Double

)

