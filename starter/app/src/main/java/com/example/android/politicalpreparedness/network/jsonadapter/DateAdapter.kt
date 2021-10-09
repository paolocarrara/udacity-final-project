package com.example.android.politicalpreparedness.network.jsonadapter

import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class DateAdapter {
    @FromJson
    fun electionDayFromJson (electionDay: String): String {
//        val date = SimpleDateFormat("yyyy-dd-MM", Locale.US).parse(electionDay)
        return electionDay
    }

    @ToJson
    fun electionDayToJson (electionDay: String): String {
        return electionDay
    }
}