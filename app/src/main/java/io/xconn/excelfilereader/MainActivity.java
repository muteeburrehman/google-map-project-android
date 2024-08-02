package io.xconn.excelfilereader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.DirectionsApi;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.android.PolyUtil;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private boolean locationPermissionGranted;
    private LatLng currentLocation;
    private GeoApiContext geoApiContext;
    private String selectedFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner fileSpinner = findViewById(R.id.fileSpinner);
        EditText searchInput = findViewById(R.id.searchInput);
        Button searchButton = findViewById(R.id.searchButton);
        Button directionsButton = findViewById(R.id.directionsButton);

        // Get selected file name from the intent
        selectedFileName = getIntent().getStringExtra("selectedFile");

        // Initialize Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize GeoApiContext for Directions API
        geoApiContext = new GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_key))
                .build();

        // Check location permissions
        getLocationPermission();

        searchButton.setOnClickListener(v -> {
            String searchId = searchInput.getText().toString();
            if (!searchId.isEmpty()) {
                LatLng location = getLocationFromExcel(selectedFileName, searchId);
                if (location != null) {
                    mMap.addMarker(new MarkerOptions().position(location).title("Location for ID: " + searchId));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                } else {
                    Toast.makeText(MainActivity.this, "ID not found in the selected file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Please enter an ID to search", Toast.LENGTH_SHORT).show();
            }
        });

        directionsButton.setOnClickListener(v -> {
            String searchId = searchInput.getText().toString();
            if (!searchId.isEmpty()) {
                LatLng destination = getLocationFromExcel(selectedFileName, searchId);
                if (destination != null && currentLocation != null) {
                    calculateRoute(currentLocation, destination);
                } else {
                    Toast.makeText(MainActivity.this, "Unable to get directions", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Please enter an ID to search", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                currentLocation = null;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
    }

    private LatLng getLocationFromExcel(String fileName, String searchId) {
        try (InputStream is = getAssets().open(fileName);
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getCell(2).getStringCellValue().equals(searchId)) {
                    double latitude = row.getCell(0).getNumericCellValue();
                    double longitude = row.getCell(1).getNumericCellValue();
                    return new LatLng(latitude, longitude);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void calculateRoute(LatLng origin, LatLng destination) {
        DirectionsApi.newRequest(geoApiContext)
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .setCallback(new com.google.maps.PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        runOnUiThread(() -> {
                            if (result.routes != null && result.routes.length > 0) {
                                DirectionsRoute route = result.routes[0];
                                List<LatLng> path = new ArrayList<>();
                                for (com.google.maps.model.LatLng latLng : route.overviewPolyline.decodePath()) {
                                    path.add(new LatLng(latLng.lat, latLng.lng));
                                }
                                mMap.addPolyline(new com.google.android.gms.maps.model.PolylineOptions().addAll(path));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to get directions", Toast.LENGTH_SHORT).show());
                    }
                });
    }
}
