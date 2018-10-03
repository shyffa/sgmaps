package com.example.android.mapsshyffa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{


    private GoogleMap mMap;
    @BindView(R.id.input_search)
    AutoCompleteTextView mSearchText;
    @BindView(R.id.tv_origin)
    TextView tvOrigin;
    @BindView(R.id.tv_destination)
    TextView tvDestination;
    @BindView(R.id.tv_duration)
    TextView TvDuration;
    @BindView(R.id.tv_origin_desc)
    TextView tvOriginDesc;
    @BindView(R.id.tv_destination_desc)
    TextView getTvDestinationDesc;
    @BindView(R.id.iv_transportDriving)
    ImageView ivDriving;
    @BindView(R.id.iv_transportTransit)
    ImageView ivTransit;
    @BindView(R.id.iv_transportCycling)
    ImageView ivCycling;
    @BindView(R.id.iv_transportWalking)
    ImageView ivWalking;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottomSheet;

    private GoogleApiClient mGoogleApiClient;
    Geocoder geocoder;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    final static float default_zoom = 15f;

    Boolean permission = false;
    Boolean gpsUsable = false;
    FusedLocationProviderClient mFusedLocationClient;
    LatLng origin, destination;

    private BottomSheetBehavior bsb;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        ButterKnife.bind(this);

        requestPermissions();
        init();

        mapFragment.getMapAsync(this);

        bsb = BottomSheetBehavior.from(bottomSheet);
        bsb.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void init() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)
                .build();
        geocoder = new Geocoder(MapsActivity.this);

        PlaceAutoCompleteAdapter mPlaceAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, LAT_LNG_BOUNDS, null);
        mSearchText.setAdapter(mPlaceAutoCompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textview, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geocoder(null);
                }
                return false;
            }
        });
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED;
            if (!permission) {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},1);
            }
        } else {
            permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                permission = (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                featureLocationMap();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("MissingPermission")
    private void featureLocationMap() {
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (permission)
            featureLocationMap();

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
