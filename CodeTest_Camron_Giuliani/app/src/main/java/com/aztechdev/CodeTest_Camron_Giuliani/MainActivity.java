package com.aztechdev.CodeTest_Camron_Giuliani;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.aztechdev.CodeTest_Camron_Giuliani.classes.Member;
import com.aztechdev.CodeTest_Camron_Giuliani.fragments.MemberDetailFragment;
import com.aztechdev.CodeTest_Camron_Giuliani.fragments.MemberListFragment;
import com.aztechdev.CodeTest_Camron_Giuliani.fragments.MemberMgmtFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.aztechdev.CodeTest_Camron_Giuliani.LoginActivity.PREF_REMEMBER_ME;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, ValueEventListener,
        MemberMgmtFragment.AddMemberListener, MemberListFragment.ListItemClickListener, MemberDetailFragment.UpdateMemberListener{

    private static final String TAG = "MainActivity";
    public static final String EXTRA_UID = "EXTRA_UID";
    public static final String EXTRA_ARRAY = "EXTRA_ARRAY";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ValueEventListener mValueEventListener;
    private DatabaseReference mDatabaseReference;
    private ArrayList<Member> mArrayList;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the userId passed in from the intent
        mUid = getIntent().getStringExtra(EXTRA_UID);

        //Get an instance of the FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //Set the FirebaseAuth.AuthStateListener to this
        mAuthListener = this;

        //Set the ValueEventListener to this
        mValueEventListener = this;

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        //Get a reference to the specific users Firebase node
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mUid);


        //Setup the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        toolbar.setTitle("Gym Members");
        setSupportActionBar(toolbar);

        //Show the member management fragment
        Fragment managementFrag = MemberMgmtFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.MemberDetailFragment, managementFrag)
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mDatabaseReference.addValueEventListener(mValueEventListener);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            //Detach auth state listener
            mAuth.removeAuthStateListener(mAuthListener);
        }

        if (mValueEventListener != null){
            //Remove ValueEventListener
            mDatabaseReference.removeEventListener(mValueEventListener);
        }

        SharedPreferences prefs = getSharedPreferences(getApplicationContext().getPackageName(), MODE_PRIVATE);
        Map<String, ?> prefsMap = prefs.getAll();

        Boolean remember;

        if (prefsMap.containsKey(PREF_REMEMBER_ME)) {
            remember = (Boolean) prefsMap.get(PREF_REMEMBER_ME);
            if (!remember)
                mAuth.signOut();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences prefs = getSharedPreferences(getApplicationContext().getPackageName(), MODE_PRIVATE);

        prefs.edit().putBoolean(PREF_REMEMBER_ME, false).apply();

        mAuth.signOut();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.
        getMenuInflater().inflate(R.menu.main_action_bar, menu);
        return true;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());



        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
            Intent mainActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(mainActivityIntent);
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {


        Map<?, ?> objectMap = (HashMap<?, ?>) dataSnapshot.getValue();
        ArrayList<Member> arrayList = new ArrayList<>();

        if (objectMap != null) {
            for (Object obj : objectMap.values()) {
                if (obj instanceof Map) {
                    //noinspection unchecked obj
                    Map<String, Object> mapObj = (Map<String, Object>) obj;
                    Member member = new Member(
                            (String) mapObj.get("userId"),
                            (String) mapObj.get("firstName"),
                            (String) mapObj.get("lastName"),
                            (Long) mapObj.get("zipCode"),
                            (String) mapObj.get("birthDate"),
                            (String) mapObj.get("phoneNumber"));
                    arrayList.add(member);
                }
            }
        }
        mArrayList = arrayList;

        Fragment listFrag = MemberListFragment.newInstance(mArrayList);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.MemberListFragment, listFrag)
                .commit();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {}

    @Override
    public void AddMember(Member member) {
        Log.d(TAG, "AddMember: " + member);

        //Get the key that is being pushed
        String Key = mDatabaseReference.push().getKey();

        //Update the member within the local member array
        member.setUserId(Key);

        //Add the new member to the Firebase Database
        mDatabaseReference.child(Key).setValue(member);
    }

    @Override
    public void onListItemClick(final Integer position) {

        DialogFragment frag = MemberDetailFragment.newInstance(mArrayList.get(position), mUid);
        FragmentManager fm = getSupportFragmentManager();
        frag.show(fm, "DetailFrag");


    }

    @Override
    public void RemoveMember(Member member) {
        mDatabaseReference.child(member.getUserId()).removeValue();
    }

    @Override
    public void UpdateMember(Member member) {
        mDatabaseReference.child(member.getUserId()).setValue(member);
    }
}
