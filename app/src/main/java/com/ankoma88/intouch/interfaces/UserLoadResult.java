package com.ankoma88.intouch.interfaces;

import com.ankoma88.intouch.models.User;

import java.util.List;

/**
 * Created by ankoma88 on 12.06.16.
 */
public interface UserLoadResult {
    void onUserLoadResult(List<User> userList);
}
