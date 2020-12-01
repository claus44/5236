package com.example.a5236;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.navigation.fragment.NavHostFragment;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.a5236.data.model.LoggedInUser;
import com.example.a5236.ui.login.LoginFragment;

import org.junit.Test;
import org.junit.Rule;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 */
@LargeTest
public class LoginActivityInstrumentedTest extends ActivityTestRule<LoginActivity>{
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    private LoginActivity mLoginActivity;
    private LoginFragment mLoginFragment;
    private NavHostFragment mNavHostFragment;
    private Button signInButton, createAccountButton;
    private EditText username, password;



    public LoginActivityInstrumentedTest() {
        super(LoginActivity.class);
        launchActivity(getActivityIntent());
        mLoginActivity = getActivity();
        mLoginFragment = mLoginActivity.getLoginFragmentForTest();
        mNavHostFragment = mLoginActivity.getNHFragmentForTest();

        // Wait for the Activity to become idle so we don't have null Fragment references.
        getInstrumentation().waitForIdleSync();

        if (mLoginFragment != null) {
            View fragmentView = mLoginFragment.getView();
            if (fragmentView != null) {
                signInButton = fragmentView.findViewById(R.id.signup);
                createAccountButton = fragmentView.findViewById(R.id.register);
                username = fragmentView.findViewById(R.id.username);
                password = fragmentView.findViewById(R.id.password);

            }
        }
    }

    @Test
    public void testSignInButton() throws InterruptedException {
        Button singInButton = (Button) getActivity().findViewById(R.id.signup);
        assertNotNull(singInButton);
    }

    @Test
    public void testPreconditions() {
        assertNotNull(mLoginActivity);
        assertNotNull(mLoginFragment);
        assertNotNull(signInButton);
        assertNotNull(createAccountButton);
        assertNotNull(username);
        assertNotNull(password);
    }
}