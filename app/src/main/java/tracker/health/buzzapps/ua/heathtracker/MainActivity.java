package tracker.health.buzzapps.ua.heathtracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tracker.health.buzzapps.ua.heathtracker.Fragment.LoginFragment;
import tracker.health.buzzapps.ua.heathtracker.Fragment.MainFragment;
import tracker.health.buzzapps.ua.heathtracker.Fragment.PulseFragment;
import tracker.health.buzzapps.ua.heathtracker.Model.User;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        LoginFragment.OnLoginListener,
        MainFragment.OnLogoutListener{

    private static final int RC_SIGN_IN = 1;
    private static final String USERS = "users";
    private static final String DATA = "data";
    private static final String PULSE = "pulse";
    private static final String PULSE_VALUE = "pulseValue";
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    OnCompleteListener mOnCompleteListener;
    GoogleApiClient mGoogleApiClient;
    DatabaseReference  mDatabase;
    private static User currentUser;
    ProgressDialog myDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = null;
        initialiseListener();
        initialiseGoogle();
        myDialog = ProgressDialog.show(MainActivity.this, "Please wait","Trying to connect to the Internet", true);

    }



    private void initialiseGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    private void initialiseListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    Log.wtf("Tag","User " + user.getEmail()+ " "+ user.getDisplayName()+ " "+ user.getProviderId()+ " " + user.getPhotoUrl()+ " "
                            + user.getUid()+" " + user.getProviders());
                    checkUserInDB(user);


                }else {
                    switchToLoginFragment();
                }

            }
        };
        mOnCompleteListener  = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(!task.isSuccessful()){
                    showLoginError("Login failed");
                }
            }
        };

    }

    private void checkUserInDB(final FirebaseUser user) {
        mDatabase.child(USERS).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null){
                    User newUser = new User(
                            user.getDisplayName(),
                            user.getEmail(),
                            user.getProviderId(),
                            user.getPhotoUrl().toString(),"none");
                    mDatabase.child(USERS).child(user.getUid()).setValue(newUser);
                    currentUser = newUser;
                    currentUser.setUserid(user.getUid());
                    mDatabase.child(DATA).child(PULSE).child(user.getUid()).child(PULSE_VALUE).setValue(60);
                }
                else{
                    currentUser = dataSnapshot.getValue(User.class);
                    currentUser.setUserid(dataSnapshot.getKey());
                }
                switchToMainFragment();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
        });

        mDatabase.child(USERS).keepSynced(true);
    }



    private void switchToMainFragment() {
        //progressBar.setVisibility(View.INVISIBLE);
        myDialog.dismiss();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, new MainFragment(), "Main");
        ft.commit();
    }

    private void switchToLoginFragment() {
        myDialog.dismiss();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, new LoginFragment(), "Login");
        ft.commit();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showLoginError("Google Connection Failed");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener!=null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(mOnCompleteListener);

        //TODO: Log user in with username & password
    }

    @Override
    public void onGoogleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onRosefireLogin() {

    }

    private void showLoginError(String s) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("Login");
        loginFragment.onLoginError(s);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                showLoginError("Google signIn failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Tag", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, mOnCompleteListener);
    }

    @Override
    public void onLogout() {
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        switchToLoginFragment();
    }

    public static User getCurrentUser()
    {
        return currentUser;
    }


}
