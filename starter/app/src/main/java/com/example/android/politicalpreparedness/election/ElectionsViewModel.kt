package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.model.Event
import com.example.android.politicalpreparedness.model.GoToScreenEvent
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import java.lang.Exception

class ElectionsViewModel(
    private val dataSource: ElectionDao
): ViewModel() {
    companion object {
        private const val TAG = "ElectionsViewModel"
    }

    private val _upcomingElections = MutableLiveData<ArrayList<Election>>()
    val upcomingElections: LiveData<ArrayList<Election>>
        get() = _upcomingElections

    private val _savedElections = MutableLiveData<ArrayList<Election>>()
    val savedElections: LiveData<ArrayList<Election>>
        get() = _savedElections

    private val _goToScreen = MutableLiveData<Event<GoToScreenEvent>>()
    val goToScreen: LiveData<Event<GoToScreenEvent>>
        get() = _goToScreen

    fun init() {
        getUpcomingElections()
        getSavedElections()
    }

    private fun getUpcomingElections() {
        viewModelScope.launch {
            try {
                val electionResponse = CivicsApi.retrofitService.elections()
                setUpcomingElections(electionResponse.elections as ArrayList<Election>)
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
            }
        }
    }

    private fun getSavedElections() {
        viewModelScope.launch {
            val savedElections = dataSource.list()
            setSavedElections(savedElections as ArrayList<Election>)
        }
    }

    fun goToElection(election: Election) {
        val bundle = Bundle()
        bundle.putInt("id", election.id)
        bundle.putParcelable("division", election.division)
        val goToScreenEvent = GoToScreenEvent("VoterInfoFragment", bundle)

        setGoToScreen(goToScreenEvent)
    }

    fun setUpcomingElections(value: ArrayList <Election>) {
        _upcomingElections.value = value
    }

    fun setSavedElections(value: ArrayList <Election>) {
        _savedElections.value = value
    }

    fun setGoToScreen(value: GoToScreenEvent) {
        _goToScreen.value = Event(value)
    }
}