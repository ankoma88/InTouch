package com.ankoma88.intouch.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ankoma88.intouch.R;
import com.ankoma88.intouch.interfaces.UserLoadListener;
import com.ankoma88.intouch.interfaces.UserLoadResult;
import com.ankoma88.intouch.models.User;
import com.ankoma88.intouch.ui.adapters.UsersAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ankoma88 on 12.06.16.
 */
public class UsersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, UserLoadResult {
    private static final String TAG = UsersFragment.class.getSimpleName();

    private UserLoadListener userLoadListener;

    @Bind(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    @Bind(R.id.rvUsers)
    RecyclerView rvUsers;

    public UsersFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        userLoadListener = (UserLoadListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        userLoadListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_users, container, false);
        setHasOptionsMenu(false);
        setRetainInstance(true);
        ButterKnife.bind(this, rootView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvUsers.setLayoutManager(layoutManager);
        refreshLayout.setOnRefreshListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadUsers();
    }

    private void loadUsers() {
        Log.d(TAG, "loadUsers");
        userLoadListener.loadUsers(this);
    }

    @Override
    public void onRefresh() {
        loadUsers();
    }


    @Override
    public void onUserLoadResult(List<User> userList) {
        final UsersAdapter adapter = new UsersAdapter(userList, userItem ->
                userLoadListener.findUserOnMap(userItem));
        rvUsers.setAdapter(adapter);
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }

        userLoadListener.showUsersOnMap(userList);
    }
}
