package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Division

class VoterInfoViewModelFactory(
    private val dataSource: ElectionDao,
    private val electionId: Int,
    private val division: Division
): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            return VoterInfoViewModel(
                dataSource = dataSource,
                electionId = electionId,
                division = division
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}