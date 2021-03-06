package com.example.a5236.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer passwordConfirmationError;
    private boolean isDataValid;

    LoginFormState(@Nullable Integer usernameError, @Nullable Integer passwordError, @Nullable Integer passwordConfirmationError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.passwordConfirmationError = passwordConfirmationError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.passwordConfirmationError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() { return passwordError; }

    @Nullable
    Integer getPasswordConfirmationError() { return passwordConfirmationError; }

    boolean isDataValid() {
        return isDataValid;
    }
}