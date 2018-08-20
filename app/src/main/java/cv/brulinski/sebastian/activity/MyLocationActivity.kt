package cv.brulinski.sebastian.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import cv.brulinski.sebastian.R
import cv.brulinski.sebastian.utils.string
import kotlinx.android.synthetic.main.my_toolbar.*

/**
 * Activity used to display my location. Place where I live
 */
class MyLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val LAT = "lat"
        const val LNG = "lng"
    }

    //Marker latitude and longitude
    private var latLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_location)
        //Setup Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = R.string.home.string()
        }
        //Get marker latitude and longitude
        intent.extras?.apply {
            latLng = LatLng(getDouble(LAT), getDouble(LNG))
        }
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    //Menu item click's
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> false
        }
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.also {
            latLng?.also { latLng ->
                googleMap.addMarker(MarkerOptions().position(latLng).title(R.string.home.string()))
                val cameraPosition = CameraPosition.Builder()
                        .target(latLng)
                        .zoom(7f)
                        .build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }
    }
}