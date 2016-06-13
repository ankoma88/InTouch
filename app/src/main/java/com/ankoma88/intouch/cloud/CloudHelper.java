package com.ankoma88.intouch.cloud;

import android.content.Context;
import android.util.Log;

import com.ankoma88.intouch.interfaces.UserLoadResult;
import com.ankoma88.intouch.models.User;
import com.ankoma88.intouch.ui.activities.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ankoma88 on 12.06.16.
 */
public class CloudHelper {
    private static final String TAG = CloudHelper.class.getSimpleName();
    private final Context context;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference database;

    public CloudHelper(Context context) {
        this.context = context;
        initCloudDb();
        initCloudAuth();
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public void initCloudDb() {
        if (database != null) {
            return;
        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        database = firebaseDatabase.getReference();

        ValueEventListener usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "onDataChange user: " + user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "loadUser:onCancelled", databaseError.toException());
            }
        };
        database.addValueEventListener(usersListener);

    }

    private void initCloudAuth() {
        auth = FirebaseAuth.getInstance();
        authListener = firebaseAuth -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                Log.d(TAG, "current user id:" + firebaseUser.getUid());
            } else {
                Log.d(TAG, "user logged out");
            }
        };
    }

    public void saveUser(String userId, User user) {
        database.child("users").child(userId).setValue(user, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                Log.d(TAG, "databaseError: " + databaseError);
            } else {
                Log.d(TAG, "user saved with id: " + databaseReference.getKey());
                ((AuthActivity) context).startUsersActivity();
            }
        });
    }

    public void addAuthStateListener() {
        auth.addAuthStateListener(authListener);
    }

    public void removeAuthStateListener() {
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public void getUsers(UserLoadResult userLoadResult) {
        Log.d(TAG, "getUsers");
        Query getAll = database.child("users");
        getAll.addValueEventListener(new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    List<User> allUsers = new ArrayList<>();
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        User user = iterator.next().getValue(User.class);
                        allUsers.add(user);
                    }
                    userLoadResult.onUserLoadResult(allUsers);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
}
