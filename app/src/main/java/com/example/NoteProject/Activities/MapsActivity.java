package com.example.NoteProject.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.NoteProject.Database.DBHelper;
import com.example.NoteProject.Database.DBLocation;
import com.example.NoteProject.Database.DBMap;
import com.example.NoteProject.Modals.Map;
import com.example.NoteProject.Modals.Note;
import com.example.NoteProject.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    boolean mLocationPermissionGranted = false;
    List<Marker> markersList = new ArrayList<>();
    public ArrayList<Map> savedNoteArrayList = new ArrayList<>();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int CONNECTION_RESOLUTION_REQUEST = 2;
    private GoogleApiClient googleApiClient;
    private Location mLastKnownLocation;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Location userLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    boolean isEdit = false;
    String completeAddress = "";
    Geocoder geocoder;
    List<Address> addresses;
    Note note = new Note();
    String noteId ="";

    Context context = this;
    DBMap dbMap = new DBMap(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            checkUserLocationPermission();
//        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        isEdit = getIntent().getExtras().getBoolean("isEdit");
        Log.d("test654","testNote" +isEdit);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);*/

       LatLng objLatLng = getIntent().getExtras().getParcelable("Latlng");

        Log.d("test654","testNote" +isEdit);
       if(isEdit == true)
       {
           noteId = getIntent().getExtras().getString("noteID");
           Log.d("test654","testNote" +noteId);
       }

        if (objLatLng != null) {
            LatLng temp = new LatLng(objLatLng.latitude, objLatLng.longitude);
            mMap.addMarker(new MarkerOptions().position(temp)
                    .title(getAddressTitle(temp)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 15));

        }

        if(isEdit == true)
        {

          savedNoteArrayList =   dbMap.getAllLocations(this,noteId);
            Log.d("value ", "test AAA" + savedNoteArrayList.size());
         for(int i =0 ;i< savedNoteArrayList.size();i++){
              LatLng temp = new LatLng(Double.parseDouble(savedNoteArrayList.get(i).getLat()), Double.parseDouble(savedNoteArrayList.get(i).getLng()));
              mMap.addMarker(new MarkerOptions().position(temp)
                      .title(getAddressTitle(temp)));
              mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 15));
          }
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isEdit == true) {
                    drawMarker(latLng);
                    Log.d("value ", "test size" + markersList.size());
                    if (markersList.size() != 0) {
                        ContentValues contentValues = new ContentValues();
                        // get  & set with contentvalues

                        contentValues.put(DBMap.LATITUDE, latLng.latitude);
                        contentValues.put(DBMap.LONGITUDE, latLng.longitude);
                        contentValues.put(DBMap.NOTE_ID, noteId);

                        dbMap.insertMap(contentValues);
                        Log.d("value ", "test map" + contentValues.get(DBMap.LONGITUDE));
                    /*if (row > 0) {
                        Toast.makeText(context,"Sucessfully",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(context, "Something Wrong...", Toast.LENGTH_SHORT).show();
                }*/

                    }
                }
            }
        });

    }
    private void drawMarker(LatLng point){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
      Marker marker =   mMap.addMarker(markerOptions);
        markersList.add(marker);
    }

    public String getAddressTitle(LatLng location) {
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            completeAddress = completeAddress + addresses.get(0).getAddressLine(0) + " " + addresses.get(0).getLocality() + " " + addresses.get(0).getAdminArea() + " " + addresses.get(0).getCountryName();

        } catch (IOException e) {

        }
        return completeAddress;

    }
}


