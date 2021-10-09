package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.model.Event
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import java.lang.Exception

class RepresentativeViewModel: ViewModel() {
    private val _address = MutableLiveData<Address>(Address("", "", "", "", ""))
    val address: LiveData<Address>
        get() = _address

    private val _representatives = MutableLiveData<ArrayList<Representative>>()
    val representatives: LiveData<ArrayList<Representative>>
        get() = _representatives

    private val _askLocationPermission = MutableLiveData<Event<Array<String>>>()
    val askLocationPermission: LiveData<Event<Array<String>>>
        get() = _askLocationPermission

    private val _getLocation = MutableLiveData<Event<Boolean>>()
    val getLocation: LiveData<Event<Boolean>>
        get() = _getLocation

    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    fun askLocationPermission() {
        this.setAskLocationPermission(permissions)
    }

    private fun setAskLocationPermission(askLocationPermission: Array<String>) {
        _askLocationPermission.value = Event(askLocationPermission)
    }

    fun getLastLocation() {
        this.setGetLocation(true)
    }

    private fun setGetLocation(value: Boolean) {
        _getLocation.value = Event(value)
    }

    fun setLocation(address: Address) {
        setAddress(address)
        loadRepresentatives(address.toFormattedString())
    }

    fun loadRepresentativesFromMyLocation() {
        askLocationPermission()
    }

    fun loadRepresentativesFromTypedAddress() {
        val address = this.address.value?.toFormattedString() ?: ""

        loadRepresentatives(address)
    }

    private fun loadRepresentatives(address: String) {
        viewModelScope.launch {
            try {
                val representativeResponse = CivicsApi.retrofitService.representatives(address)
                val representatives = representativeResponse.offices.flatMap { office -> office.getRepresentatives(representativeResponse.officials) }
                setRepresentatives(representatives as ArrayList<Representative>)
            } catch (e: Exception) {
                Log.d("TAG", e.toString())
            }
        }
    }

    private fun setRepresentatives(value: ArrayList<Representative>) {
        _representatives.value = value
    }

    private fun setAddress(value: Address) {
        _address.value = value
    }
}
