package com.example.android.politicalpreparedness.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class NameAdapter {
    @FromJson
    fun fromJson (name: String): String {
//        val date = SimpleDateFormat("yyyy-dd-MM", Locale.US).parse(electionDay)
        return name + " Fezes"
    }

    @ToJson
    fun toJson (name: String): String {
        return name
    }
}