package com.goyo.tracking.tracking.initials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.common.Checker;
import com.goyo.tracking.tracking.common.LoginClass;
import com.goyo.tracking.tracking.dashboard.dash;
import com.goyo.tracking.tracking.forms.registrationinfo;
import com.goyo.tracking.tracking.globals.Global;
import com.goyo.tracking.tracking.main;
import com.goyo.tracking.tracking.model.model_loginusr;
import com.goyo.tracking.tracking.model.model_loginusr_vts;
import com.goyo.tracking.tracking.utils.SHP;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class login extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 991;
    private static final int FACEBOOK_SIGN_IN = CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode();
    private static final String TAG = "login_Activity";
    private static final String EMAIL = "email";
    /* form variable */
    EditText edtUserName, edtPassword;
    Button btnLogin;
    ProgressDialog loader;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        onCerateInit();

        initAllControls();


//        PackageInfo info;
//        try {
//            info = getPackageManager().getPackageInfo("com.goyo.tracking.tracking", PackageManager.GET_SIGNATURES);
//            for (android.content.pm.Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                //String something = new String(Base64.encodeBytes(md.digest()));
//                Log.e("hash key", something);
//            }
//        } catch (PackageManager.NameNotFoundException e1) {
//            Log.e("name not found", e1.toString());
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("no such an algorithm", e.toString());
//        } catch (Exception e) {
//            Log.e("exception", e.toString());
//        }
    }


    @BindView(R.id.btnFacebook)
    Button btnFacebook;


    @BindView(R.id.btnFacebook_main)
    LoginButton btnFacebook_main;

    @OnClick(R.id.btnFacebook)
    void onFaceBookClick(View v) {
        btnFacebook_main.performClick();
    }


    /*Google sign in */
    private void onCerateInit() {


        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //********************************FACEBOOK

        callbackManager = CallbackManager.Factory.create();
        btnFacebook_main.setReadPermissions(Arrays.asList("public_profile", "email"));
        // Callback registration

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        btnFacebook_main.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);

                        if (bFacebookData != null) {
                            SHP.set(login.this, SHP.ids.signedact, "facebook");
                            email = bFacebookData.getString("email");
                            photo = bFacebookData.getString("profile_pic");
                            fname = bFacebookData.getString("first_name");
                            lname = bFacebookData.getString("last_name");
                            name = fname + " " + lname;
                            src = "facebook";
                            vtsLogin();
                        }

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }


    /*fill all controls*/
    private void initAllControls() {
        /*Edit text box*/
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        /*Button*/
        btnLogin = (Button) findViewById(R.id.btnLogin);

        /*Progress bar*/
        loader = new ProgressDialog(login.this);
        loader.setMessage("Login.. Please wait.");

        setClickListner();
        signOutGoogle();
        signOutFaceBook();

    }



    private void signOutGoogle() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            // Build a GoogleSignInClient with the options specified by gso.
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });
        } catch (Exception ex) {

        }
    }


    private void signOutFaceBook() {
        try {
            LoginManager.getInstance().logOut();
        } catch (Exception ex) {

        }
    }

    /*Set all click lisners*/
    private void setClickListner() {
        btnLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin: {
                if (!validate()) {
                    return;
                }
                SHP.set(login.this, SHP.ids.signedact, "normal");

                JsonObject json = new JsonObject();
                json.addProperty("email", edtUserName.getText().toString());
                json.addProperty("pwd", edtPassword.getText().toString());
                //json.addProperty("type", "driver");
                json.addProperty("otherdetails", "{}");
                json.addProperty("src", "m");
                Global.showProgress(loader);
                Ion.with(this)
                        .load(Global.urls.getlogin.value)
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                // do stuff with the result or error
                                try {
                                    if (result != null) Log.v("result", result.toString());
                                    // JSONObject jsnobject = new JSONObject(jsond);
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<model_loginusr>>() {
                                    }.getType();
                                    List<model_loginusr> login = (List<model_loginusr>) gson.fromJson(result.get("data"), listType);
                                    if (login.size() > 0) {
                                        Global.loginusr = login.get(0);
                                        if (Global.loginusr.getStatus() == 1) {
                                            SHP.set(login.this, SHP.ids.uid, Global.loginusr.getDriverid() + "");
                                            String g = Global.loginusr.getSessiondetails().toString();
                                            if (!g.equals("null")) {
                                                String s = ((LinkedTreeMap) Global.loginusr.getSessiondetails()).get("sessionid").toString();
                                                Global.loginusr.setSessiondetails(s.replace(".0", ""));
                                                SHP.set(login.this, SHP.ids.sessionid, Global.loginusr.getSessiondetails().toString());
                                            }

                                            //Toast.makeText(login.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(login.this, dash.class);
                                            startActivity(i);
                                            login.this.finish();
                                        } else {
                                            Toast.makeText(login.this, "Login Failed! " + Global.loginusr.getErrmsg(), Toast.LENGTH_SHORT).show();
                                            edtPassword.setText("");
                                        }
                                    } else {
                                        Toast.makeText(login.this, "Oops there is some issue! please login later!", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception ea) {
                                    Toast.makeText(login.this, "Error: " + ea.getMessage(), Toast.LENGTH_LONG).show();
                                    ea.printStackTrace();
                                }
                                Global.hideProgress(loader);

                            }
                        });

            }
            break;
            default:
                break;
        }

    }

    private void vtsLogin() {


        LoginClass c = new LoginClass();
        c.SetOnLoginCallback(new LoginClass.LoginCallBack() {
            @Override
            public void onDone(model_loginusr_vts user, int statusCode, String Error, boolean status) {
                if (statusCode == 2) {
                    registered_vts();
                } else if (statusCode == 1) {
                    Intent i = new Intent(login.this, dash.class);
                    startActivity(i);
                    login.this.finish();
                } else {

                }
            }
        });

        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("pwd", "");
        //json.addProperty("type", "driver");
        json.addProperty("otherdetails", "{}");
        json.addProperty("src", "android");

        c.Login(json, loader, this);


//
//        JsonObject json = new JsonObject();
//        json.addProperty("email", email);
//        json.addProperty("pwd", "");
//        //json.addProperty("type", "driver");
//        json.addProperty("otherdetails", "{}");
//        json.addProperty("src", "android");
//        Global.showProgress(loader);
//        Ion.with(this)
//                .load(Global.urls.login_vts.value)
//                .setJsonObjectBody(json)
//                .asJsonObject()
//                .setCallback(new FutureCallback<JsonObject>() {
//                    @Override
//                    public void onCompleted(Exception e, JsonObject result) {
//                        // do stuff with the result or error
//                        try {
//                            if (result != null) Log.v("result", result.toString());
//                            // JSONObject jsnobject = new JSONObject(jsond);
//                            int statusCode = result.get("statuscode").getAsInt();
//                            if (statusCode == 2) {
//                                registered_vts();
//                                return;
//                            }
//
//                            Gson gson = new Gson();
//                            Type listType = new TypeToken<model_loginusr_vts>() {
//                            }.getType();
//                            model_loginusr_vts login = gson.fromJson(result.get("data"), listType);
//                            model_loginusr lo = new model_loginusr();
//                            lo.setDriverid(login.getDriverid());
//                            lo.setEmail(login.getEmail());
//                            lo.setFullname(login.getFullname());
//                            lo.setSessiondetails(login.getSessiondetails() + "");
//                            lo.setStatus((result.get("status").getAsBoolean() ? 1 : 0));
//
//                            Global.loginusr = lo;
//
//
//                            if (Global.loginusr.getStatus() == 1) {
//                                SHP.set(login.this, SHP.ids.uid, Global.loginusr.getDriverid() + "");
//                                String g = Global.loginusr.getSessiondetails().toString();
//
//                                SHP.set(login.this, SHP.ids.sessionid, g);
//
//                                //Toast.makeText(login.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
//                                Intent i = new Intent(login.this, dash.class);
//                                startActivity(i);
//                                login.this.finish();
//                            } else {
//                                Toast.makeText(login.this, "Login Failed! " + Global.loginusr.getErrmsg(), Toast.LENGTH_SHORT).show();
//                                edtPassword.setText("");
//                            }
//
//                        } catch (Exception ea) {
//                            Toast.makeText(login.this, "Error: " + ea.getMessage(), Toast.LENGTH_LONG).show();
//                            ea.printStackTrace();
//                        }
//                        Global.hideProgress(loader);
//
//                    }
//
//
//                });
    }

    private void registered_vts() {
        Intent intent = new Intent(getApplicationContext(), registrationinfo.class);
        intent.putExtra("fname", fname);
        intent.putExtra("lname", lname);

        intent.putExtra("fullname", name);
        intent.putExtra("email", email);
        intent.putExtra("pic", photo);
        intent.putExtra("src", src);

        startActivity(intent);
        finish();
    }


    private boolean validate() {
        if (edtUserName.getText().toString().trim().equals("")) {
            edtUserName.setError("Required!");
            return false;
        } else if (edtPassword.getText().toString().trim().equals("")) {
            edtPassword.setError("Required!");
            return false;
        }
        edtUserName.setError(null);
        edtPassword.setError(null);
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        new Checker(this).pass(new Checker.Pass() {
            @Override
            public void pass() {

            }

        }).check(Checker.Resource.NETWORK);
    }

    @OnClick(R.id.btnGoogle)
    void onBtnGoogleClick(View v) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else if (requestCode == FACEBOOK_SIGN_IN) {

            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data);


        }
    }

    String email, photo, name, fname, lname, src;

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            SHP.set(login.this, SHP.ids.signedact, "google");
            email = account.getEmail();
            if (account.getPhotoUrl() != null)
                photo = "https://lh3.googleusercontent.com" + account.getPhotoUrl().getPath();
            name = account.getDisplayName();
            fname = account.getGivenName();
            lname = account.getFamilyName();
            src = "google";
            vtsLogin();
            // Signed in successfully, show authenticated UI.
            // updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            // updateUI(null);
        }
    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
           if(object == null)return null;
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException e) {
            Log.d(TAG, "Error parsing JSON");
        }
        return null;
    }
}


