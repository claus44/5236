package com.example.a5236.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.a5236.data.LoginRepository;
import com.example.a5236.data.Result;
import com.example.a5236.data.model.LoggedInUser;
import com.example.a5236.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(boolean loginComplete, LoggedInUser data ){
        if(loginComplete){
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getUserId())));
            loginRepository.login(loginComplete,data);
        }else{
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void register(boolean passwordMatch, LoggedInUser data, boolean userCreated){
        if(!passwordMatch){
            loginResult.setValue(new LoginResult(R.string.password_confirmation_failed));
        }else{
            if(userCreated){
                loginResult.setValue(new LoginResult(new LoggedInUserView(data.getUserId())));
                loginRepository.register(userCreated,data);
            }else{
                loginResult.setValue(new LoginResult(R.string.registration_failed));
                loginRepository.register(userCreated,data);
            }
        }
    }

    public boolean updatePasswordMatch(String password, String passwordConfirmation) {
        boolean passwordsMatch = true;
        if (!password.equals(passwordConfirmation)) {
            loginResult.setValue(new LoginResult(R.string.password_confirmation_failed));
            passwordsMatch = false;
        }
        return passwordsMatch;
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password, null));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    public void signUpDataChanged(String username, String password, String passwordConfirmation) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password, null));
        } else if (!password.equals(passwordConfirmation)) {
            loginFormState.setValue(new LoginFormState(null, null, R.string.password_confirmation_failed));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

}