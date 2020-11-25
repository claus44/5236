package com.example.a5236.ui.login;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.a5236.LoginActivity;
import com.example.a5236.R;
import com.example.a5236.data.model.LoggedInUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SignUpFragment extends Fragment {

    private LoginViewModel signUpViewModel;
    private DatabaseReference mDatabase;
    private LoggedInUser user;
    private static final String TAG = "SignUpFragment";
    private ProgressBar loadingProgressBar;
    private EditText usernameEditText, passwordEditText,passwordConfirmationEditText;
    private Button signUpButton,loginButton;
    private Context mContext;

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
        mContext = (LoginActivity) getActivity();
        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);
        passwordConfirmationEditText = view.findViewById(R.id.passwordConfirmation);
        signUpButton = view.findViewById(R.id.signup);
        loginButton = view.findViewById(R.id.login);
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
            mDatabase.child("Friends").child(account.getUsername()).setValue(username);
            ArrayList<String> friends = new ArrayList<String>();
            friends.add(username);
            mDatabase.child("Friends").child(account.getUsername()).setValue(friends);
            LoginActivity.setUser(account);
            LoginActivity.setFriends(friends);
        }
        return userExists;
    }

    private void signUp(){
        if(LoginActivity.isConnectedToInternet(mContext)){
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
                            LoginActivity.retrieveLandmarkData(snapshot,mContext);
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
        }else{
            Toast.makeText(mContext,  getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }
}