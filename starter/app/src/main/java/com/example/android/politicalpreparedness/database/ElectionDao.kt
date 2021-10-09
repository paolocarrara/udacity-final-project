package com.example.android.politicalpreparedness.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {
    @Insert
    suspend fun insertElection(election: Election): Long

    @Query("SELECT * FROM election_table")
    suspend fun list(): List<Election>

    @Query("SELECT * FROM election_table WHERE ID = :id")
    suspend fun get(id: Int): Election?

    @Query("DELETE FROM election_table WHERE ID = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM election_table")
    suspend fun clear()

}