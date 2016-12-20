package ashu.chatori.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import ashu.chatori.R;
import ashu.chatori.global.Constant;

/**
 * Created by apple on 07/09/16.
 */
public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private LoginButton loginButton;
    private SignInButton signInButton;
    private CallbackManager callbackManager;
    private int RC_SIGN_IN = 1;
    private AccessToken accessToken;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ProgressDialog progress;

    private String mFullName;
    private String mEmail;
    private String mPhoto;

    public static int APP_REQUEST_CODE = 99;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutAndVariables();
    }

    private void setLayoutAndVariables(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        signInButton = (SignInButton) findViewById(R.id.btn_sign_in);

        loginButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);

        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("user_status");
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("email");

        progress = new ProgressDialog(this);

        sp = getSharedPreferences("login", MODE_APPEND);

        editor = sp.edit();
    }

    private void loginFb() {

        progress.setMessage("Signing In ... ");
        progress.setIndeterminate(false);
        progress.show();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                if(accessToken == null)
                    accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                try {
                                    Log.d("value", response.toString());
                                    loginButton.setVisibility(View.GONE);
                                    mFullName = response.getJSONObject().getString("name");
                                    mPhoto = "https://graph.facebook.com/" + response.getJSONObject().getString("id")+ "/picture?type=large";
                                    mEmail = response.getJSONObject().getString("email");
                                    String from = "fb";

                                    editor.putString(Constant.NAME, mFullName);
                                    editor.putString(Constant.EMAIL, mEmail);
                                    editor.putString(Constant.PROFILE_PIC, mPhoto);
                                    editor.putString(Constant.FROM, from);
                                    editor.apply();

                                    progress.dismiss();

                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();

                                } catch (Exception e) {
                                    Log.d("fblogin", "error is = " + e.getMessage());
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,cover,email");
                request.setParameters(parameters);
                request.executeAsync();


//                if(accessToken != null) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

                loginButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void gPlusSignIn(){
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
// basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

// Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            progress.setMessage("Signing In ... ");
            progress.setIndeterminate(false);
            progress.show();

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                // Get account information
                mFullName = acct.getDisplayName();
                mEmail = acct.getEmail();
                mPhoto = acct.getPhotoUrl().toString();

                editor.putString(Constant.NAME, mFullName);
                editor.putString(Constant.EMAIL, mEmail);
                editor.putString(Constant.PROFILE_PIC, mPhoto);
                editor.putString(Constant.FROM, "gPlus");
                editor.apply();

                progress.dismiss();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }
        else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.login_button:
                loginFb();
                break;

            case R.id.btn_sign_in:
                gPlusSignIn();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}