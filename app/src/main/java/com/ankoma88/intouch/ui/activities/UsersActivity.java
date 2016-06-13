package com.ankoma88.intouch.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ankoma88.intouch.R;
import com.ankoma88.intouch.cloud.CloudHelper;
import com.ankoma88.intouch.interfaces.UserLoadListener;
import com.ankoma88.intouch.interfaces.UserLoadResult;
import com.ankoma88.intouch.models.User;
import com.ankoma88.intouch.ui.fragments.MapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankoma88 on 12.06.16.
 */
public class UsersActivity extends AppCompatActivity implements UserLoadListener {
    public final static String TAG = UsersActivity.class.getSimpleName();

    public static final String EXTRA_USERS = "inTouchUsers";
    private CloudHelper cloudHelper;
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cloudHelper = new CloudHelper(UsersActivity.this);
        setContentView(R.layout.activity_user_locations);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_locations, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                showMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadUsers(UserLoadResult userLoadResult) {
        Log.d(TAG, "loadUsers");
        cloudHelper.getUsers(userLoadResult);
    }

    private void showMap() {
        Intent intent = new Intent(this, MapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_USERS, users);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void showUsersOnMap(List<User> users) {
        this.users = (ArrayList<User>) users;
        FragmentManager manager = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) manager.findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.showUsersOnMap(users);
        }
    }

    @Override
    public void findUserOnMap(User user) {
        FragmentManager manager = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) manager.findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.showUserOnMap(user);
        }
    }

}
