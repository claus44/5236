package com.example.a5236.data;

import com.example.a5236.Account;
import com.example.a5236.data.model.LoggedInUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;




    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public  Result<LoggedInUser> register(String username, String password) {
        // TODO: handle adding user
        try {
            mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("Accounts").push();
            DatabaseReference ref = mDatabase.child("Accounts").push();

            Account account = new Account("abc", "123456", 0);
            ref.setValue(account);



            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            username);
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error Signing up", e));
        }
    }

//    public Result<LoggedInUser> deleteUser(String Username, String password) {
//        //TODO: Authenticate username, password, and delete
//    }

    public void logout() {
        // TODO: revoke authentication
    }
}