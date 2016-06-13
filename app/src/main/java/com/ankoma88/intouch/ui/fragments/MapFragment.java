package com.ankoma88.intouch.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ankoma88.intouch.R;
import com.ankoma88.intouch.models.User;
import com.ankoma88.intouch.ui.activities.UsersActivity;
import com.ankoma88.intouch.utils.Settings;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ankoma88 on 12.06.16.
 */
public class MapFragment extends Fragment {
    private static final String TAG = MapFragment.class.getSimpleName();
    @Bind(R.id.mapView)
    MapView mapView;

    private GoogleMap map;

    public MapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_map, container, false);
        setHasOptionsMenu(false);
        setRetainInstance(true);
        ButterKnife.bind(this, rootView);
        initMap(savedInstanceState);
        return rootView;
    }

    private void initMap(@Nullable Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            map = googleMap;
            map.getUiSettings().setZoomControlsEnabled(true);
            MapsInitializer.initialize(MapFragment.this.getActivity());
            pointCameraToLocation(new LatLng(Settings.DEFAULT_LOCATION_LATITUDE, Settings.DEFAULT_LOCATION_LONGITUDE));

            restoreState();
        });
    }

    private void restoreState() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            List<User> users = (List<User>) bundle.getSerializable(UsersActivity.EXTRA_USERS);
            showUsersOnMap(users);
        }
    }

    public void showUsersOnMap(List<User> users) {
        Log.d(TAG, "showUsersOnMap");
        for (User user : users) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(user.getLatitude(), user.getLongitude()))
                    .title(user.getNickname()));
            marker.showInfoWindow();
        }
    }

    public void showUserOnMap(User user) {
        Log.d(TAG, "showUserOnMap");
        if (map != null) {
            pointCameraToLocation(new LatLng(user.getLatitude(), user.getLongitude()));
        }
    }

    private void pointCameraToLocation(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 8);
        map.animateCamera(cameraUpdate);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
