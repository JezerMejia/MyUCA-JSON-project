package com.example.myuca_android

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

data class Coordinador(
    @SerializedName("idC") var id: Int,
    var nombres: String,
    var apellidos: String,
    @SerializedName("fechaNac") var fechaNacimiento: LocalDate,
    var titulo: String,
    var email: String,
    var facultad: String,
) {
    fun getFullName(): String {
        return "$nombres $apellidos".trim()
    }

    fun getAge(): Int {
        val now = LocalDate.now()
        return Period.between(fechaNacimiento, now).years
    }

    override fun toString(): String {
        return "{ id: $id, nombres: $nombres, apellidos: $apellidos, fechaNac: $fechaNacimiento }"
    }
}

data class CoordinadorResponse(
    val data: List<Coordinador>
)

class LocalDateSerializer : JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate {
        val ldtString: String = json?.asJsonPrimitive?.asString ?: ""
        return LocalDate.parse(ldtString, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    override fun serialize(
        src: LocalDate?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }
}