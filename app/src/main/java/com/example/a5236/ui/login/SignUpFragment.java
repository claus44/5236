package com.example.a5236.ui.login;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a5236.Account;
import com.example.a5236.Landmark;
import com.example.a5236.LandmarkActivity;
import com.example.a5236.LoginActivity;
import com.example.a5236.R;
import com.example.a5236.data.model.LoggedInUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUpFragment extends Fragment {

    private LoginViewModel signUpViewModel;
    private DatabaseReference mDatabase;
    private LoggedInUser user;
    private static final String TAG = "SignUpFragment";
    private ProgressBar loadingProgressBar;
    private EditText usernameEditText, passwordEditText,passwordConfirmationEditText;
    private Button signUpButton,loginButton,updatePassword,deleteUser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signUpViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);
        passwordConfirmationEditText = view.findViewById(R.id.passwordConfirmation);
        signUpButton = view.findViewById(R.id.signup);
        loginButton = view.findViewById(R.id.login);
        updatePassword = view.findViewById(R.id.update_password);
        deleteUser = view.findViewById(R.id.deleteUser);
        loadingProgressBar = view.findViewById(R.id.loading);

        signUpViewModel.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                signUpButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
                if (loginFormState.getPasswordConfirmationError() != null) {
                    passwordConfirmationEditText.setError(getString(loginFormState.getPasswordConfirmationError()));
                }
            }
        });

        signUpViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                signUpViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordConfirmationEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signUp();
                }
                return false;
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(SignUpFragment.this)
                        .navigate(R.id.action_signUpFragment_to_loginFragment);
            }
        });

        //TODO: Move into profile fragment and fix username variable
        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                // finding username will be changed to the logged in user - there will be no text field
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                String passwordConfirm = passwordConfirmationEditText.getText().toString();
                // this if won't need to check for username empty later
                if(!password.equals("") && signUpViewModel.updatePasswordMatch(password, passwordConfirm) && !username.equals("")){
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean updatedPassword = updateUserCheck(snapshot, username, password);
                            if(updatedPassword){
                                NavHostFragment.findNavController(SignUpFragment.this)
                                        .navigate(R.id.action_signUpFragment_to_landmarkActivity);
                            }else{
                                loadingProgressBar.setVisibility(View.GONE);
                                Toast.makeText((LoginActivity) getActivity(),"Update Failed: User does not exist",Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }else{
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText((LoginActivity) getActivity(),"Cannot update with blank information",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //TODO: Move into profile fragment (this will just be a button -will not make user confirm password)
        // and fix username variable
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                // finding username will be changed to the logged in user - there will be no text field
                final String username = usernameEditText.getText().toString();
                // this if and else won't be necessary later
                if(!username.equals("")){
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean userDeleted = deleteUser(snapshot, username);
                            if(userDeleted){
                                loadingProgressBar.setVisibility(View.GONE);
                                Toast.makeText((LoginActivity) getActivity(),"Successfully Deleted "+username,Toast.LENGTH_SHORT).show();
                            }else{
                                loadingProgressBar.setVisibility(View.GONE);
                                Toast.makeText((LoginActivity) getActivity(),"Delete Failed: User does not exist",Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }else {
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText((LoginActivity) getActivity(), "Username is blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + " "+ model.getDisplayName();
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        }
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();
        }
    }
    private boolean checkUsernameExists(DataSnapshot dataSnapshot, String username, String password) {
        boolean userExists = false;
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            if (ds.getKey().equals("Accounts")){
                HashMap<String, Object> hm = (HashMap<String, Object>) ds.getValue();
                userExists = hm.containsKey(username);
            }
        }
        if (!userExists) {
            Account account = new Account(username, password, 0);
            mDatabase.child("Accounts").child(account.getUsername()).setValue(account);
        }
        return userExists;
    }
    // TODO: move to profile fragment for updating password for user
    private boolean updateUserCheck(DataSnapshot dataSnapshot, String username, String password) {
        boolean updatedPassword = false;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals("Accounts")) {
                HashMap<String, Object> hm = (HashMap<String, Object>) ds.getValue();
                if(hm.containsKey(username)){
                    Account updatedAccount = new Account(username, password,  Integer.parseInt(((HashMap<String, Object>) hm.get(username)).get("score").toString()));
                    mDatabase.child("Accounts").child(username).setValue(updatedAccount);
                    updatedPassword = true;
                 }
            }
        }
        return updatedPassword;
    }
    // TODO: move to profile fragment for deleting user
    private boolean deleteUser(DataSnapshot dataSnapshot, String username){
        boolean userDeleted = false;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals("Accounts")) {
                HashMap<String, Object> hm = (HashMap<String, Object>) ds.getValue();
                if (hm.containsKey(username)) {
                    mDatabase.child("Accounts").child(username).removeValue();
                    userDeleted = true;
                }
            }
        }
        return userDeleted;
    }
    private void signUp(){
        loadingProgressBar.setVisibility(View.VISIBLE);
        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmationEditText.getText().toString();
        if(password.equals(passwordConfirm)){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            user = new LoggedInUser(username);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean userExists = checkUsernameExists(snapshot, username, password);
                    if(!userExists){
                        signUpViewModel.register(true, user, true);
                        LoginActivity.setLoggedInUser(user);
                        LoginActivity.retrieveLandmarkData(snapshot);
                        LoginActivity.retrieveLeaderboardData(snapshot);
                        NavHostFragment.findNavController(SignUpFragment.this)
                                .navigate(R.id.action_signUpFragment_to_landmarkActivity);
                    }else{
                        signUpViewModel.register(true, user, false);
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }else{
            signUpViewModel.register(false, user, false);
        }
    }
}