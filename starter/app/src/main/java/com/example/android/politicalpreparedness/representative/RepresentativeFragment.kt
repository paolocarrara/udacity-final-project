package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import java.util.*

class DetailFragment : Fragment(), OnSuccessListener<Location> {

    companion object {
        private const val TAG = "VoterInfoFragment"
        const val ASK_PERMISSION_REQUEST_CODE = 0x01
    }

    private lateinit var viewModel: RepresentativeViewModel
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_representative,
            container,
            false
        )
        binding.lifecycleOwner = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setViewModels()
    }

    private fun setViewModels() {
        viewModel = ViewModelProvider(this).get(RepresentativeViewModel::class.java)
        binding.viewModel = viewModel

        binding.representativesRecyclerView.adapter = RepresentativeListAdapter()

        viewModel.representatives.observe(viewLifecycleOwner) { list ->
            val adapter = binding.representativesRecyclerView.adapter as RepresentativeListAdapter
            adapter.submitList(list)
        }

        viewModel.askLocationPermission.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                askLocationPermission(it)
            }
        }

        viewModel.getLocation.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                getLocation()
            }
        }
    }

    private fun askLocationPermission(permissions: Array<String>) {
        requestPermissions(permissions, ASK_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            ASK_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.getLastLocation()
                }
            }
            else -> {
                Log.d(TAG, requestCode.toString())
                Log.d(TAG, grantResults.toString())
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return isPermissionGranted()
    }

    private fun isPermissionGranted() : Boolean {
        return (
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkLocationPermissions()) {
            fusedLocationClient.lastLocation.addOnSuccessListener(this)
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onSuccess(location: Location?) {
        location?.let {
            val address = geoCodeLocation(location)

            viewModel.setLocation(
                address = address
            )
        } ?: run {}
    }

}