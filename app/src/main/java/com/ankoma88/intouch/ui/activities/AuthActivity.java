package com.ankoma88.intouch.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ankoma88.intouch.R;
import com.ankoma88.intouch.cloud.CloudHelper;
import com.ankoma88.intouch.interfaces.AuthListener;
import com.ankoma88.intouch.models.User;
import com.ankoma88.intouch.ui.fragments.LoginFragment;
import com.ankoma88.intouch.ui.fragments.RegistrationFragment;
import com.ankoma88.intouch.utils.Settings;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;

/**
 * Created by ankoma88 on 12.06.16.
 */
public class AuthActivity extends AppCompatActivity implements AuthListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = AuthActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    public static final String EXTRA_EMAIL = "userEmail";
    public static final String EXTRA_PASSWORD = "userPassword";

    private CloudHelper cloudHelper;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LatLng latLng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        cloudHelper = new CloudHelper(AuthActivity.this);

        initGoogleApiClient();

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.container);
        if (fragment == null) {
            showLoginFragment();
        }

        if (!isOnline()) {
            askEnableWifi();
        }
    }

    private void showLoginFragment() {
        Fragment fragment = new LoginFragment();
        show(fragment, LoginFragment.TAG);
    }

    private void showRegistrationFragment(String email, String password) {
        Fragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_EMAIL, email);
        args.putSerializable(EXTRA_PASSWORD, password);
        fragment.setArguments(args);
        show(fragment, RegistrationFragment.TAG);
    }

    private void show(Fragment fragment, String tag) {
        Log.d(TAG, "show");
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.container, fragment, tag)
                .commit();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        cloudHelper.addAuthStateListener();

        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        cloudHelper.removeAuthStateListener();

        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    private void showPromptToRegister(String email, String password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
        builder.setTitle(getString(R.string.user_not_found))
                .setMessage(getString(R.string.prompt_register))
                .setCancelable(true)
                .setPositiveButton(getString(android.R.string.ok),
                        (dialog, id) -> {
                            showRegistrationFragment(email, password);
                        })
                .setNegativeButton(getString(android.R.string.no), (dialog, i) -> {
                    dialog.cancel();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLogin(String email, String password) {
        Log.d(TAG, "onLogin");
        cloudHelper.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "task failed");
                        finishLoading();
                        showPromptToRegister(email, password);
                    } else {
                        startUsersActivity();
                    }
                });
    }

    @Override
    public void onRegister(User newUser, String password) {
        Log.d(TAG, "onRegister");
        cloudHelper.getAuth().createUserWithEmailAndPassword(newUser.getEmail(), password)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "registration successful:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        onRegistrationFailed();
                    } else {
                        String userId = task.getResult().getUser().getUid();
                        setUsersLocation(newUser);
                        cloudHelper.saveUser(userId, newUser);
                    }
                }).addOnFailureListener(e -> onRegistrationFailed());
    }

    private void onRegistrationFailed() {
        Toast.makeText(AuthActivity.this, R.string.registration_failed,
                Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setUsersLocation(User newUser) {
        if (latLng == null) {
            latLng = new LatLng(Settings.DEFAULT_LOCATION_LATITUDE, Settings.DEFAULT_LOCATION_LONGITUDE);
        }
        newUser.setLatitude(latLng.latitude);
        newUser.setLongitude(latLng.longitude);
    }

    private void finishLoading() {
        LoginFragment fragment = (LoginFragment) getSupportFragmentManager()
                .findFragmentByTag(LoginFragment.TAG);
        if (fragment != null) {
            fragment.hideProgress();
        }
    }

    public void startUsersActivity() {
        Intent intent = new Intent(AuthActivity.this, UsersActivity.class);
        finish();
        startActivity(intent);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (isLocationTrackingForbidden()) {
            requestPermissionToAccessLocation();
            return;
        }
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (currentLocation != null) {
            Log.d(TAG, "current location: " + currentLocation);
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please reconnect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please reconnect.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Settings.LOCATION_UPDATE_INTERVAL)
                .setFastestInterval(Settings.LOCATION_FASTEST_INTERVAL);

        if (isLocationTrackingForbidden()) {
            requestPermissionToAccessLocation();
            return;
        }
        requestLocationUpdates();
    }

    private void requestPermissionToAccessLocation() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_LOCATION);
    }

    @SuppressWarnings("MissingPermission")
    private void requestLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    private boolean isLocationTrackingForbidden() {
       boolean isDisabled = ActivityCompat.checkSelfPermission(
               this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
               this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
        return isDisabled;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed: " + connectionResult);
    }

    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (googleApiClient.isConnected()) {
                        googleApiClient.reconnect();
                    } else {
                        googleApiClient.connect();
                    }
                    googleApiClient.reconnect();
                } else {
                    Log.d(TAG, "Permission to access location denied.");
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void askEnableWifi() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.turn_on_wifi));
        builder.setPositiveButton(getString(android.R.string.yes),
                (dialogInterface, i) -> {
                    WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                });
        builder.setNegativeButton(getString(android.R.string.no),
                (dialogInterface, i) -> {
                    finish();});
        builder.create().show();
    }

}
