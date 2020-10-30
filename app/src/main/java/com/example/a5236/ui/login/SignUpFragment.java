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
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private LoggedInUser user;
    private static final String TAG = "SignUpFragment";

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

        final EditText usernameEditText = view.findViewById(R.id.username);
        final EditText passwordEditText = view.findViewById(R.id.password);
        final EditText passwordConfirmationEditText = view.findViewById(R.id.passwordConfirmation);
        final Button signUpButton = view.findViewById(R.id.signup);
        final Button loginButton = view.findViewById(R.id.login);
        final Button updatePassword = view.findViewById(R.id.update_password);
        final Button deleteUser = view.findViewById(R.id.deleteUser);
        final ProgressBar loadingProgressBar = view.findViewById(R.id.loading);

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
                    signUpViewModel.register(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString(),
                            passwordConfirmationEditText.getText().toString());
                }
                return false;
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                String passwordConfirm = passwordConfirmationEditText.getText().toString();

//                if (signUpViewModel.register(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString(),
//                        passwordConfirmationEditText.getText().toString())) {
//                    NavHostFragment.findNavController(SignUpFragment.this)
//                            .navigate(R.id.action_signUpFragment_to_landmarkActivity); //TODO: update action
//                }
                if(password.equals(passwordConfirm)){
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    user = new LoggedInUser(username);
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean userExists = checkUsernameExists(snapshot, username, password);
                            if(!userExists){
                                signUpViewModel.register(false, user, true);
                                NavHostFragment.findNavController(SignUpFragment.this)
                                    .navigate(R.id.action_signUpFragment_to_landmarkActivity);
                            }else{
                                signUpViewModel.register(false, user, false);
                                loadingProgressBar.setVisibility(View.GONE);
//                                Toast.makeText((LoginActivity) getActivity(),"Username already taken",Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }else{
                    signUpViewModel.register(true, user, false);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(SignUpFragment.this)
                        .navigate(R.id.action_signUpFragment_to_loginFragment);
            }
        });

        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                if (signUpViewModel.updatePassword(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        passwordConfirmationEditText.getText().toString())) {
                    NavHostFragment.findNavController(SignUpFragment.this)
                            .navigate(R.id.action_signUpFragment_to_loginFragment); //TODO: update action
                }
            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                if (signUpViewModel.deleteUser(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        passwordConfirmationEditText.getText().toString())) {
                    NavHostFragment.findNavController(SignUpFragment.this)
                            .navigate(R.id.action_signUpFragment_to_loginFragment); //TODO: update action
                }
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful sign up in experience
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
}