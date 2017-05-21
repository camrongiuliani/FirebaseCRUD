package com.aztechdev.CodeTest_Camron_Giuliani;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.aztechdev.CodeTest_Camron_Giuliani.fragments.NoConnectionFragment;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

import static com.aztechdev.CodeTest_Camron_Giuliani.MainActivity.EXTRA_UID;

public class LoginActivity extends AppCompatActivity implements OnClickListener, FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult> {

    public static final String PREF_REMEMBER_ME = "PREF_REMEMBER_ME";
    private static final String PREF_EMAIL_ADDRESS = "PREF_EMAIL_ADDRESS";
    private static final String PREF_PASSWORD = "PREF_PASSWORD";
    private static final String TAG = "LoginActivity";


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private BroadcastReceiver mBroadcastReceiver;
    private DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Configure buttons onClickListener
        Button registerButton = (Button) findViewById(R.id.btnCreateAccount);
        registerButton.setOnClickListener(this);

        Button signInButton = (Button) findViewById(R.id.btnSignIn);
        signInButton.setOnClickListener(this);

        //Set the firebase context
        Firebase.setAndroidContext(this);

        //Get an instance of the FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();

        //Set this as the FirebaseAuth.AuthStateListener
        mAuthListener = this;



        //Setup remember me based checkbox state
        setPrefRememberMe();
    }

    private void setPrefRememberMe() {
        //Get share preferences as a map.
        SharedPreferences prefs = getSharedPreferences(getApplicationContext().getPackageName(), MODE_PRIVATE);
        Map<String, ?> prefsMap = prefs.getAll();

        //Handle the checkbox state
        Boolean remember;

        if (prefsMap.containsKey(PREF_REMEMBER_ME)) {
            remember = (Boolean) prefsMap.get(PREF_REMEMBER_ME);

            CheckBox checkBox = (CheckBox) findViewById(R.id.cbRemember);
            checkBox.setChecked(remember);

            //Update the preferences with the users email and password
            if (remember && prefsMap.containsKey(PREF_EMAIL_ADDRESS) && prefsMap.containsKey(PREF_PASSWORD)) {
                String email = (String) prefsMap.get(PREF_EMAIL_ADDRESS);
                String password = (String) prefsMap.get(PREF_PASSWORD);

                //Sign in using the updated credentials
                signIn(email, password);
            }
        }
    }

    private void createAccount(final String email, final String password) {
        //Method to create an account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, this);
    }

    private void signIn(String email, String password) {
        //Method to sign in to an account
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Attach the AuthState listener
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            //detach the auth state listener
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void showNoConnectionDialog() {
        dialogFragment = NoConnectionFragment.newInstance();
        dialogFragment.setCancelable(false);
        FragmentManager fm = getSupportFragmentManager();
        dialogFragment.show(fm, "NoConnectionFragment");
    }

    private void dismissNoConnectionDialog() {
        if (dialogFragment != null)
            dialogFragment.dismiss();

        dialogFragment = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Register the non-exported receiver.
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isDisconnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);


                if (!isDisconnected){
                    dismissNoConnectionDialog();
                } else {
                    showNoConnectionDialog();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Unregister the non-exported receiver.
        unregisterReceiver(mBroadcastReceiver);
    }



    @Override
    public void onClick(View v) {
        //On button click, wire up views.
        EditText emailField = (EditText) findViewById(R.id.etEmail);
        EditText passwordField = (EditText) findViewById(R.id.etPassword);
        CheckBox checkBox = (CheckBox) findViewById(R.id.cbRemember);


        String lastEmail = emailField.getText().toString().trim();
        String lastPass = passwordField.getText().toString().trim();

        //Verify input
        if (lastEmail.equals("") || lastPass.equals("")) {
            Toast.makeText(this, "Empty Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (v.getId() == R.id.btnCreateAccount){
            //Create an account
            createAccount(lastEmail, lastPass);
        } else if (v.getId() == R.id.btnSignIn){

            //Set the remember me settings
            SharedPreferences prefs = getSharedPreferences(getApplicationContext().getPackageName(), MODE_PRIVATE);

            prefs.edit()
                    .putBoolean(PREF_REMEMBER_ME, checkBox.isChecked())
                    .putString(PREF_EMAIL_ADDRESS, lastEmail)
                    .putString(PREF_PASSWORD, lastPass).apply();

            //Sign-In
            signIn(lastEmail, lastPass);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            // User is signed in. Show main activity
            Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainActivityIntent.putExtra(EXTRA_UID, user.getUid());
            startActivity(mainActivityIntent);
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
        if (!task.isSuccessful()) {
            Exception taskException = task.getException();

            assert taskException != null;
            Toast.makeText(getApplicationContext(), taskException.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}