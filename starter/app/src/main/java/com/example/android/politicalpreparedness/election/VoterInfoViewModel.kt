package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch
import java.lang.Exception

import com.example.android.politicalpreparedness.model.Event


class VoterInfoViewModel(
    private val dataSource: ElectionDao,
    private val electionId: Int,
    private val division: Division
) : ViewModel() {

    companion object {
        private const val TAG = "VoterInfoViewModel"
    }

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _goToUrl = MutableLiveData<Event<String>>()
    val goToUrl: LiveData<Event<String>>
        get() = _goToUrl

    private val _isItBeingFollowed = MutableLiveData<Boolean>(false)
    val isItBeingFollowed: LiveData<Boolean>
        get() = _isItBeingFollowed

    init {
        getVoterInfo()
        verifyIfItIsBeingFollowed()
    }

    private fun getVoterInfo() {
        viewModelScope.launch {
            try {
                val voterInfoResponse = CivicsApi.retrofitService.voterInfo(
                    address = division.state + ", " + division.country,
                    electionId = electionId
                )

                setVoterInfo(voterInfoResponse)
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
            }
        }
    }

    private fun verifyIfItIsBeingFollowed() {
        viewModelScope.launch {
            try {
                val voterInfoResponse = dataSource.get(
                    id = electionId
                )

                voterInfoResponse?.let {
                    setIsItBeingFollowed(true);
                }

                Log.d(TAG, voterInfoResponse.toString())
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
            }
        }
    }

    private fun setVoterInfo(value: VoterInfoResponse) {
        _voterInfo.value = value
    }

    fun openVotingLocationsUrl() {
        openUrl("https://myvote.wi.gov/en-us/")
    }

    fun openBallotInformationUrl() {
        openUrl("https://myvote.wi.gov/en-us/")
    }

    fun openUrl(url: String) {
        setGoToUrl(url)
    }

    private fun setGoToUrl(value: String) {
        _goToUrl.value = Event(value)
    }

    private fun setIsItBeingFollowed(value: Boolean) {
        _isItBeingFollowed.value = value
    }

    fun follow() {
        voterInfo.value?.let {
            viewModelScope.launch {
                dataSource.insertElection(it.election)
            }
        }
    }

    fun unfollow() {
        voterInfo.value?.let {
            viewModelScope.launch {
                dataSource.delete(it.election.id)
            }
        }
    }
}