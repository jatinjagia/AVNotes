package com.example.jatin.avnotes.activities;

/**
 * Created by jatin on 16/12/17.
 */
import com.example.jatin.avnotes.R;
import com.example.jatin.avnotes.Utils.PrefsUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

public abstract class BaseDriveActivity extends Activity {

    private static final String TAG = "BaseDriveActivity";

    protected static final String ACCOUNT_NAME_KEY = "account_name";
    private static final int REQUEST_CODE_SIGN_IN = 0;
    protected static final int NEXT_AVAILABLE_REQUEST_CODE = 1;
    protected GoogleSignInClient mGoogleSignInClient;
    protected DriveClient mDriveClient;
    protected DriveResourceClient mDriveResourceClient;
    protected String mAccountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void signInAction(){
        if (!isSignedIn()) {
            signIn();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACCOUNT_NAME_KEY, mAccountName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAccountName = savedInstanceState.getString(ACCOUNT_NAME_KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.i(TAG, "Sign-in request code.");
            // Called after user is signed in.
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Signed in successfully.");
                PrefsUtils.getInstance(this).setValue(getString(R.string.first_login),true);
                boolean name= PrefsUtils.getInstance(this).hasValue(getString(R.string.first_login));
                // Create Drive clients now that account has been authorized access.
                createDriveClients(GoogleSignIn.getLastSignedInAccount(this));
            } else {
                Log.w(TAG, String.format("Unable to sign in, result code %d", resultCode));
                LoginActivity.activityCheck=false;
            }
        }
    }

    protected boolean isSignedIn() {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        return mGoogleSignInClient != null
                && (signInAccount != null
                && signInAccount.getGrantedScopes().contains(Drive.SCOPE_FILE));
    }

    /**
     * Attempts silent sign-in. On failure, start a sign-in {@link Intent}.
     */
    protected void signIn() {
        Log.i(TAG, "Start sign-in.");
        mGoogleSignInClient = getGoogleSignInClient();
        // Attempt silent sign-in
        mGoogleSignInClient.silentSignIn()
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        createDriveClients(googleSignInAccount);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Silent sign-in failed, display account selection prompt
                startActivityForResult(
                        mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
            }
        });
    }

    protected void signOut(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        PrefsUtils.getInstance(getApplicationContext()).setValue(getString(R.string.first_login),false);
                        boolean name=PrefsUtils.getInstance(getApplicationContext()).hasValue(getString(R.string.first_login));
                        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    /**
     * Builds a Google sign-in client.
     */
    private GoogleSignInClient getGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    /**
     * Builds the Drive clients after successful sign-in.
     *
     * @param googleSignInAccount The account which was signed in to.
     */
    private void createDriveClients(GoogleSignInAccount googleSignInAccount) {
        Log.i(TAG, "Update view with sign-in account.");
        // Build a drive client.
        mDriveClient = Drive.getDriveClient(getApplicationContext(), googleSignInAccount);
        // Build a drive resource client.
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), googleSignInAccount);
        if (LoginActivity.activityCheck){
            navigateToMainActivity();
        }
    }

    protected void navigateToMainActivity() {
        LoginActivity.activityCheck=false;
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
