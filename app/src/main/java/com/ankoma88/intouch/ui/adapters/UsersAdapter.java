package com.ankoma88.intouch.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ankoma88.intouch.R;
import com.ankoma88.intouch.models.User;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ankoma88 on 12.06.16.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    private List<User> users = new ArrayList<>();
    private final OnItemClickListener listener;

    public UsersAdapter(List<User> users, OnItemClickListener listener) {
        if (users != null) {
            this.users = users;
        }
        this.listener = listener;
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvNickname)
        public TextView tvNickname;
        @Bind(R.id.tvFirstName)
        public TextView tvFirstName;
        @Bind(R.id.tvLastName)
        public TextView tvLastName;
        @Bind(R.id.tvEmail)
        public TextView tvEmail;
        @Bind(R.id.tvLocation)
        public TextView tvLocation;


        public UsersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final User userItem, final OnItemClickListener listener) {
            tvNickname.setText(userItem.getNickname());
            tvFirstName.setText(userItem.getFirstName());
            tvLastName.setText(userItem.getLastName());
            tvEmail.setText(userItem.getEmail());
            tvLocation.setText(locationToString(userItem));

            itemView.setOnClickListener(v -> listener.onItemClick(userItem));
        }
    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);

        return new UsersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UsersViewHolder holder, int position) {
        holder.bind(users.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @NonNull
    private String locationToString(User userItem) {
        DecimalFormat df = new DecimalFormat("#.0000");
        return df.format(userItem.getLatitude()) + " : " + df.format(userItem.getLongitude());
    }

    public interface OnItemClickListener {
        void onItemClick(User userItem);
    }

}
