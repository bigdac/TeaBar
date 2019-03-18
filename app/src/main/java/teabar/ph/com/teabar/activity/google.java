package teabar.ph.com.teabar.activity;


import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import teabar.ph.com.teabar.R;

public class google extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private GoogleApiClient mGoogleApiClient;
//    private SignInButton sign_in_button;
    private Button sign_in_button;
    private static int RC_SIGN_IN=10001;
    private TextView tv_1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glogin);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)/* FragmentActivity *//* OnConnectionFailedListener */
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        tv_1 = (TextView) findViewById(R.id.tv_1);
        sign_in_button = findViewById(R.id.sign_in_button);
//        sign_in_button.setSize(SignInButton.SIZE_STANDARD);
//        sign_in_button.setStyle(SignInButton.SIZE_STANDARD,SignInButton.COLOR_DARK);
//        sign_in_button.setScopes(gso.getScopeArray());
        sign_in_button.setOnClickListener(this);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void handleSignInResult(GoogleSignInResult result){
        Log.i("robin", "handleSignInResult:" + result.isSuccess());
        if(result.isSuccess()){
            Log.i("robin", "成功");
            GoogleSignInAccount acct = result.getSignInAccount();
            if(acct!=null){
                Log.i("robin", "用户名是:" + acct.getDisplayName());
                Log.i("robin", "用户email是:" + acct.getEmail());
                Log.i("robin", "用户头像是:" + acct.getPhotoUrl());
                Log.i("robin", "用户Id是:" + acct.getId());//之后就可以更新UI了
                Log.i("robin", "用户IdToken是:" + acct.getIdToken());
                tv_1.setText("用户名是:" + acct.getDisplayName()+"\n用户email是:" + acct.getEmail()+"\n用户头像是:" + acct.getPhotoUrl()+ "\n用户Id是:" + acct.getId()+"\n用户IdToken是:" + acct.getIdToken());
            }
        }else{
            tv_1.setText("登录失败");
            Log.i("robin", "没有成功"+result.getStatus());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("robin","google登录-->onConnected,bundle=="+bundle);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("robin","google登录-->onConnectionSuspended,i=="+i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("robin","google登录-->onConnectionFailed,connectionResult=="+connectionResult);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient!=null) mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient!=null&&mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("robin", "requestCode==" + requestCode + ",resultCode==" + resultCode + ",data==" + data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button: {
                Log.i("robin","点击了登录按钮");
                signIn();
                break;
            }
        }
    }
}

