package com.example.jatin.avnotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.SignInButton;

/**
 * Created by jatin on 16/12/17.
 */

public class LoginActivity extends BaseDriveActivity implements View.OnClickListener{


    SignInButton mSignInButton;
    protected static boolean activityCheck=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean name=PrefsUtils.getInstance(this).hasValue(getString(R.string.first_login));
        if (!PrefsUtils.getInstance(this).getValue(getString(R.string.first_login))) {
            setContentView(R.layout.login_activity);
            mSignInButton = findViewById(R.id.sign_in);
            mSignInButton.setOnClickListener(this);
        }
        else
            navigateToMainActivity();


    }



    @Override
    public void onClick(View view) {
        activityCheck=true;
        signInAction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
