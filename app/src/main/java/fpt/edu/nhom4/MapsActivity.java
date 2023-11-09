package fpt.edu.nhom4;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fpt.edu.nhom4.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Existing marker in FPT University
        LatLng FPTU = new LatLng(10.013162219620174, 105.73222136386758);
        mMap.addMarker(new MarkerOptions().position(FPTU).title("FPTU Can Tho"));

        LatLng fuQuyNhon = new LatLng(13.804124022705652, 109.21908102243833);
        mMap.addMarker(new MarkerOptions().position(fuQuyNhon).title("FPTU Quy Nhon"));

        LatLng fuHoaLac = new LatLng(21.065672470902758, 105.52512140954303);
        mMap.addMarker(new MarkerOptions().position(fuHoaLac).title("FPTU Hoa Lac"));

        LatLng fuHCM = new LatLng(10.862165182269598, 106.81041577722421);
        mMap.addMarker(new MarkerOptions().position(fuHCM).title("FPTU HCM"));



        // Other map settings
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(FPTU, 20));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        // Permissions and location settings
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Consider calling ActivityCompat#requestPermissions
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
}