package com.intercam.inversiones.data.database.model

import com.google.gson.annotations.SerializedName

data class EnrolamientoModel(
    //@SerializedName("id") val id: Int,
    @SerializedName("enrolado") val enrolado: String,
    @SerializedName("username") val username: String
   // @SerializedName("idUsr") val idUsr: String
)

