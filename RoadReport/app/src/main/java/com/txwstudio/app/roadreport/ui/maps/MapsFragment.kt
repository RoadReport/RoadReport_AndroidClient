package com.txwstudio.app.roadreport.ui.maps

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.txwstudio.app.roadreport.R
import com.txwstudio.app.roadreport.Util
import kotlinx.android.synthetic.main.fragment_maps.*


class MapsFragment(
    private val isSelectMode: Boolean,
    private val latLngToShow: LatLng = LatLng(0.0, 0.0)
) :
    BottomSheetDialogFragment() {

    companion object {
        private val TAG = MapsFragment::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    // Shared view model between EventEditorFragment and MapsFragment.
    private val addGeoPointViewModel: AddGeoPointViewModel by activityViewModels()

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // A default location (Yushan, Taiwan) to use when location permission is not granted.
    private var locationPermissionGranted = false
    private val defaultLocation = LatLng(23.470024, 120.957339)
    private val defaultLocation24 by lazy {
        LatLng(22.747690, 120.700548)
    }
    private val defaultLocation182 by lazy {
        LatLng(22.936062, 120.421799)
    }

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null
    private var likelyPlaceNames: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAddresses: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAttributions: Array<List<*>?> = arrayOfNulls(0)
    private var likelyPlaceLatLngs: Array<LatLng?> = arrayOfNulls(0)

    private var userSelectLocation: LatLng = LatLng(0.0, 0.0)

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation(false)

        if (isSelectMode) {
            Util().toast(
                requireActivity(),
                requireActivity().getString(R.string.mapsFrag_howToSelect)
            )
            map?.setOnMapLongClickListener {
                Util().toast(requireActivity(), "${it.latitude} ${it.longitude}")
                userSelectLocation = it
                val newPick = LatLng(it.latitude, it.longitude)
                map?.clear()
                map?.addMarker(
                    MarkerOptions()
                        .position(newPick)
                        .title(requireActivity().getString(R.string.mapsFrag_justSelect))
                )
                map?.animateCamera(CameraUpdateFactory.newLatLng(newPick))
            }
        } else {
            setAndMoveToEventLocation(latLngToShow)
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar()

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        subscribeUi()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // Set state to expanded at start.
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    private fun setupToolBar() {
        // MapsFragment title.
        toolbar_mapsFrag.title = if (isSelectMode) {
            getString(R.string.mapsFrag_titleSelectLocation)
        } else {
            getString(R.string.mapsFrag_titleShowLocation)
        }
    }

    private fun subscribeUi() {
        // Confirm button behavior.
        button_mapsFrag_confirm.setOnClickListener {
            if (isSelectMode && userSelectLocation == LatLng(0.0, 0.0)) {
                Util().toast(
                    requireActivity(),
                    getString(R.string.mapsFrag_notSelect)
                )
            } else if (isSelectMode && userSelectLocation != LatLng(0.0, 0.0)) {
                addGeoPointViewModel.setLatLng(userSelectLocation)
                dismiss()
            } else {
                dismiss()
            }
        }

        // To my location button
        fab_mapFrag_toMyLocation.setOnClickListener {
            getDeviceLocation(true)
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    // [START maps_current_place_on_request_permissions_result]
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
//        Comment out, it can cause get permission loop.
        updateLocationUI()
    }
    // [END maps_current_place_on_request_permissions_result]


    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }
    // [END maps_current_place_location_permission]


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    // [START maps_current_place_update_location_ui]
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    // [END maps_current_place_update_location_ui]


    /**
     * Get the best and most recent location of the device, and positions the map's camera.
     * May be null in rare cases when a location is not available.
     */
    // [START maps_current_place_get_device_location]
    private fun getDeviceLocation(byClick: Boolean) {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null && isSelectMode || byClick) {
                            map?.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                defaultLocation,
                                DEFAULT_ZOOM.toFloat()
                            )
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    // [END maps_current_place_get_device_location]


    /**
     * Set the event location, and positions the map's camera.
     */
    private fun setAndMoveToEventLocation(eventLatLng: LatLng) {
        val eventLocation: LatLng =
            if (eventLatLng != LatLng(0.0, 0.0))
                eventLatLng
            else
                defaultLocation

        map?.addMarker(
            MarkerOptions().position(eventLocation)
                .title(requireActivity().getString(R.string.mapsFrag_titleShowLocation))
        )

        map?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                eventLocation,
                DEFAULT_ZOOM.toFloat()
            )
        )
    }


    private fun showDistanceBetween() {

    }
}