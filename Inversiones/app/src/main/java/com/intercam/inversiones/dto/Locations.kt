package com.intercam.inversiones.dto

import kotlinx.serialization.*

@Serializable
data class Locations(

    val id: String,
    val atm: String,
    val latitud: String,
    val longitud: String

)
